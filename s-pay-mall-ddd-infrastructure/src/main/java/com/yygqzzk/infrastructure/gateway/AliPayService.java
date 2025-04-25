package com.yygqzzk.infrastructure.gateway;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.yygqzzk.domain.order.model.entity.OrderEntity;
import com.yygqzzk.domain.order.service.IAlipayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@Component
public class AliPayService implements IAlipayService {

    @Resource
    private AlipayClient aliPayClient;


    @Value("${alipay.notify_url}")
    private String notifyUrl;

    @Value("${alipay.return_url}")
    private String returnUrl;

    @Override
    public String creatPayUrl(OrderEntity unpaidOrder) throws Exception {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", unpaidOrder.getOrderId());
        bizContent.put("total_amount", unpaidOrder.getTotalAmount().toString());
        bizContent.put("subject", unpaidOrder.getProductName());
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());

        return aliPayClient.pageExecute(request).getBody();
    }
}




