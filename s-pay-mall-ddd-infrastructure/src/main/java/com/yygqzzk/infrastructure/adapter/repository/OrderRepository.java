package com.yygqzzk.infrastructure.adapter.repository;

import com.alibaba.fastjson2.JSON;
import com.google.common.eventbus.EventBus;
import com.yygqzzk.domain.order.adapter.event.PaySuccessMessageEvent;
import com.yygqzzk.domain.order.adapter.repository.IOrderRepository;
import com.yygqzzk.domain.order.model.aggregate.CreateOrderAggregate;
import com.yygqzzk.domain.order.model.entity.OrderEntity;
import com.yygqzzk.domain.order.model.entity.ProductEntity;
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
    public OrderEntity queryUnPayOrder(String userId, String productId) {
        PayOrder payOrder = PayOrder.builder()
                .userId(userId)
                .productId(productId)
                .build();

        payOrder = orderDao.queryUnPayOrder(payOrder);

        // 没有未支付订单、订单付款已完成、订单已关闭，返回null
        if (payOrder == null || OrderStatusVO.PAY_SUCCESS.getCode().equals(payOrder.getStatus()) ||
                OrderStatusVO.CLOSE.getCode().equals(payOrder.getStatus())) {
            return null;
        }

        OrderEntity order = OrderEntity.builder().build();
        BeanUtils.copyProperties(payOrder, order);
        return order;
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
                .build());
    }

    @Override
    public void updateOrderPayInfo(OrderEntity orderEntity) {
        PayOrder payOrder = PayOrder.builder().build();
        BeanUtils.copyProperties(orderEntity, payOrder);

        orderDao.updateOrderPayInfo(payOrder);
    }

    @Override
    public OrderEntity queryByOrderId(String orderId) {
        PayOrder payOrder = orderDao.queryByOrderId(orderId);
        OrderEntity orderEntity = OrderEntity.builder().build();
        BeanUtils.copyProperties(payOrder, orderEntity);
        return orderEntity;
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




