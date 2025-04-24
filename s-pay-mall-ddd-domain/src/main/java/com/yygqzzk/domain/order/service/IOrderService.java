package com.yygqzzk.domain.order.service;

import com.yygqzzk.domain.order.model.entity.PayOrderEntity;
import com.yygqzzk.domain.order.model.entity.ShopCartEntity;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/22
 * description 订单服务接口
 */
public interface IOrderService {
    PayOrderEntity createOrder(ShopCartEntity shopCartReq) throws  Exception;
}
