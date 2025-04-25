package com.yygqzzk.api;

import com.yygqzzk.api.dto.CreatePayRequestDTO;
import com.yygqzzk.api.response.Response;

import javax.annotation.Resource;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/25
 */

public interface IPayService {

    public Response<String> createPayOrder(CreatePayRequestDTO createPayRequestDTO);

}
