package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

public class LongMinFlowFunction extends AbstractLongFlowFunction<LongMinFlowFunction> {

    @Override
    public long aggregateLong(long input) {
        value = reset ? input : Math.min(value, input);
        reset = false;
        return getAsLong();
    }

    @Override
    public void combine(LongMinFlowFunction add) {
        aggregateLong(add.getAsLong());
    }

    @Override
    public boolean deductSupported() {
        return false;
    }
}
