package com.yygqzzk.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayOrder {

    private Long id;
    private String userId;
    private String productId;
    private String productName;
    private String orderId;
    private Date orderTime;
    private BigDecimal totalAmount;
    private String status;
    private String payUrl;
    private Date payTime;
    /* 营销类型 */
    private Integer marketType;
    /* 营销金额: 优惠金额*/
    private BigDecimal marketDeductionAmount;
    /* 支付金额 */
    private BigDecimal payAmount;

    private Date createTime;
    private Date updateTime;

}
