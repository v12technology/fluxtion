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
public abstract class BaseSlidingWindowFunction<I, R, T extends BaseSlidingWindowFunction<I, R, T>>
        implements Stateful<R>, Supplier<R> {

    public void combine(T add) {
    }

    public void deduct(T add) {
    }

    public abstract R get();

    public abstract R aggregate(I input);

}
