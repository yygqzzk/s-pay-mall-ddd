package com.yygqzzk.domain.goods.service;

import com.yygqzzk.domain.goods.adapter.repository.IGoodsRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zzk
 * @version 1.0
 * @description 结算服务
 * @since 2025/5/4
 */
@Service
public class GoodsServiceImpl implements IGoodsService {
    @Resource
    private IGoodsRepository repository;


    @Override
    public void changeOrderDealDone(String orderId) {
        repository.changeOrderDealDone(orderId);
    }
}




