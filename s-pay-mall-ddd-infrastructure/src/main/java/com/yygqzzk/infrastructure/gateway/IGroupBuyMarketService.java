package com.yygqzzk.infrastructure.gateway;

import org.springframework.web.bind.annotation.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import xyz.yygqzzk.api.dto.LockMarketPayOrderRequestDTO;
import xyz.yygqzzk.api.dto.LockMarketPayOrderResponseDTO;
import xyz.yygqzzk.api.dto.SettlementMarketPayOrderRequestDTO;
import xyz.yygqzzk.api.dto.SettlementMarketPayOrderResponseDTO;
import xyz.yygqzzk.api.response.Response;

/**
 * @author zzk
 * @version 1.0
 * @description 拼团营销
 * @since 2025/5/3
 */
public interface IGroupBuyMarketService {

    @POST("api/v1/gbm/trade/lock_market_pay_order")
    Call<Response<LockMarketPayOrderResponseDTO>> lockMarketPayOrder(@Body LockMarketPayOrderRequestDTO requestDTO);

    @POST("api/v1/gbm/trade/settlement_market_pay_order")
    Call<Response<SettlementMarketPayOrderResponseDTO>> settlementMarketPayOrder(@Body SettlementMarketPayOrderRequestDTO settlementMarketPayOrderRequestDTO);
}




