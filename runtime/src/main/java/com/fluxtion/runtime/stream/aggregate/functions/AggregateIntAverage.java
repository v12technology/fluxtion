package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateIntAverage extends BaseIntSlidingFunction<AggregateIntAverage> {

    private final AggregateDoubleAverage avg = new AggregateDoubleAverage();

    @Override
    public void combine(AggregateIntAverage add) {
        avg.combine(add.avg);
    }

    @Override
    public void deduct(AggregateIntAverage add) {
        avg.deduct(add.avg);
    }

    @Override
    public int aggregateInt(int input) {
        value = (int) avg.aggregateDouble(input);
        return getAsInt();
    }
}
