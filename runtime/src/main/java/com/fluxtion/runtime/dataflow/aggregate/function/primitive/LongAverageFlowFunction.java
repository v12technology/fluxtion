package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

public class LongAverageFlowFunction extends AbstractLongFlowFunction<LongAverageFlowFunction> {

    private final DoubleAverageFlowFunction avg = new DoubleAverageFlowFunction();

    @Override
    public void combine(LongAverageFlowFunction add) {
        avg.combine(add.avg);
    }

    @Override
    public void deduct(LongAverageFlowFunction add) {
        avg.deduct(add.avg);
    }

    @Override
    public long aggregateLong(long input) {
        value = (long) avg.aggregateDouble(input);
        return getAsLong();
    }
}
