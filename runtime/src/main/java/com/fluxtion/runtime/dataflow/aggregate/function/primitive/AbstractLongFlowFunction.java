package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

import com.fluxtion.runtime.dataflow.LongFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateLongFlowFunction;

public abstract class AbstractLongFlowFunction<T extends AbstractLongFlowFunction<T>>
        implements LongFlowFunction, AggregateLongFlowFunction<T> {

    protected long value;

    @Override
    public long resetLong() {
        value = 0;
        return getAsLong();
    }

    @Override
    public Long reset() {
        return resetLong();
    }

    @Override
    public Long aggregate(Long input) {
        return aggregateLong(input);
    }

    public Long get() {
        return getAsLong();
    }

    @Override
    public long getAsLong() {
        return value;
    }

}
