package com.yygqzzk.domain.auth.service.impl;

import com.google.common.cache.Cache;
import com.yygqzzk.domain.auth.adapter.port.ILoginPort;
import com.yygqzzk.domain.auth.service.ILoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@Service
@Slf4j
public class WeixinLoginServiceImpl implements ILoginService {

    @Resource
    private Cache<String, String> openidToken;

    @Resource
    private Cache<String, String> loginIp;


    @Resource
    private ILoginPort loginPort;


    @Override
    public String createQrCodeTicket() throws Exception {
        return loginPort.createQrCodeTicket();
    }

    @Override
    public String createQrCodeTicket(String sceneStr) throws Exception {
        String ticket = loginPort.createQrCodeTicket(sceneStr);
        // 保存浏览器指纹信息和ticket映射关系
        openidToken.put(sceneStr, ticket);
        return ticket;
    }

    @Override
    public String checkLogin(String ticket) {
        return openidToken.getIfPresent(ticket);
    }

    @Override
    public String checkLogin(String ticket, String sceneStr) {
        String cacheTicket = openidToken.getIfPresent(sceneStr);
        if(StringUtils.isBlank(cacheTicket) || !cacheTicket.equals(ticket)){
            // 用户浏览器指纹 和 ticket 不一致
            return null;
        }
        return checkLogin(ticket);
    }

    @Override
    public void saveLoginState(String ticket, String openid) throws IOException {
        openidToken.put(ticket, openid);
    }

    @Override
    public void saveLoginIpinfo(String openId, String ip) throws IOException {
        loginIp.put(openId, ip);
    }

    @Override
    public void sendLoginTemplate(String ticket, String openid) throws IOException {
        loginPort.sendLoginTemplate(ticket, openid);
    }

}




