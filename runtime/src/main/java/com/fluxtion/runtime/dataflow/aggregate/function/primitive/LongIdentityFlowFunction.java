package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

public class LongIdentityFlowFunction extends AbstractLongFlowFunction<LongIdentityFlowFunction> {

    @Override
    public long aggregateLong(long input) {
        value = input;
        return getAsLong();
    }

    @Override
    public void combine(LongIdentityFlowFunction combine) {
        value = combine.value;
    }

    @Override
    public boolean deductSupported() {
        return false;
    }

}