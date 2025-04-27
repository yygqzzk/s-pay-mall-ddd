package com.yygqzzk.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jthinking.common.util.ip.IPInfo;
import com.jthinking.common.util.ip.IPInfoUtils;
import com.yygqzzk.types.enums.WeixinMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ApiTest {

    @Test
    public void test() throws Exception {
        System.out.println(WeixinMessageEvent.SUBSCRIBE.getCode());
    }


    @Test
    public void getCityByIp() throws Exception {
        String ip = "113.204.50.120";
        System.out.println(ip);
        IPInfo ipInfo = IPInfoUtils.getIpInfo(ip);
        System.out.println(ipInfo);
        System.out.println(ipInfo.getProvince() + " " + ipInfo.getAddress());
    }

}
