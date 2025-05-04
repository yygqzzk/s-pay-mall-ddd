package com.yygqzzk.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.google.common.eventbus.Subscribe;
import com.yygqzzk.domain.goods.service.IGoodsService;
import com.yygqzzk.domain.order.adapter.event.PaySuccessMessageEvent;
import com.yygqzzk.domain.order.adapter.port.IWeixinMessagePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/22
 */
@Slf4j
@Component
public class OrderPaySuccessListener {
    @Resource
    private IWeixinMessagePort weixinMessagePort;
    @Resource
    private IGoodsService goodsService;

    @Subscribe
    public void handleEvent(String paySuccessMessageJson) throws IOException {
        log.info("收到支付成功消息:{}", paySuccessMessageJson);
        PaySuccessMessageEvent.PaySuccessMessage paySuccessMessage = JSON.parseObject(paySuccessMessageJson, PaySuccessMessageEvent.PaySuccessMessage.class);

        log.info("模拟发货（如；发货、充值、开户员、返利），单号:{}", paySuccessMessage.getTradeNo());
        /* 微信消息通知 */
        weixinMessagePort.sendPaySuccessTemplate(paySuccessMessage);

        // 变更订单状态 - 发货完成&结算
        goodsService.changeOrderDealDone(paySuccessMessage.getTradeNo());


    }
}




