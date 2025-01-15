package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

public class LongMaxFlowFunction extends AbstractLongFlowFunction<LongMaxFlowFunction> {

    @Override
    public long aggregateLong(long input) {
        value = reset ? input : Math.max(value, input);
        reset = false;
        return getAsLong();
    }

    @Override
    public void combine(LongMaxFlowFunction add) {
        aggregateLong(add.getAsLong());
    }

    @Override
    public boolean deductSupported() {
        return false;
    }
}
