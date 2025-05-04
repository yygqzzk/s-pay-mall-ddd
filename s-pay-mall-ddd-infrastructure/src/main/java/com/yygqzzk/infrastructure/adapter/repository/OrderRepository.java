package com.yygqzzk.infrastructure.adapter.repository;

import com.alibaba.fastjson2.JSON;
import com.google.common.eventbus.EventBus;
import com.yygqzzk.domain.order.adapter.event.PaySuccessMessageEvent;
import com.yygqzzk.domain.order.adapter.repository.IOrderRepository;
import com.yygqzzk.domain.order.model.aggregate.CreateOrderAggregate;
import com.yygqzzk.domain.order.model.entity.OrderEntity;
import com.yygqzzk.domain.order.model.entity.PayOrderEntity;
import com.yygqzzk.domain.order.model.entity.ProductEntity;
import com.yygqzzk.domain.order.model.entity.ShopCartEntity;
import com.yygqzzk.domain.order.model.valobj.OrderStatusVO;
import com.yygqzzk.infrastructure.dao.IOrderDao;
import com.yygqzzk.infrastructure.dao.po.PayOrder;
import com.yygqzzk.types.event.BaseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@Component
@RequiredArgsConstructor
public class OrderRepository implements IOrderRepository {
    private final IOrderDao orderDao;


    @Override
    public OrderEntity queryUnPayOrder(ShopCartEntity shopCartEntity) {
        // 1. 封装参数
        PayOrder orderReq = new PayOrder();
        orderReq.setUserId(shopCartEntity.getUserId());
        orderReq.setProductId(shopCartEntity.getProductId());

        // 2. 查询到订单
        PayOrder order = orderDao.queryUnPayOrder(orderReq);
        if (null == order) return null;

        // 3. 返回结果
        return OrderEntity.builder()
                .productId(order.getProductId())
                .productName(order.getProductName())
                .orderId(order.getOrderId())
                .orderStatusVO(OrderStatusVO.valueOf(order.getStatus()))
                .orderTime(order.getOrderTime())
                .totalAmount(order.getTotalAmount())
                .payUrl(order.getPayUrl())
                .marketType(order.getMarketType())
                .marketDeductionAmount(order.getMarketDeductionAmount())
                .payAmount(order.getPayAmount())
                .build();
    }

    @Override
    public PayOrderEntity queryPayOrderByOrderId(String orderId) {
        PayOrder payOrder = orderDao.queryByOrderId(orderId);
        return PayOrderEntity.builder()
                .userId(payOrder.getUserId())
                .orderId(payOrder.getOrderId())
                .payUrl(payOrder.getPayUrl())
                .marketType(payOrder.getMarketType())
                .marketDeductionAmount(payOrder.getMarketDeductionAmount())
                .payAmount(payOrder.getPayAmount())
                .build();
    }

    @Override
    public OrderEntity queryOrderByOrderId(String orderId) {
        PayOrderEntity payOrderEntity = queryPayOrderByOrderId(orderId);
        return OrderEntity.builder()
                .userId(payOrderEntity.getUserId())
                .orderId(payOrderEntity.getOrderId())
                .payUrl(payOrderEntity.getPayUrl())
                .marketType(payOrderEntity.getMarketType())
                .marketDeductionAmount(payOrderEntity.getMarketDeductionAmount())
                .payAmount(payOrderEntity.getPayAmount())
                .build();
    }

    @Override
    public void changeMarketOrderPaySuccess(String orderId) {
        PayOrder payOrder = PayOrder.builder()
                .orderId(orderId)
                .status(OrderStatusVO.PAY_SUCCESS.getCode())
                .build();
        orderDao.changeOrderPaySuccess(payOrder);
    }

    @Override
    public void changeOrderMarketSettlement(List<String> outTradeNoList) {
        orderDao.changeOrderMarketSettlement(outTradeNoList);
    }

    @Override
    public void doSaveOrder(CreateOrderAggregate orderAggregate) {
        OrderEntity orderEntity = orderAggregate.getOrderEntity();
        ProductEntity productEntity = orderAggregate.getProductEntity();

        orderDao.insert(PayOrder.builder()
                .userId(orderAggregate.getUserId())
                .productId(productEntity.getProductId())
                .productName(productEntity.getProductName())
                .orderId(orderEntity.getOrderId())
                .totalAmount(productEntity.getPrice())
                .orderTime(new Date())
                .status(OrderStatusVO.CREATE.getCode())
                        .marketType(orderEntity.getMarketType())
                        .marketDeductionAmount(orderEntity.getMarketDeductionAmount())
                        .payAmount(orderEntity.getPayAmount())
                .build());
    }

    @Override
    public void updateOrderPayInfo(PayOrderEntity payOrderEntity) {
        PayOrder payOrderReq = PayOrder.builder()
                .userId(payOrderEntity.getUserId())
                .orderId(payOrderEntity.getOrderId())
                .status(payOrderEntity.getOrderStatus().getCode())
                .payUrl(payOrderEntity.getPayUrl())
                .marketType(payOrderEntity.getMarketType())
                .marketDeductionAmount(payOrderEntity.getMarketDeductionAmount())
                .payAmount(payOrderEntity.getPayAmount())
                .build();
        orderDao.updateOrderPayInfo(payOrderReq);
    }



    @Override
    public void changeOrderPaySuccess(String orderId) {
        PayOrder payOrder = PayOrder.builder()
                .orderId(orderId)
                .status(OrderStatusVO.PAY_SUCCESS.getCode())
                .build();
        orderDao.changeOrderPaySuccess(payOrder);
    }


    @Override
    public List<String> queryNoPayNotifyOrder() {
        return orderDao.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return orderDao.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return orderDao.changeOrderClose(orderId);
    }


}




