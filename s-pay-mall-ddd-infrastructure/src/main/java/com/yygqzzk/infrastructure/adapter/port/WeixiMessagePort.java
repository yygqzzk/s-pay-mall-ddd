package com.yygqzzk.infrastructure.adapter.port;

import com.google.common.cache.Cache;
import com.yygqzzk.domain.order.adapter.port.IWeixinMessagePort;
import com.yygqzzk.infrastructure.gateway.IWeixinApiService;
import com.yygqzzk.infrastructure.gateway.dto.WeixinPayTemplateMessageDTO;
import com.yygqzzk.infrastructure.gateway.dto.WeixinTemplateMessageDTO;
import com.yygqzzk.infrastructure.gateway.dto.WeixinTokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Call;

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
public class WeixiMessagePort implements IWeixinMessagePort {
    @Value("${weixin.config.app-id}")
    String appid;
    @Value("${weixin.config.app-secret}")
    String appSecret;
    @Value("${weixin.config.pay_template_id}")
    String templateId;

    private final Cache<String, String> weixinAccessToken;

    private final IWeixinApiService weixinApiService;

    @Override
    public void sendPaySuccessTemplate(Map<String, String> info) throws IOException {
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
        WeixinTemplateMessageDTO.put(data, WeixinPayTemplateMessageDTO.TemplateKey.SUBJECT, info.get("productName"));
        WeixinTemplateMessageDTO.put(data, WeixinPayTemplateMessageDTO.TemplateKey.AMOUNT, info.get("totalAmount"));
        WeixinTemplateMessageDTO.put(data, WeixinPayTemplateMessageDTO.TemplateKey.PAYTIME, info.get("payTime"));

        WeixinTemplateMessageDTO templateMessageDTO = new WeixinPayTemplateMessageDTO(info.get("userId"), templateId);
        templateMessageDTO.setUrl("http://yygqzzk.xyz");
        templateMessageDTO.setData(data);

        Call<Void> call = weixinApiService.sendMessage(accessToken, templateMessageDTO);
        call.execute();
    }
}




