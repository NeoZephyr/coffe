package com.pain.flame.punk.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
public class TransactionSupport {
    public void doAfterCommit(Runnable runnable) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    runnable.run();
                }

                @Override
                public void afterCompletion(int status) {
                    log.info("===== afterCompletion: {}", status);
                }
            });
        } else {
            runnable.run();
        }
    }
}
