package com.yygqzzk.infrastructure.adapter.port;

import com.google.common.cache.Cache;
import com.yygqzzk.domain.auth.adapter.port.ILoginPort;
import com.yygqzzk.infrastructure.gateway.IWeixinApiService;
import com.yygqzzk.infrastructure.gateway.dto.WeixinQrCodeRequestDTO;
import com.yygqzzk.infrastructure.gateway.dto.WeixinQrCodeResponseDTO;
import com.yygqzzk.infrastructure.gateway.dto.WeixinTemplateMessageDTO;
import com.yygqzzk.infrastructure.gateway.dto.WeixinTokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@Service
@RequiredArgsConstructor
public class LoginPort implements ILoginPort {

    @Value("${weixin.config.app-id}")
    String appid;
    @Value("${weixin.config.app-secret}")
    String appSecret;
    @Value("${weixin.config.template_id}")
    String templateId;

    private final Cache<String, String> weixinAccessToken;

    private final IWeixinApiService weixinApiService;


    @Override
    public String createQrCodeTicket() throws Exception {
        // 1. 获取 accessToken
        // 先查本地缓存
        String accessToken = weixinAccessToken.getIfPresent(appid);
        if (null == accessToken) {
            // 本地缓存不存在，请求weixin api获取accessToken
            Call<WeixinTokenResponseDTO> call = weixinApiService.getToken("client_credential", appid, appSecret);
            WeixinTokenResponseDTO weixinTokenRes = call.execute().body();
            assert weixinTokenRes != null;
            accessToken = weixinTokenRes.getAccess_token();
            // 存入本地缓存
            weixinAccessToken.put(appid, accessToken);
        }

        // 生成 ticket
        WeixinQrCodeRequestDTO weixinQrCodeReq = WeixinQrCodeRequestDTO.builder()
                .expire_seconds(2592000)
                .action_name(WeixinQrCodeRequestDTO.ActionNameTypeVO.QR_SCENE.getCode())
                .action_info(WeixinQrCodeRequestDTO.ActionInfo.builder()
                        .scene(WeixinQrCodeRequestDTO.ActionInfo.Scene.builder()
                                .scene_id(100601).build())
                        .build())
                .build();
        Call<WeixinQrCodeResponseDTO> call = weixinApiService.createQrCode(accessToken, weixinQrCodeReq);
        WeixinQrCodeResponseDTO weixinQrCodeRes = call.execute().body();
        assert null != weixinQrCodeRes;

        return weixinQrCodeRes.getTicket();
    }

    @Override
    public void sendLoginTemplate(String ticket, String openid) throws IOException {
        // 1. 获取 accessToken 【实际业务场景，按需处理下异常】
        String accessToken = weixinAccessToken.getIfPresent(appid);
        if (null == accessToken){
            Call<WeixinTokenResponseDTO> call = weixinApiService.getToken("client_credential", appid, appSecret);
            WeixinTokenResponseDTO weixinTokenRes = call.execute().body();
            assert weixinTokenRes != null;
            accessToken = weixinTokenRes.getAccess_token();
            weixinAccessToken.put(appid, accessToken);
        }

        // 2. 发送模板消息
        Map<String, Map<String, String>> data = new HashMap<>();
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.USER, openid);

        WeixinTemplateMessageDTO templateMessageDTO = new WeixinTemplateMessageDTO(openid, templateId);
        templateMessageDTO.setUrl("https://github.com/yygqzzk");
        templateMessageDTO.setData(data);

        Call<Void> call = weixinApiService.sendMessage(accessToken, templateMessageDTO);
        call.execute();
    }
}




