package com.fluxtion.runtime.dataflow.aggregate;

import com.fluxtion.runtime.dataflow.Stateful;

import java.util.function.Supplier;

/**
 * {@link java.util.function.Function}
 *
 * @param <I> Input type
 * @param <R> Return type of the wrapped function
 * @param <T> The type of this BaseSlidingWindowFunction
 */
public interface AggregateFlowFunction<I, R, T extends AggregateFlowFunction<I, R, T>> extends Stateful<R>, Supplier<R> {

    default void combine(T add) {
        throw new RuntimeException("Sliding not supported implement combine for " + this.getClass().getName());
    }

    default void deduct(T add) {
        throw new RuntimeException("Sliding not supported implement deduct for " + this.getClass().getName());
    }

    default boolean deductSupported() {
        return true;
    }

    R get();

    R aggregate(I input);

}
