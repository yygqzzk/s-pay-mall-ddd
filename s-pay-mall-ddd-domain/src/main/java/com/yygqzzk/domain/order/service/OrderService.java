package com.yygqzzk.domain.order.service;

import com.alibaba.fastjson.JSONObject;
import com.yygqzzk.domain.order.adapter.port.IProductPort;
import com.yygqzzk.domain.order.adapter.repository.IOrderRepository;
import com.yygqzzk.domain.order.model.aggregate.CreateOrderAggregate;
import com.yygqzzk.domain.order.model.entity.OrderEntity;
import com.yygqzzk.domain.order.model.entity.PayOrderEntity;
import com.yygqzzk.domain.order.model.valobj.OrderStatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/24
 */
@Component
@Slf4j
public class OrderService extends AbstractOrderService{


    public OrderService(IAlipayService alipayService, IProductPort productPort, IOrderRepository orderRepository) {
        super(alipayService, productPort, orderRepository);
    }

    @Override
    public OrderEntity doPrepayOrder(OrderEntity orderEntity) throws Exception {

        String form = alipayService.creatPayUrl(orderEntity);
        orderEntity.setStatus(OrderStatusVO.PAY_WAIT.getCode());
        orderEntity.setPayUrl(form);
        orderRepository.updateOrderPayInfo(orderEntity);
        return orderEntity;
    }

    @Override
    public void doSaveOrder(CreateOrderAggregate orderAggregate) {
        orderRepository.doSaveOrder(orderAggregate);
    }


}




