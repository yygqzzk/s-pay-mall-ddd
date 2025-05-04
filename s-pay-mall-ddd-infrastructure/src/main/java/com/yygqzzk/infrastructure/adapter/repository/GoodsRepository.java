package com.yygqzzk.infrastructure.adapter.repository;

import com.yygqzzk.domain.goods.adapter.repository.IGoodsRepository;
import com.yygqzzk.infrastructure.dao.IOrderDao;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author zzk
 * @version 1.0
 * @description TODO
 * @since 2025/5/4
 */
@Repository
public class GoodsRepository implements IGoodsRepository {
    @Resource
    private IOrderDao orderDao;

    @Override
    public void changeOrderDealDone(String orderId) {
        orderDao.changeOrderDealDone(orderId);
    }
}




