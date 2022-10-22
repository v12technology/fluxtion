package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateLongAverage extends BaseLongSlidingFunction<AggregateLongAverage> {

    private final AggregateDoubleAverage avg = new AggregateDoubleAverage();

    @Override
    public void combine(AggregateLongAverage add) {
        avg.combine(add.avg);
    }

    @Override
    public void deduct(AggregateLongAverage add) {
        avg.deduct(add.avg);
    }

    @Override
    public long aggregateLong(long input) {
        value = (long) avg.aggregateDouble(input);
        return getAsLong();
    }
}
