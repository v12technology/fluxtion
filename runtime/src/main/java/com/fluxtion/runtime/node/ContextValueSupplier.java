package com.fluxtion.runtime.node;

import java.util.function.Supplier;

/**
 * <pre>
 *
 * Lookups and injects a value from the {@link com.fluxtion.runtime.EventProcessorContext#getContextProperty(Object)}
 * The key for the lookup is taken from the value of this annotation. Makes the value available as {@link Supplier}
 *
 * Can optionally fail fast if there is no value in the context mapped to the specified key. Failure is lazy and occurs
 * at the get() call.
 * </pre>
 *
 * @param <T>
 */
public interface ContextValueSupplier<T> extends Supplier<T> {

    static <S> ContextValueSupplier<S> build(String contextKey) {
        return new ContextValueSupplierNode<>(contextKey);
    }

    static <S> ContextValueSupplier<S> buildFailFast(String contextKey) {
        return new ContextValueSupplierNode<>(contextKey, true);
    }

}
