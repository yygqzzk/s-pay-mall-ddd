package com.yygqzzk.domain.order.adapter.port;

import java.io.IOException;
import java.util.Map;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/26
 */
public interface IWeixinMessagePort {
    public void sendPaySuccessTemplate(Map<String, String> info) throws IOException;
}
