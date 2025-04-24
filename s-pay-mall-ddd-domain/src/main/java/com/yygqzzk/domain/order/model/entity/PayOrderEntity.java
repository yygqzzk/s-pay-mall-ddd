package com.yygqzzk.domain.order.model.entity;

import com.yygqzzk.domain.order.model.valobj.OrderStatusVO;
import com.yygqzzk.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayOrderEntity {

    private String userId;
    private String orderId;
    private String payUrl;
    private OrderStatusVO orderStatusVO;

}
