package com.yygqzzk.domain.order.service;

import java.io.IOException;
import java.util.Map;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/26
 */
public interface IWeixinMessageService {
    void sendPaySuccessTemplate(Map<String,String> info) throws IOException;
}
