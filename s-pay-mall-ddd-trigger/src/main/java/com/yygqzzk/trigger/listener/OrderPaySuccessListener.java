package com.yygqzzk.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.google.common.eventbus.Subscribe;
import com.yygqzzk.domain.order.service.IWeixinMessageService;
import com.yygqzzk.domain.order.service.impl.OrderService;
import com.yygqzzk.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/22
 */
@Slf4j
@Component
public class OrderPaySuccessListener {
    @Resource
    private IWeixinMessageService weixinMessageService;

    @Subscribe
    public void handleEvent(String paySuccessMessage) throws IOException {
        log.info("收到支付成功消息，可以做接下来的事情，如；发货、充值、开户员、返利 {}", paySuccessMessage);
        Map<String, String> msg = JSON.parseObject(paySuccessMessage, Map.class);
        weixinMessageService.sendPaySuccessTemplate(msg);
    }
}




