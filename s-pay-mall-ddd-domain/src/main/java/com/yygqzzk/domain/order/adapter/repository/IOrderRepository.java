package com.yygqzzk.domain.order.adapter.repository;

import com.yygqzzk.domain.order.model.aggregate.CreateOrderAggregate;
import com.yygqzzk.domain.order.model.entity.OrderEntity;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
public interface IOrderRepository {
    OrderEntity queryUnPayOrder(OrderEntity orderEntity);

    void doSaveOrder(CreateOrderAggregate orderAggregate);
}
