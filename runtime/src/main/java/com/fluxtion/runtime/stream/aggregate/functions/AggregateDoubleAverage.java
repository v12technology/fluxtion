package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateDoubleAverage extends BaseDoubleSlidingFunction<AggregateDoubleAverage> {

    private int count;
    private double sum;

    @Override
    public double aggregateDouble(double input) {
        sum += input;
        count++;
        value = sum / count;
        return getAsDouble();
    }

    @Override
    public void combine(AggregateDoubleAverage combine) {
        sum += combine.sum;
        count += combine.count;
        value = sum / count;
    }

    @Override
    public void deduct(AggregateDoubleAverage deduct) {
        sum -= deduct.sum;
        count -= deduct.count;
        value = sum / count;
    }

    @Override
    public double resetDouble() {
        value = 0;
        sum = 0;
        count = 0;
        return 0;
    }
}
