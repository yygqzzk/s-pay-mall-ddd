package com.yygqzzk.domain.order.service;

import com.yygqzzk.domain.order.model.entity.PayOrderEntity;
import com.yygqzzk.domain.order.model.entity.ShopCartEntity;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/22
 * description 订单服务接口
 */
public interface IOrderService {
    PayOrderEntity createOrder(ShopCartEntity shopCartReq) throws  Exception;

    /**
     * 改变订单状态为已支付
     * @param orderId
     */
    void changeOrderPaySuccess(String orderId, Date payTime);


    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    /**
     * 改变订单状态为已关闭
     * @return
     */
    boolean changeOrderClose(String orderId);


    void changeOrderMarketSettlement(List<String> outTradeNoList);
}
