package com.yygqzzk.domain.auth.service;

import java.io.IOException;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/22
 */
public interface ILoginService {

    String createQrCodeTicket() throws Exception;

    String checkLogin(String ticket);

    void saveLoginState(String ticket, String openid) throws IOException;

    void saveLoginIpinfo(String ticket, String ip) throws IOException;



}
