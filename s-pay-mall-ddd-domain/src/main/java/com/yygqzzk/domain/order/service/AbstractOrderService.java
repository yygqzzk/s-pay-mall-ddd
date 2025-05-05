package com.yygqzzk.domain.order.service;

import com.alipay.api.AlipayApiException;
import com.google.common.eventbus.EventBus;
import com.yygqzzk.domain.order.adapter.ITransactionalManager;
import com.yygqzzk.domain.order.adapter.event.PaySuccessMessageEvent;
import com.yygqzzk.domain.order.adapter.port.IProductPort;
import com.yygqzzk.domain.order.adapter.repository.IOrderRepository;
import com.yygqzzk.domain.order.model.aggregate.CreateOrderAggregate;
import com.yygqzzk.domain.order.model.entity.*;
import com.yygqzzk.domain.order.model.valobj.MarketTypeVO;
import com.yygqzzk.domain.order.model.valobj.OrderStatusVO;
import com.yygqzzk.types.common.Constants;
import com.yygqzzk.types.enums.ResponseCode;
import com.yygqzzk.types.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@Slf4j
public abstract class AbstractOrderService implements IOrderService {
    @Resource
    protected  IAlipayService alipayService;
    @Resource

    protected  IProductPort productPort;
    @Resource

    protected  IOrderRepository orderRepository;
    @Resource
    protected  PaySuccessMessageEvent paySuccessMessageEvent;
    @Resource
    protected  EventBus eventBus;
    @Resource
    protected ITransactionalManager transactionalManager;

    @Override
    public PayOrderEntity createOrder(ShopCartEntity shopCartEntity) throws AppException {

        return transactionalManager.executeInTransaction(() -> {
            try {
                // 1. 查询当前用户是否存在未支付订单或掉单订单
                OrderEntity unpaidOrder = orderRepository.queryUnPayOrder(shopCartEntity);
                // 判断未支付订单和掉单情况
                if (null != unpaidOrder && OrderStatusVO.PAY_WAIT.equals(unpaidOrder.getOrderStatusVO())) {
                    log.info("创建订单-存在，已存在未支付订单，userId: {} productId: {} orderId: {}", shopCartEntity.getUserId(), shopCartEntity.getProductId(), unpaidOrder.getOrderId());
                    return PayOrderEntity.builder().orderId(unpaidOrder.getOrderId()).payUrl(unpaidOrder.getPayUrl()).build();
                } else if (null != unpaidOrder && OrderStatusVO.CREATE.equals(unpaidOrder.getOrderStatusVO())) {
                    log.info("创建订单-存在，存在未创建支付单订单，创建支付单开始 userId:{} productId:{} orderId:{}", shopCartEntity.getUserId(), shopCartEntity.getProductId(), unpaidOrder.getOrderId());
                    Integer marketType = unpaidOrder.getMarketType();
                    BigDecimal marketDeductionAmount = unpaidOrder.getMarketDeductionAmount();

                    PayOrderEntity payOrderEntity = null;

                    if (MarketTypeVO.GROUP_BUY_MARKET.getCode().equals(marketType) && null == marketDeductionAmount) {
                        MarketPayDiscountEntity marketPayDiscountEntity = this.lockMarketPayOrder(shopCartEntity.getUserId(), shopCartEntity.getTeamId(), shopCartEntity.getActivityId(), shopCartEntity.getProductId(), unpaidOrder.getOrderId());

                        payOrderEntity = doPrepayOrder(shopCartEntity.getUserId(), shopCartEntity.getProductId(), unpaidOrder.getProductName(), unpaidOrder.getOrderId(), unpaidOrder.getTotalAmount(), marketPayDiscountEntity);

                    } else if (MarketTypeVO.GROUP_BUY_MARKET.getCode().equals(marketType)) {
                        payOrderEntity = doPrepayOrder(shopCartEntity.getUserId(), shopCartEntity.getProductId(), unpaidOrder.getProductName(), unpaidOrder.getOrderId(), unpaidOrder.getPayAmount());

                    } else {
                        payOrderEntity = doPrepayOrder(shopCartEntity.getUserId(), shopCartEntity.getProductId(), unpaidOrder.getProductName(), unpaidOrder.getOrderId(), unpaidOrder.getTotalAmount());
                    }

                    return PayOrderEntity.builder().orderId(payOrderEntity.getOrderId()).payUrl(payOrderEntity.getPayUrl()).build();
                }

                // 首次下单，查询商品、创建订单
                // 验证商品Id是否存在
                ProductEntity productEntity = productPort.queryProductByProductId(shopCartEntity.getProductId());
                if (productEntity == null) {
                    log.error("未找到商品信息 productId: {}", shopCartEntity.getProductId());
                    throw new AppException(Constants.ResponseCode.ILLEGAL_PARAMETER.getCode(), "没有对应商品信息");
                }
                // 订单实体信息
                OrderEntity orderEntity = CreateOrderAggregate.buildOrderEntity(productEntity.getProductId(), productEntity.getProductName(), shopCartEntity.getMarketTypeVO().getCode());
                // 订单聚合对象
                CreateOrderAggregate orderAggregate = CreateOrderAggregate.builder().userId(shopCartEntity.getUserId()).orderEntity(orderEntity).productEntity(productEntity).build();

                log.info("创建新订单，创建支付单开始 userId:{} productId:{} orderId:{}", orderAggregate.getUserId(), orderEntity.getProductId(), orderEntity.getOrderId());
                // 创建本地订单
                this.doSaveOrder(orderAggregate);
                MarketPayDiscountEntity marketPayDiscountEntity = null;

                // 发起营销锁单
                if (MarketTypeVO.GROUP_BUY_MARKET.equals(shopCartEntity.getMarketTypeVO())) {
                    // 有营销活动
                    marketPayDiscountEntity = this.lockMarketPayOrder(shopCartEntity.getUserId(), shopCartEntity.getTeamId(), shopCartEntity.getActivityId(), shopCartEntity.getProductId(), orderEntity.getOrderId());

                }

                // 创建支付单
                // 此时订单支付状态为 CREATE
                // 创建支付宝支付连接, 并更新支付状态为 PAY_WAIT
                PayOrderEntity payOrderEntity = doPrepayOrder(shopCartEntity.getUserId(), productEntity.getProductId(), productEntity.getProductName(), orderEntity.getOrderId(), productEntity.getPrice(), marketPayDiscountEntity);

                // 返回订单信息
                return PayOrderEntity.builder().orderId(payOrderEntity.getOrderId()).payUrl(payOrderEntity.getPayUrl()).build();
            } catch (AlipayApiException e) {
                throw new RuntimeException(e);
            }

        });


    }

    protected abstract MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId) throws AppException;

    protected abstract PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount) throws AlipayApiException;

    protected abstract PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount, MarketPayDiscountEntity marketPayDiscountEntity) throws AlipayApiException;

    public abstract void doSaveOrder(CreateOrderAggregate orderAggregate);
}




