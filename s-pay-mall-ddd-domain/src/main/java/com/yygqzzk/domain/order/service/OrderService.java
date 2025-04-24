package com.yygqzzk.domain.order.service;

import com.yygqzzk.domain.order.adapter.port.IProductPort;
import com.yygqzzk.domain.order.adapter.repository.IOrderRepository;
import com.yygqzzk.domain.order.model.aggregate.CreateOrderAggregate;
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
    public OrderService(IProductPort productPort, IOrderRepository orderRepository) {
        super(productPort, orderRepository);
    }

    @Override
    public void creatPayOrder() {

    }

    @Override
    public void doSaveOrder(CreateOrderAggregate orderAggregate) {
        orderRepository.doSaveOrder(orderAggregate);
    }


}




