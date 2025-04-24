package com.yygqzzk.test.domain;

import com.yygqzzk.domain.order.model.entity.PayOrderEntity;
import com.yygqzzk.domain.order.model.entity.ProductEntity;
import com.yygqzzk.domain.order.model.entity.ShopCartEntity;
import com.yygqzzk.domain.order.service.IOrderService;
import com.yygqzzk.infrastructure.adapter.port.ProductPort;
import com.yygqzzk.infrastructure.gateway.ProductRPC;
import com.yygqzzk.infrastructure.gateway.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@SpringBootTest
@Slf4j
public class OrderServiceTest {
    @Autowired
    private IOrderService orderService;

    @Autowired
    private ProductPort productPort;

    @Test
    public void createOrderTest() throws Exception {
        ProductEntity productEntity = productPort.queryProductByProductId("1");
        ShopCartEntity shopCartEntity = ShopCartEntity.builder()
                .productId(productEntity.getProductId())
                .userId("yygqzzk")
                .build();
        PayOrderEntity order = orderService.createOrder(shopCartEntity);
        log.info(order.toString());
    }
}




