package com.yygqzzk.domain.order.model.aggregate;

import com.yygqzzk.domain.order.model.entity.OrderEntity;
import com.yygqzzk.domain.order.model.entity.ProductEntity;
import com.yygqzzk.domain.order.model.valobj.OrderStatusVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateOrderAggregate {
    String userId;
    OrderEntity orderEntity;
    ProductEntity productEntity;

    public static OrderEntity buildOrderEntity(String productId, String productName, Integer marketType) {
        return OrderEntity.builder()
                .productId(productId)
                .productName(productName)
                .orderId(RandomStringUtils.randomNumeric(12))
                .orderTime(new Date())
                .orderStatusVO(OrderStatusVO.CREATE)
                .marketType(marketType)
                .build();
    }
}




