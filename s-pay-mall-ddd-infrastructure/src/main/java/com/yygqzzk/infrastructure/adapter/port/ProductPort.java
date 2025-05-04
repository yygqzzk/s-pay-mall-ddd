package com.yygqzzk.infrastructure.adapter.port;

import com.alibaba.fastjson2.JSON;
import com.yygqzzk.domain.order.adapter.port.IProductPort;
import com.yygqzzk.domain.order.model.entity.MarketPayDiscountEntity;
import com.yygqzzk.domain.order.model.entity.ProductEntity;
import com.yygqzzk.infrastructure.gateway.IGroupBuyMarketService;
import com.yygqzzk.infrastructure.gateway.ProductRPC;
import com.yygqzzk.infrastructure.gateway.dto.ProductDTO;
import com.yygqzzk.types.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import xyz.yygqzzk.api.dto.LockMarketPayOrderRequestDTO;
import xyz.yygqzzk.api.dto.LockMarketPayOrderResponseDTO;
import xyz.yygqzzk.api.dto.SettlementMarketPayOrderRequestDTO;
import xyz.yygqzzk.api.dto.SettlementMarketPayOrderResponseDTO;
import xyz.yygqzzk.api.response.Response;

import java.util.Date;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class ProductPort implements IProductPort {

    @Value("${app.config.group-buy-market.source}")
    private String source;
    @Value("${app.config.group-buy-market.channel}")
    private String channel;
    @Value("${app.config.group-buy-market.notify-url}")
    private String notifyUrl;

    private final ProductRPC productRPC;

    private final IGroupBuyMarketService groupBuyMarketService;

    @Override
    public ProductEntity queryProductByProductId(String productId) {
        ProductDTO productDTO = productRPC.queryProductByProductId(productId);

        return ProductEntity.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .productDesc(productDTO.getProductDesc())
                .price(productDTO.getPrice())
                .build();
    }

    @Override
    public MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId) {
        // 请求参数
        LockMarketPayOrderRequestDTO requestDTO = new LockMarketPayOrderRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setTeamId(teamId);
        requestDTO.setGoodsId(productId);
        requestDTO.setActivityId(activityId);
        requestDTO.setSource(source);
        requestDTO.setChannel(channel);
        requestDTO.setOutTradeNo(orderId);
        requestDTO.setNotifyUrl(notifyUrl);

        try {
            // 营销锁单
            Call<Response<LockMarketPayOrderResponseDTO>> call = groupBuyMarketService.lockMarketPayOrder(requestDTO);

            // 获取结果
            Response<LockMarketPayOrderResponseDTO> response = call.execute().body();
            log.info("营销锁单{} requestDTO:{} responseDTO:{}", userId, JSON.toJSONString(requestDTO), JSON.toJSONString(response));
            if (null == response) return null;

            // 异常判断
            if (!"0000".equals(response.getCode())){
                throw new AppException(response.getCode(), response.getInfo());
            }

            LockMarketPayOrderResponseDTO responseDTO = response.getData();

            // 获取拼团优惠
            return MarketPayDiscountEntity.builder()
                    .originalPrice(responseDTO.getOriginalPrice())
                    .deductionPrice(responseDTO.getDeductionPrice())
                    .payPrice(responseDTO.getPayPrice())
                    .build();
        } catch (Exception e) {
            log.error("营销锁单失败{}", userId, e);
            return null;
        }
    }

    @Override
    public void settlementMarketPayOrder(String userId, String orderId, Date orderTime) {
        SettlementMarketPayOrderRequestDTO requestDTO = new SettlementMarketPayOrderRequestDTO();
        requestDTO.setSource(source);
        requestDTO.setChannel(channel);
        requestDTO.setUserId(userId);
        requestDTO.setOutTradeNo(orderId);
        requestDTO.setOutTradeTime(orderTime);

        try {
            Call<Response<SettlementMarketPayOrderResponseDTO>> call = groupBuyMarketService.settlementMarketPayOrder(requestDTO);

            // 获取结果
            Response<SettlementMarketPayOrderResponseDTO> response = call.execute().body();
            log.info("营销结算{} requestDTO:{} responseDTO:{}", userId, JSON.toJSONString(requestDTO), JSON.toJSONString(response));
            if (null == response) return;

            // 异常判断
            if (!"0000".equals(response.getCode())) {
                throw new AppException(response.getCode(), response.getInfo());
            }

        } catch (Exception e) {
            log.error("营销结算失败{}", userId, e);
        }
    }
}




