package com.yygqzzk.api;

import com.yygqzzk.api.response.Response;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 * @description 用户身份校验
 */
public interface IAuthService {
    public Response<String> weixinQrCodeTicket();
    Response<String> checkLogin(String ticket);
}
