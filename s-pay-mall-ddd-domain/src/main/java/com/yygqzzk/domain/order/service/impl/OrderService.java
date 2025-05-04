package com.yygqzzk.domain.order.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.google.common.eventbus.EventBus;
import com.yygqzzk.domain.order.adapter.event.PaySuccessMessageEvent;
import com.yygqzzk.domain.order.adapter.port.IProductPort;
import com.yygqzzk.domain.order.adapter.repository.IOrderRepository;
import com.yygqzzk.domain.order.model.aggregate.CreateOrderAggregate;
import com.yygqzzk.domain.order.model.entity.MarketPayDiscountEntity;
import com.yygqzzk.domain.order.model.entity.OrderEntity;
import com.yygqzzk.domain.order.model.entity.PayOrderEntity;
import com.yygqzzk.domain.order.model.valobj.MarketTypeVO;
import com.yygqzzk.domain.order.model.valobj.OrderStatusVO;
import com.yygqzzk.domain.order.service.AbstractOrderService;
import com.yygqzzk.domain.order.service.IAlipayService;
import com.yygqzzk.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@Component
@Slf4j
public class OrderService extends AbstractOrderService {

    @Value("${alipay.notify_url}")
    private String notifyUrl;
    @Value("${alipay.return_url}")
    private String returnUrl;

    protected final AlipayClient alipayClient;
    protected final IProductPort port;

    public OrderService(IAlipayService alipayService, IProductPort productPort, IOrderRepository orderRepository, PaySuccessMessageEvent paySuccessMessageEvent, EventBus eventBus, AlipayClient alipayClient, IProductPort port) {
        super(alipayService, productPort, orderRepository, paySuccessMessageEvent, eventBus);
        this.alipayClient = alipayClient;
        this.port = port;
    }


    @Override
    protected MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId) {
        return port.lockMarketPayOrder(userId, teamId, activityId, productId, orderId);
    }

    @Override
    protected PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount) throws AlipayApiException {
        return doPrepayOrder(userId, productId, productName, orderId, totalAmount, null);
    }

    @Override
    protected PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount, MarketPayDiscountEntity marketPayDiscountEntity) throws AlipayApiException {
        // 支付金额
        BigDecimal payAmount = null == marketPayDiscountEntity ? totalAmount : marketPayDiscountEntity.getPayPrice();

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", payAmount);
        bizContent.put("subject", productName);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());

        String form = alipayClient.pageExecute(request).getBody();

        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setOrderId(orderId);
        payOrderEntity.setPayUrl(form);
        payOrderEntity.setOrderStatus(OrderStatusVO.PAY_WAIT);

        // 营销信息
        payOrderEntity.setMarketType(null == marketPayDiscountEntity ? MarketTypeVO.NO_MARKET.getCode() : MarketTypeVO.GROUP_BUY_MARKET.getCode());
        payOrderEntity.setMarketDeductionAmount(null == marketPayDiscountEntity ? BigDecimal.ZERO : marketPayDiscountEntity.getDeductionPrice());
        payOrderEntity.setPayAmount(payAmount);

        orderRepository.updateOrderPayInfo(payOrderEntity);

        return payOrderEntity;
    }


    @Override
    public void doSaveOrder(CreateOrderAggregate orderAggregate) {
        orderRepository.doSaveOrder(orderAggregate);
    }


    @Override
    public void changeOrderPaySuccess(String orderId, Date payTime) {

        /* 查询对应订单 */
        OrderEntity orderEntity = orderRepository.queryOrderByOrderId(orderId);

        if(null == orderEntity)
            return;

        if(MarketTypeVO.GROUP_BUY_MARKET.getCode().equals(orderEntity.getMarketType())) {
            /* 改变订单支付状态 */
            orderRepository.changeMarketOrderPaySuccess(orderId);
            port.settlementMarketPayOrder(orderEntity.getUserId(), orderId, payTime);
            // 注意；在公司中，发起结算的http/rpc调用可能会失败，这个时候还会有增加job任务补偿。条件为，检查一笔走了拼团的订单，超过n分钟后，仍然没有做拼团结算状态变更。
            // 我们这里失败了，会抛异常，借助支付宝回调/job来重试。你可以单独实现一个独立的job来处理。

        } else {
            /* 改变订单支付状态 */
            orderRepository.changeOrderPaySuccess(orderId);
            /* 发送支付成功消息 */
            sendOrderPaySuccessEvent(orderEntity, payTime);
        }



    }

    public void sendOrderPaySuccessEvent(OrderEntity orderEntity, Date payTime) {

        // 发送MQ消息
        BaseEvent.EventMessage<PaySuccessMessageEvent.PaySuccessMessage> eventMessage = paySuccessMessageEvent.buildEventMessage(PaySuccessMessageEvent.PaySuccessMessage.builder().userId(orderEntity.getUserId())
                .tradeNo(orderEntity.getOrderId())
                .payTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(payTime))
                .productName(orderEntity.getProductName())
                .totalAmount(orderEntity.getPayAmount().toString())
                .build());
        PaySuccessMessageEvent.PaySuccessMessage paySuccessMessage = eventMessage.getData();

        eventBus.post(JSON.toJSONString(paySuccessMessage));
    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return orderRepository.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return orderRepository.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return orderRepository.changeOrderClose(orderId);
    }

    @Override
    public void changeOrderMarketSettlement(List<String> outTradeNoList) {
        // 更新拼团结算状态
        orderRepository.changeOrderMarketSettlement(outTradeNoList);
        // 循环成功发送消息 - 一般在公司的场景里，还会有job任务扫描超时没有结算的订单，查询订单状态。查询对方服务端的接口，会被限制一次查询多少，频次多少。
        outTradeNoList.forEach(orderId -> {
            OrderEntity orderEntity = orderRepository.queryOrderByOrderId(orderId);
            sendOrderPaySuccessEvent(orderEntity, new Date());
        });



    }




}




