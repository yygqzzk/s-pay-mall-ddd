package com.yygqzzk.domain.order.adapter.repository;

import com.yygqzzk.domain.order.model.aggregate.CreateOrderAggregate;
import com.yygqzzk.domain.order.model.entity.OrderEntity;
import com.yygqzzk.domain.order.model.entity.PayOrderEntity;
import com.yygqzzk.domain.order.model.entity.ShopCartEntity;

import java.util.List;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
public interface IOrderRepository {

    void doSaveOrder(CreateOrderAggregate orderAggregate);

    void updateOrderPayInfo(PayOrderEntity payOrderEntity);

    void changeOrderPaySuccess(String orderId);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);

    OrderEntity queryUnPayOrder(ShopCartEntity shopCartEntity);

    PayOrderEntity queryPayOrderByOrderId(String orderId);
}
