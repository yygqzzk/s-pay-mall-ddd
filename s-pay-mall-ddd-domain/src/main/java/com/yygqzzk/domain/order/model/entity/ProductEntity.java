package com.yygqzzk.domain.order.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {
    /** 商品ID */
    private String productId;
    /** 商品名称 */
    private String productName;
    /** 商品描述 */
    private String productDesc;
    /** 商品价格 */
    private BigDecimal price;
}




