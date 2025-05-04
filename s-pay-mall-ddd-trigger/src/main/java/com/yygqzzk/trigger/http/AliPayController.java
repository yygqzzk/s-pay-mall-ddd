package com.yygqzzk.trigger.http;

import com.alibaba.fastjson2.JSON;
import com.alipay.api.internal.util.AlipaySignature;
import com.google.common.eventbus.EventBus;
import com.yygqzzk.api.IPayService;
import com.yygqzzk.api.dto.CreatePayRequestDTO;
import com.yygqzzk.api.response.Response;
import com.yygqzzk.domain.order.adapter.event.PaySuccessMessageEvent;
import com.yygqzzk.domain.order.model.entity.PayOrderEntity;
import com.yygqzzk.domain.order.model.entity.ShopCartEntity;
import com.yygqzzk.domain.order.service.IOrderService;
import com.yygqzzk.types.common.Constants;
import com.yygqzzk.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import xyz.yygqzzk.api.dto.NotifyRequestDTO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/25
 */

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/alipay/")
public class AliPayController implements IPayService {
    @Value("${alipay.alipay_public_key}")
    private String alipayPublicKey;

    @Resource
    private IOrderService orderService;


    @RequestMapping(value = "create_pay_order", method =  RequestMethod.POST)
    @Override
    public Response<String> createPayOrder(@RequestBody CreatePayRequestDTO createPayRequestDTO) {
        try {
            log.info("商品下单，根据商品ID创建支付单开始 userId:{} productId:{}", createPayRequestDTO.getUserId(), createPayRequestDTO.getUserId());
            String userId = createPayRequestDTO.getUserId();
            String productId = createPayRequestDTO.getProductId();
            // 下单
            PayOrderEntity payOrderEntity = orderService.createOrder(ShopCartEntity.builder()
                    .userId(userId)
                    .productId(productId)
                    .build());
            log.info("商品下单，支付订单创建完成 userId:{} productId:{}", createPayRequestDTO.getUserId(), createPayRequestDTO.getUserId());
            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(payOrderEntity.getPayUrl())
                    .build();
        } catch (Exception e) {
            log.error("商品下单，根据商品ID创建支付单失败 userId:{} productId:{}", createPayRequestDTO.getUserId(), createPayRequestDTO.getUserId(), e);
            return Response.<String>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "alipay_notify_url", method = RequestMethod.POST)
    public String payNotify(HttpServletRequest request) throws Exception {
        log.info("支付回调，消息接收 {}", request.getParameter("trade_status"));

        if (!request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            return "false";
        }

        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            params.put(name, request.getParameter(name));
        }

        String tradeNo = params.get("out_trade_no");
        String gmtPayment = params.get("gmt_payment");
        String alipayTradeNo = params.get("trade_no");

        String sign = params.get("sign");
        String content = AlipaySignature.getSignCheckContentV1(params);
        boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, alipayPublicKey, "UTF-8"); // 验证签名
        // 支付宝验签
        if (!checkSignature) {
            return "false";
        }

        // 验签通过
        log.info("支付回调，交易名称: {}", params.get("subject"));
        log.info("支付回调，交易状态: {}", params.get("trade_status"));
        log.info("支付回调，支付宝交易凭证号: {}", params.get("trade_no"));
        log.info("支付回调，商户订单号: {}", params.get("out_trade_no"));
        log.info("支付回调，交易金额: {}", params.get("total_amount"));
        log.info("支付回调，买家在支付宝唯一id: {}", params.get("buyer_id"));
        log.info("支付回调，买家付款时间: {}", params.get("gmt_payment"));
        log.info("支付回调，买家付款金额: {}", params.get("buyer_pay_amount"));
        log.info("支付回调，支付回调，更新订单 {}", tradeNo);
        orderService.changeOrderPaySuccess(tradeNo, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(params.get("gmt_payment")));

        return "success";
    }


    /* 拼团完成结算回调 */
    @RequestMapping(value = "group_buy_notify", method = RequestMethod.POST)
    @Override
    public String groupBuyNotify(@RequestBody NotifyRequestDTO requestDTO) {
        log.info("拼团回调，组队完成，结算开始 {}", JSON.toJSONString(requestDTO));
        try {
            // 营销结算
            orderService.changeOrderMarketSettlement(requestDTO.getOutTradeNoList());

            return "success";
        } catch (Exception e) {
            log.error("拼团回调，组队完成，结算失败 {}, {}", JSON.toJSONString(requestDTO), e.getMessage());

            return "error";
        }
    }
}




