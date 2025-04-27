package com.yygqzzk.domain.order.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.eventbus.EventBus;
import com.yygqzzk.domain.order.adapter.event.PaySuccessMessageEvent;
import com.yygqzzk.domain.order.adapter.port.IProductPort;
import com.yygqzzk.domain.order.adapter.repository.IOrderRepository;
import com.yygqzzk.domain.order.model.aggregate.CreateOrderAggregate;
import com.yygqzzk.domain.order.model.entity.OrderEntity;
import com.yygqzzk.domain.order.model.valobj.OrderStatusVO;
import com.yygqzzk.domain.order.service.AbstractOrderService;
import com.yygqzzk.domain.order.service.IAlipayService;
import com.yygqzzk.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@Component
@Slf4j
public class OrderService extends AbstractOrderService {


    public OrderService(IAlipayService alipayService, IProductPort productPort, IOrderRepository orderRepository, PaySuccessMessageEvent paySuccessMessageEvent, EventBus eventBus) {
        super(alipayService, productPort, orderRepository, paySuccessMessageEvent, eventBus);
    }

    @Override
    public OrderEntity doPrepayOrder(OrderEntity orderEntity) throws Exception {
        String form = alipayService.creatPayUrl(orderEntity);
        orderEntity.setStatus(OrderStatusVO.PAY_WAIT.getCode());
        orderEntity.setPayUrl(form);
        orderRepository.updateOrderPayInfo(orderEntity);
        return orderEntity;
    }

    @Override
    public void doSaveOrder(CreateOrderAggregate orderAggregate) {
        orderRepository.doSaveOrder(orderAggregate);
    }


    @Override
    public void changeOrderPaySuccess(String orderId) {

        orderRepository.changeOrderPaySuccess(orderId);

    }

    @Override
    public void sendOrderPaySuccessEvent(Map<String, String> params) {
        OrderEntity orderEntity = orderRepository.queryByOrderId(params.get("out_trade_no"));
        // 发送MQ消息
        BaseEvent.EventMessage<PaySuccessMessageEvent.PaySuccessMessage> eventMessage = paySuccessMessageEvent.buildEventMessage(PaySuccessMessageEvent.PaySuccessMessage.builder()
                .userId(orderEntity.getUserId())
                .tradeNo(params.get("out_trade_no"))
                .payTime(params.get("gmt_payment"))
                .productName(params.get("subject"))
                .totalAmount(params.get("total_amount"))
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


}




