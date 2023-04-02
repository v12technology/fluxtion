package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

public class LongSumFlowFunction extends AbstractLongFlowFunction<LongSumFlowFunction> {

    @Override
    public long aggregateLong(long input) {
        value += input;
        return getAsLong();
    }

    @Override
    public void combine(LongSumFlowFunction combine) {
        value += combine.value;
    }

    @Override
    public void deduct(LongSumFlowFunction deduct) {
        value -= deduct.value;
    }

}
