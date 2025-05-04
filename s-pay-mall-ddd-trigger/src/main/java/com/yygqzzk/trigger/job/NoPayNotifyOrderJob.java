package com.yygqzzk.trigger.job;

import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.yygqzzk.domain.order.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 检测未收到或未正确处理支付回调通知
 */
@Slf4j
@Component()
public class NoPayNotifyOrderJob {

    @Resource
    private IOrderService orderService;
    @Resource
    private AlipayClient alipayClient;

    @Scheduled(cron = "0/3 * * * * ?")
    public void exec() {
        try {

            List<String> orderIds = orderService.queryNoPayNotifyOrder();
            if (null == orderIds || orderIds.isEmpty()) return;

            for (String orderId : orderIds) {
                AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
                AlipayTradeQueryModel bizModel = new AlipayTradeQueryModel();
                bizModel.setOutTradeNo(orderId);
                request.setBizModel(bizModel);

                AlipayTradeQueryResponse alipayTradeQueryResponse = alipayClient.execute(request);
                String code = alipayTradeQueryResponse.getCode();
                String tradeStatus = alipayTradeQueryResponse.getTradeStatus();
                Date payTime = alipayTradeQueryResponse.getSendPayDate();
                // 判断状态码
                if ("10000".equals(code)) {
                    if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                        // 支付成功才更新数据库状态
                        // 用来解决系统没有被正常回调的情况
                        log.info("任务:检测未接收到或未正确处理的支付订单 orderId : {}", orderId);
                        orderService.changeOrderPaySuccess(orderId, payTime);
                    }
                }
            }
        } catch (Exception e) {
            // 通常是因为数据库中创建了订单信息，但支付宝那边没有创建订单信息
            log.error("检测未接收到或未正确处理的支付回调通知失败", e);
        }
    }

}
