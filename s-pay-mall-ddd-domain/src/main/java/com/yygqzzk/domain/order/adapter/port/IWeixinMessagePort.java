package com.yygqzzk.domain.order.adapter.port;

import com.yygqzzk.domain.order.adapter.event.PaySuccessMessageEvent;

import java.io.IOException;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/26
 */
public interface IWeixinMessagePort {
    void sendPaySuccessTemplate(PaySuccessMessageEvent.PaySuccessMessage paySuccessMessage) throws IOException;

}
