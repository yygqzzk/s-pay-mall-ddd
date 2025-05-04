package com.yygqzzk.test.domain;

import com.alibaba.fastjson2.JSON;
import com.yygqzzk.domain.order.model.entity.PayOrderEntity;
import com.yygqzzk.domain.order.model.entity.ProductEntity;
import com.yygqzzk.domain.order.model.entity.ShopCartEntity;
import com.yygqzzk.domain.order.model.valobj.MarketTypeVO;
import com.yygqzzk.domain.order.service.IOrderService;
import com.yygqzzk.infrastructure.adapter.port.ProductPort;
import com.yygqzzk.infrastructure.gateway.IGroupBuyMarketService;
import com.yygqzzk.infrastructure.gateway.ProductRPC;
import com.yygqzzk.infrastructure.gateway.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import retrofit2.Call;
import xyz.yygqzzk.api.dto.LockMarketPayOrderRequestDTO;
import xyz.yygqzzk.api.dto.LockMarketPayOrderResponseDTO;
import xyz.yygqzzk.api.response.Response;

import javax.annotation.Resource;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@SpringBootTest
@Slf4j
public class OrderServiceTest {
    @Resource
    private IOrderService orderService;

    @Resource
    private ProductPort productPort;

    @Resource
    private IGroupBuyMarketService groupBuyMarketService;


    @Test
    public void test_createOrder() throws Exception {
        ShopCartEntity shopCartEntity = new ShopCartEntity();
        shopCartEntity.setUserId("jgdf");
        shopCartEntity.setProductId("9890001");
        shopCartEntity.setTeamId(null);
        shopCartEntity.setActivityId(100123L);
        shopCartEntity.setMarketTypeVO(MarketTypeVO.GROUP_BUY_MARKET);

        PayOrderEntity payOrderEntity = orderService.createOrder(shopCartEntity);

        log.info("请求参数:{}", JSON.toJSONString(shopCartEntity));
        log.info("测试结果:{}", JSON.toJSONString(payOrderEntity));
    }

    @Test
    public void test_settlementOrder() throws Exception {
        ShopCartEntity shopCartEntity = new ShopCartEntity();
        shopCartEntity.setUserId("zzk");
        shopCartEntity.setProductId("9890001");
        shopCartEntity.setTeamId(null);
        shopCartEntity.setActivityId(100123L);
        shopCartEntity.setMarketTypeVO(MarketTypeVO.GROUP_BUY_MARKET);

        PayOrderEntity payOrderEntity = orderService.createOrder(shopCartEntity);

        log.info("请求参数:{}", JSON.toJSONString(shopCartEntity));
        log.info("测试结果:{}", JSON.toJSONString(payOrderEntity));
    }


    @Test
    public void test_getOrder() throws Exception {
        // 请求参数
        LockMarketPayOrderRequestDTO requestDTO = new LockMarketPayOrderRequestDTO();
        requestDTO.setUserId("qwer");
        requestDTO.setTeamId("");
        requestDTO.setGoodsId("9890001");
        requestDTO.setActivityId(100123L);
        requestDTO.setSource("s01");
        requestDTO.setChannel("c01");
        requestDTO.setOutTradeNo(RandomStringUtils.randomNumeric(12));
        requestDTO.setNotifyUrl("http://127.0.0.1:8090/api/v1/test/group_buy_notify");
        Call<Response<LockMarketPayOrderResponseDTO>> call = groupBuyMarketService.lockMarketPayOrder(requestDTO);
        // 获取结果
        Response<LockMarketPayOrderResponseDTO> response = call.execute().body();
        log.info("测试结果: {}", JSON.toJSONString(response));
    }
}




