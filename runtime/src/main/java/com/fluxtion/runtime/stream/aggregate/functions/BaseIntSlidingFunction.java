package com.fluxtion.runtime.stream.aggregate.functions;

import com.fluxtion.runtime.stream.EventStream.IntEventStream;
import com.fluxtion.runtime.stream.IntAggregateFunction;

public abstract class BaseIntSlidingFunction<T extends BaseIntSlidingFunction<T>>
        implements IntEventStream, IntAggregateFunction<T> {

    protected int value;

    @Override
    public Integer reset() {
        return resetInt();
    }

    @Override
    public int resetInt() {
        value = 0;
        return getAsInt();
    }

    @Override
    public Integer aggregate(Integer input) {
        return aggregateInt(input);
    }

    public Integer get() {
        return getAsInt();
    }

    @Override
    public int getAsInt() {
        return value;
    }

}
