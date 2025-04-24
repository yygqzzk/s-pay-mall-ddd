package com.yygqzzk.domain.order.service;

import com.yygqzzk.domain.order.adapter.port.IProductPort;
import com.yygqzzk.domain.order.adapter.repository.IOrderRepository;
import com.yygqzzk.domain.order.model.aggregate.CreateOrderAggregate;
import com.yygqzzk.domain.order.model.entity.OrderEntity;
import com.yygqzzk.domain.order.model.entity.PayOrderEntity;
import com.yygqzzk.domain.order.model.entity.ProductEntity;
import com.yygqzzk.domain.order.model.entity.ShopCartEntity;
import com.yygqzzk.domain.order.model.valobj.OrderStatusVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractOrderService implements IOrderService {

    protected final IProductPort productPort;
    protected final IOrderRepository orderRepository;

    public abstract void creatPayOrder();

    @Override
    public PayOrderEntity createOrder(ShopCartEntity shopCartEntity) throws Exception {
        // 1. 查询当前用户是否存在未支付订单或掉单订单

        OrderEntity unpaidOrder = orderRepository.queryUnPayOrder(OrderEntity.builder()
                .userId(shopCartEntity.getUserId())
                .productId(shopCartEntity.getProductId())
                .build());
        // 判断未支付订单和掉单情况
        if(null != unpaidOrder && OrderStatusVO.PAY_WAIT.getCode().equals(unpaidOrder.getStatus())){
            log.info("创建订单-存在，已存在未支付订单，userId: {} productId: {} orderId: ", unpaidOrder.getUserId(), shopCartEntity.getProductId(), unpaidOrder.getOrderId());
            return PayOrderEntity.builder()
                    .orderId(unpaidOrder.getOrderId())
                    .payUrl(unpaidOrder.getPayUrl())
                    .build();
        } else if(null != unpaidOrder && OrderStatusVO.CREATE.getCode().equals(unpaidOrder.getStatus())){
            // TODO

        }

        // 首次下单，查询商品、创建订单
        ProductEntity productEntity = productPort.queryProductByProductId(shopCartEntity.getProductId());
        if(productEntity == null){
            log.error("未找到商品信息 productId: {}", shopCartEntity.getProductId());
            throw new Exception();
        }
        OrderEntity orderEntity = CreateOrderAggregate.buildOrderEntity(productEntity.getProductId(), productEntity.getProductName());

        CreateOrderAggregate orderAggregate = CreateOrderAggregate.builder()
                .userId(shopCartEntity.getUserId())
                .orderEntity(orderEntity)
                .productEntity(productEntity)
                .build();

        this.doSaveOrder(orderAggregate);
        // 创建支付单
        // TODO
        creatPayOrder();

        // 返回订单信息
        return PayOrderEntity.builder()
                .orderId(orderEntity.getOrderId())
                .userId(orderEntity.getUserId())
                .payUrl("暂无")
                .build();
    }

    public abstract void doSaveOrder(CreateOrderAggregate orderAggregate);
}




