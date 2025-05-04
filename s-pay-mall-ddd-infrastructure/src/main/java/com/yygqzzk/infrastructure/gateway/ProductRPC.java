package com.yygqzzk.infrastructure.gateway;

import com.yygqzzk.domain.order.adapter.port.IProductPort;
import com.yygqzzk.domain.order.model.entity.ProductEntity;
import com.yygqzzk.infrastructure.gateway.dto.ProductDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

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
        productDTO.setProductName("MyBatisBook");
        productDTO.setProductDesc("MyBatisBook");
        productDTO.setPrice(new BigDecimal("100.00"));
        return productDTO;
    }
}




