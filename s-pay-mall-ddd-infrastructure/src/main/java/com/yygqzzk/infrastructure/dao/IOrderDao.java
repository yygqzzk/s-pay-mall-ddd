package com.yygqzzk.infrastructure.dao;

import com.yygqzzk.infrastructure.dao.po.PayOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/22
 */
@Mapper
public interface IOrderDao {

    void insert(PayOrder payOrder);

    PayOrder queryUnPayOrder(PayOrder payOrder);
}
