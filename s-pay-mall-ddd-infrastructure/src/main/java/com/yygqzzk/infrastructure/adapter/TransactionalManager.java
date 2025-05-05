package com.yygqzzk.infrastructure.adapter;

import com.yygqzzk.domain.order.adapter.ITransactionalManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

/**
 * @author zzk
 * @version 1.0
 * @description TODO
 * @since 2025/5/4
 */
@Component
public class TransactionalManager implements ITransactionalManager {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public <T> T executeInTransaction(Supplier<T> operation) {
        return operation.get();
    }
}




