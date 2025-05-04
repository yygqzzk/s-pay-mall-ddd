package com.yygqzzk.test;

import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author zzk
 * @version 1.0
 * @description TODO
 * @since 2025/5/4
 */
@SpringBootTest
@Slf4j
public class SandBoxTest {
    @Resource
    AlipayClient alipayClient;


    @Test
    public void test() throws AlipayApiException {
        String APP_ID = "2021000148615584";
        String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCspiBIj9O077aR605/K5RGo4FhbwZnWLKht+VJwlE3VQq7l1wwxCcKNdWwrUNrjGKLMjbhbo/WN5YO5/ySU53ZhjgS5jONW3JeKXwGwwZiIinGGGotOtFRaCXcY7xkEdJkBXmsztsjlSWOBSEptx2nqNVniJAeE97WjJ9wesE4lhBmZigxjT1e4pwULqFmkPU+yFNcvo7Lw91bFkZPMn/eaTnlyeo7wfA5URy6MAHrEBW66C8QWaUf5/zDzlA4jH3ZrjbwCW9l1zRT1NfBT2EaruPuzDHzc7PvfwxDuOBii1BmRLsc7FaZVNVycuHD1a7bJeBHUirS8hYEQJk341J5AgMBAAECggEAdjOipKs70YPQ5iRI8M4gTSCCYvfFVZonaorhw1bzjcN+fqKJLqYM6HOlIrntD8Pkb9JoOn0coxgZd4kipffzeUroNyA+KUhzi74xONvrEeK74PE0OFUtm+OHEFWEVKrS9UG+ZS6WX80sEgi3BL1m4zO3L5QSv1TxWsiaHEMe6KLM3cPCI8WeTMbAfpadqFws7Uh8HiI6xgj3J0JeAJp80THpnu2w+dqsY7vYpOS3LA4207vmCi+INi7HPm8qSchoRmQhT/LQ7GqkvN0azL8H82CoS3l+Dg2YRcpyJvNIMZzGiQOwJLYAamMROKgW+F3UhPAl2U2/S32SyqPeuKbvAQKBgQDbycGWSOe8nJslcu40lK6NLLngZvUOgvcKqfEd4VA6TqDqytm0Zpaj7omZSquTaKu5085qb8W7xcgHf1S70LfJ4K7ZB04znJV1fDy3dCX4HrGLizX/+VrBKByMnjFYYmVY15UmIv6j3JvQzBGrdNwODxoDrcUcVihp1azpBWqRIQKBgQDJGCEMH4vSdZpIadViAJuQv4Sy7TcpS/2vD1KleJoJp93tncTyL2jHcWYFe4Z8O3zTKMuaomIWvyKRmkWZNzLt0mRN80wXVo5S5CidN+vQtTzsattR7LP0FsLhxfCbzJxYBvHxEnkFIMnontFz7N7F4lV7IVUvWBRQ5gFBLTUeWQKBgCiW1uhxKFK9vS0qT3Fob9/kfaNCgEkgyIywm7W/8XgDI1aZBaF6PKGjoMetcYg0kVotEna3k5W8zdcMndJdlkNy+fdckQWZCkT7H59llMmAqZsyp1NRcoAvby5M3KIM8dh5X7L+TXZnfGMP/MdkOmtiQJSjV76G7sm0y0zxUh/hAoGALUPhLHltX9JZCDPKdGTun4Z8uEntQL56PA2W7OhYxBOlQPdifaFuVHaSIGnNu+koD9/+9K/YpIYhoeS4BeUG8cGKEoHRfu9c2gvBmKId0rz5Gb5z390IJMl61JdZsbg8+ROXIkymMSFQUaxuOUn4W1K3KyA1rJKHAC7F2YXzjwECgYEAqCcsLhJCXlRD6GRHMVmVGt7LrRyhv9WxA136H1zSdAmukzI1YlOICIEfO0VVE1dO8s7UB8Jsfp6KrHulKp+3ZyTd43yR4CTaEZ0vjKcpgV3ItXLanZaKMjEXySDRxtB094Uvz7DwZiH1sL145bf8Jh8I6wPpBLaiMD0jhjhyGNw=";
        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhAf8pVswmtuLSUKvZVKtIa5LLUOhU59Ur8V7DIJsLUcyMj+q/nIHgOWGUHLDeiO7wVcAz4dSzRaRXeIihasDkUkPV2ob+S4FSRTLFQvwQESCQZi43/YHavlFTkeReUP9pbtcqUdvkGy8DUq6Qo0Oz2Grn1NHhEJxXp6J7RkMtHY9w9GncQSfVLd6hOO4x4tArGGXs+MPFwlwKM4zrAo6KMXeh+hqTWuF3UoYfwAsdfoDW52Gp5/JjvR12JS94A2+4hRQPRb35bqLkjWudmLefpHC63KRHpn7qJSUlEnntzPgzrsIak+Gpg/QYzKeR15NT1E/laYPKHLEXvKR+dxamwIDAQAB";
        String BUYER_ID = "2088722065554736";
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi-sandbox.dl.alipaydev.com/gateway.do",APP_ID,APP_PRIVATE_KEY,"json","UTF-8",ALIPAY_PUBLIC_KEY,"RSA2");
        AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
        request.setNotifyUrl("");
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "20210817010101003X02");
        bizContent.put("total_amount", 0.01);
        bizContent.put("subject", "测试商品");
        bizContent.put("buyer_id", BUYER_ID);
        bizContent.put("timeout_express", "10m");
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        AlipayTradeCreateResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
    }

}




