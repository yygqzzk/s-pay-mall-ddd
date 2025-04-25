package com.yygqzzk.infrastructure.adapter.repository;

import com.yygqzzk.domain.order.adapter.repository.IOrderRepository;
import com.yygqzzk.domain.order.model.aggregate.CreateOrderAggregate;
import com.yygqzzk.domain.order.model.entity.OrderEntity;
import com.yygqzzk.domain.order.model.entity.ProductEntity;
import com.yygqzzk.domain.order.model.valobj.OrderStatusVO;
import com.yygqzzk.infrastructure.dao.IOrderDao;
import com.yygqzzk.infrastructure.dao.po.PayOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    public OrderEntity queryUnPayOrder(OrderEntity orderEntity) {
        PayOrder payOrder = PayOrder.builder()
                .userId(orderEntity.getUserId())
                .productId(orderEntity.getProductId())
                .build();

        payOrder = orderDao.queryUnPayOrder(payOrder);

        // 没有未支付订单，返回null
        if (payOrder == null) {
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
}




