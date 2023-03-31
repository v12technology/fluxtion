package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

public class IntAverageFlowFunction extends AbstractIntFlowFunction<IntAverageFlowFunction> {

    private final DoubleAverageFlowFunction avg = new DoubleAverageFlowFunction();

    @Override
    public void combine(IntAverageFlowFunction add) {
        avg.combine(add.avg);
    }

    @Override
    public void deduct(IntAverageFlowFunction add) {
        avg.deduct(add.avg);
    }

    @Override
    public int aggregateInt(int input) {
        value = (int) avg.aggregateDouble(input);
        return getAsInt();
    }
}
