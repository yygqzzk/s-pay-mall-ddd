package com.yygqzzk.api;

import com.yygqzzk.api.dto.CreatePayRequestDTO;
import com.yygqzzk.api.response.Response;
import xyz.yygqzzk.api.dto.NotifyRequestDTO;

import javax.annotation.Resource;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/25
 */

public interface IPayService {

    public Response<String> createPayOrder(CreatePayRequestDTO createPayRequestDTO);

    /**
     * 拼团结算回调
     *
     * @param requestDTO 请求对象
     * @return 返参，success 成功
     */
    String groupBuyNotify(NotifyRequestDTO requestDTO);

}
