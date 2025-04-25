package com.yygqzzk.domain.order.service;

import com.yygqzzk.domain.order.model.entity.OrderEntity;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
public interface IAlipayService {
    String creatPayUrl(OrderEntity unpaidOrder) throws Exception;
}
