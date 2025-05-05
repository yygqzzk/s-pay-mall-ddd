package com.yygqzzk.domain.order.adapter.port;

import com.yygqzzk.domain.order.model.entity.MarketPayDiscountEntity;
import com.yygqzzk.domain.order.model.entity.ProductEntity;
import com.yygqzzk.types.exception.AppException;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
public interface IProductPort {
    ProductEntity queryProductByProductId(String productId);

    MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId) throws AppException;


    void settlementMarketPayOrder(String userId, String orderId, Date orderTime);

}
