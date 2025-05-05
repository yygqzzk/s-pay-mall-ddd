package com.yygqzzk.trigger.http;

import com.yygqzzk.api.IAuthService;
import com.yygqzzk.api.response.Response;
import com.yygqzzk.domain.auth.service.ILoginService;
import com.yygqzzk.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */

@RestController
@Slf4j
@CrossOrigin("*")
@RequestMapping("/api/v1/login")
public class LoginController implements IAuthService {

    @Resource
    private ILoginService loginService;

    @Resource
    private HttpServletRequest request;


    /**
     * http://yygqzzk.natapp1.cc/api/v1/login/weixin_qrcode_ticket
     *
     * @return
     */
    @RequestMapping(value = "weixin_qrcode_ticket", method = RequestMethod.GET)
    @Override
    public Response<String> weixinQrCodeTicket() throws Exception {
        String qrCodeTicket = loginService.createQrCodeTicket();
        log.info("生成微信扫码登录 ticket: {}", qrCodeTicket);
        return Response.<String>builder().code(Constants.ResponseCode.SUCCESS.getCode()).info(Constants.ResponseCode.SUCCESS.getInfo()).data(qrCodeTicket).build();

    }
    @RequestMapping(value = "weixin_qrcode_ticket_scene", method = RequestMethod.GET)
    @Override
    public Response<String> weixinQrCodeTicket(@RequestParam String sceneStr) throws Exception {
        String qrCodeTicket = loginService.createQrCodeTicket(sceneStr);
        log.info("生成微信扫码登录 ticket: {}", qrCodeTicket);
        return Response.<String>builder().code(Constants.ResponseCode.SUCCESS.getCode()).info(Constants.ResponseCode.SUCCESS.getInfo()).data(qrCodeTicket).build();
    }


    /**
     * http://yygqzzk.natapp1.cc/api/v1/login/check_login
     *
     * @param ticket
     * @return
     */
    @RequestMapping(value = "check_login", method = RequestMethod.GET)
    @Override
    public Response<String> checkLogin(@RequestParam String ticket) throws IOException {
        String openId = loginService.checkLogin(ticket);

        log.info("扫码检测登录结果 ticket: {} openId: {}", ticket, openId);
        if (StringUtils.isNotBlank(openId)) {
            log.info("用户登录ip: {}", request.getRemoteAddr());
            loginService.saveLoginIpinfo(openId, request.getRemoteAddr());
            loginService.sendLoginTemplate(ticket, openId);

            return Response.<String>builder().code(Constants.ResponseCode.SUCCESS.getCode()).info(Constants.ResponseCode.SUCCESS.getInfo()).data(openId).build();
        } else {
            return Response.<String>builder().code(Constants.ResponseCode.NO_LOGIN.getCode()).info(Constants.ResponseCode.NO_LOGIN.getInfo()).build();
        }
    }

    @Override
    @RequestMapping(value = "check_login_scene", method = RequestMethod.GET)
    public Response<String> checkLogin(@RequestParam String ticket, @RequestParam String sceneStr) throws IOException {
        String openId = loginService.checkLogin(ticket, sceneStr);

        log.info("扫码检测登录结果 ticket: {} openId: {}", ticket, openId);
        if (StringUtils.isNotBlank(openId)) {
            log.info("用户登录ip: {}", request.getRemoteAddr());
            loginService.saveLoginIpinfo(openId, request.getRemoteAddr());
            loginService.sendLoginTemplate(ticket, openId);

            return Response.<String>builder().code(Constants.ResponseCode.SUCCESS.getCode()).info(Constants.ResponseCode.SUCCESS.getInfo()).data(openId).build();
        } else {
            return Response.<String>builder().code(Constants.ResponseCode.NO_LOGIN.getCode()).info(Constants.ResponseCode.NO_LOGIN.getInfo()).build();
        }
    }
}




