package com.yygqzzk.domain.auth.service.impl;

import com.google.common.cache.Cache;
import com.yygqzzk.domain.auth.adapter.port.ILoginPort;
import com.yygqzzk.domain.auth.service.ILoginService;
import lombok.extern.slf4j.Slf4j;
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
    public String checkLogin(String ticket) {
        return openidToken.getIfPresent(ticket);
    }

    @Override
    public void saveLoginState(String ticket, String openid) throws IOException {
        openidToken.put(ticket, openid);
        loginPort.sendLoginTemplate(ticket, openid);
    }

    @Override
    public void saveLoginIpinfo(String ticket, String ip) throws IOException {
        loginIp.put(ticket, ip);
    }

}




