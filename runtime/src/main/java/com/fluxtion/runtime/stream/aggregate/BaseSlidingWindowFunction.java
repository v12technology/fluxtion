package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.stream.Stateful;

/**
 * {@link java.util.function.Function}
 * @param <I> Input type
 * @param <R> Return type of the wrapped function
 * @param <T> The type of this BaseSlidingWindowFunction
 */
public abstract class BaseSlidingWindowFunction<I, R, T extends BaseSlidingWindowFunction<I, R, T>> implements Stateful<R> {

    private R thing;

    public void combine(T add){
    }

    public void deduct(T add){
    }

    public R get(){
        return thing;
    }

    protected void setValue(R aggregateResult){
        thing = aggregateResult;
    }

    public abstract R aggregate(I input);

}
