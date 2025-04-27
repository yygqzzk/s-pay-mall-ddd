package com.yygqzzk.infrastructure.gateway;

import com.yygqzzk.domain.order.adapter.port.IProductPort;
import com.yygqzzk.domain.order.model.entity.ProductEntity;
import com.yygqzzk.infrastructure.gateway.dto.ProductDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@Component
public class ProductRPC {

    public ProductDTO queryProductByProductId(String productId) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(productId);
        productDTO.setProductName("美女程序员同款键盘");
        productDTO.setProductDesc("美女程序员同款键盘");
        productDTO.setPrice(new BigDecimal("9.99"));

        return productDTO;
    }
}




