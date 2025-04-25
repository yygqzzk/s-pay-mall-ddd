package com.yygqzzk.domain.order.adapter.repository;

import com.yygqzzk.domain.order.model.aggregate.CreateOrderAggregate;
import com.yygqzzk.domain.order.model.entity.OrderEntity;

import java.util.List;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
public interface IOrderRepository {

    void doSaveOrder(CreateOrderAggregate orderAggregate);

    void updateOrderPayInfo(OrderEntity orderEntity);

    void changeOrderPaySuccess(String orderId);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);

    OrderEntity queryUnPayOrder(String userId, String productId);
}
