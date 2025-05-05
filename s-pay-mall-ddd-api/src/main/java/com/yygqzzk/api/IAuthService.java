package com.yygqzzk.api;

import com.yygqzzk.api.response.Response;

import java.io.IOException;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 * @description 用户身份校验
 */
public interface IAuthService {
    Response<String> weixinQrCodeTicket() throws Exception;
    /* 根据指纹创建ticket */
    Response<String> weixinQrCodeTicket(String sceneStr) throws Exception;


    Response<String> checkLogin(String ticket) throws IOException;

    /* 指纹登录验证 */
    Response<String> checkLogin(String ticket, String sceneStr) throws IOException;
}
