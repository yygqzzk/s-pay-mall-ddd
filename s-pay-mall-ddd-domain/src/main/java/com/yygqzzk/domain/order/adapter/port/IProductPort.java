package com.yygqzzk.domain.order.adapter.port;

import com.yygqzzk.domain.order.model.entity.ProductEntity;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
public interface IProductPort {
    ProductEntity queryProductByProductId(String productId);

}
