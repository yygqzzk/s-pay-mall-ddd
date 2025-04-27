package com.yygqzzk.domain.order.service.impl;

import com.google.common.cache.Cache;
import com.yygqzzk.domain.order.adapter.port.IWeixinMessagePort;
import com.yygqzzk.domain.order.service.IWeixinMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/26
 */
@Component
@RequiredArgsConstructor
public class WeixinMeesageService implements IWeixinMessageService {
    private final IWeixinMessagePort weixinMessagePort;

    @Override
    public void sendPaySuccessTemplate(Map<String, String> info) throws IOException {
        weixinMessagePort.sendPaySuccessTemplate(info);
    }
}




