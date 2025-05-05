package com.yygqzzk.domain.order.adapter;

import com.yygqzzk.types.exception.AppException;

import java.util.function.Supplier;

/**
 * @author zzk
 * @version 1.0
 * @description 事务管理器
 * @since 2025/5/4
 */
public interface ITransactionalManager {
    <T> T executeInTransaction(Supplier<T> operation) throws AppException;
}




