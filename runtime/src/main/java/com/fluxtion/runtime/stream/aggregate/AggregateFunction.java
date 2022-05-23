package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.stream.Stateful;

import java.util.function.Supplier;

/**
 * {@link java.util.function.Function}
 *
 * @param <I> Input type
 * @param <R> Return type of the wrapped function
 * @param <T> The type of this BaseSlidingWindowFunction
 */
public interface AggregateFunction<I, R, T extends AggregateFunction<I, R, T>>
        extends Stateful<R>, Supplier<R> {

    default void combine(T add) {
        throw new RuntimeException("Sliding not supported implement combine for " + this.getClass().getName());
    }

    default void deduct(T add) {
        throw new RuntimeException("Sliding not supported implement deduct for " + this.getClass().getName());
    }

    R get();

    R aggregate(I input);

}
