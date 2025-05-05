package com.yygqzzk.infrastructure.adapter.port;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.jthinking.common.util.ip.IPInfo;
import com.jthinking.common.util.ip.IPInfoUtils;
import com.yygqzzk.domain.auth.adapter.port.ILoginPort;
import com.yygqzzk.infrastructure.gateway.IWeixinApiService;
import com.yygqzzk.infrastructure.gateway.dto.WeixinQrCodeRequestDTO;
import com.yygqzzk.infrastructure.gateway.dto.WeixinQrCodeResponseDTO;
import com.yygqzzk.infrastructure.gateway.dto.WeixinLoginTemplateMessageDTO;
import com.yygqzzk.infrastructure.gateway.dto.WeixinTokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
    @Value("${weixin.config.login_template_id}")
    String templateId;

    private final Cache<String, String> weixinAccessToken;
    private final Cache<String, String> loginIp;

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
//        WeixinLoginTemplateMessageDTO.put(data, WeixinLoginTemplateMessageDTO.TemplateKey.USER, openid);
        // 登录网站
        WeixinLoginTemplateMessageDTO.put(data, WeixinLoginTemplateMessageDTO.TemplateKey.SITE, "商品支付服务系统");
        // 登录网址
        WeixinLoginTemplateMessageDTO.put(data, WeixinLoginTemplateMessageDTO.TemplateKey.HOST, "yygqzzk.xyz");
        // 添加用户登录IP信息
        String ip = loginIp.getIfPresent(openid);
        WeixinLoginTemplateMessageDTO.put(data, WeixinLoginTemplateMessageDTO.TemplateKey.IP, ip);
        // 添加登录IP地址地点信息
        IPInfo ipInfo = IPInfoUtils.getIpInfo(ip);
        String addr = ipInfo.getProvince() + " " + ipInfo.getAddress();
        WeixinLoginTemplateMessageDTO.put(data, WeixinLoginTemplateMessageDTO.TemplateKey.ADDRESS, addr);
        // 添加登录时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");
        String time = formatter.format(LocalDateTime.now());
        WeixinLoginTemplateMessageDTO.put(data, WeixinLoginTemplateMessageDTO.TemplateKey.TIME, time);

        WeixinLoginTemplateMessageDTO templateMessageDTO = new WeixinLoginTemplateMessageDTO(openid, templateId);
        templateMessageDTO.setUrl("https://github.com/yygqzzk");
        templateMessageDTO.setData(data);

        Call<Void> call = weixinApiService.sendMessage(accessToken, templateMessageDTO);
        call.execute();
    }




}




