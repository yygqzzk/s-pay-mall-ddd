package com.yygqzzk.domain.auth.adapter.port;

import java.io.IOException;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
public interface ILoginPort {
    String createQrCodeTicket() throws Exception;


    void sendLoginTemplate(String ticket, String openid) throws IOException;

}
