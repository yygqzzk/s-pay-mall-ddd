package com.yygqzzk.trigger.http.handler;

import com.yygqzzk.api.response.Response;
import com.yygqzzk.types.common.Constants;
import com.yygqzzk.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author zzk
 * @version 1.0
 * @description 全局异常拦截器
 * @since 2025/5/5
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 处理你自定义的业务异常
    @ExceptionHandler(AppException.class)
    public Response<String> handleAppException(AppException e) {
        log.info("业务异常：{}, {}", e.getCode(), e.getInfo());
        return Response.<String>builder()
                .code(e.getCode())
                .info(e.getInfo())
                .build();
    }

    // 处理所有未捕获的系统异常
    @ExceptionHandler(Exception.class)
    public Response<String> handleGeneralException(Exception e) {
        log.info("未知异常：{}", e.toString());
        return Response.<String>builder()
                .code(Constants.ResponseCode.UN_ERROR.getCode())
                .info(Constants.ResponseCode.UN_ERROR.getInfo())
                .build();
    }
}




