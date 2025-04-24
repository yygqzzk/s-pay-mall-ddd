package com.yygqzzk.infrastructure.adapter.port;

import com.yygqzzk.domain.order.adapter.port.IProductPort;
import com.yygqzzk.domain.order.model.entity.ProductEntity;
import com.yygqzzk.infrastructure.gateway.ProductRPC;
import com.yygqzzk.infrastructure.gateway.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@RequiredArgsConstructor
@Component
public class ProductPort implements IProductPort {
    private final ProductRPC productRPC;
    @Override
    public ProductEntity queryProductByProductId(String productId) {
        ProductDTO productDTO = productRPC.queryProductByProductId(productId);
        ProductEntity productEntity = ProductEntity.builder().build();
        BeanUtils.copyProperties(productDTO, productEntity);
        return productEntity;
    }
}




