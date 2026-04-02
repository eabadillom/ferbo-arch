package com.ferbo.arch.example.config;

import com.ferbo.arch.persistence.TransactionManager;
import com.ferbo.tools.functional.ThrowingRunnable;
import com.ferbo.tools.functional.ThrowingSupplier;

/**
 * Mock simple de TransactionManager
 */
public final class MockTransactionManager implements TransactionManager {

    @Override
    public <R> R execute(ThrowingSupplier<R> action) {
        try {
            return action.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeVoid(ThrowingRunnable accion) {
        try {
            accion.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}