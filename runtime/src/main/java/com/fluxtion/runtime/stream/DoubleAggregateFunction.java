package com.fluxtion.runtime.stream;

import java.util.function.DoubleSupplier;

public interface DoubleAggregateFunction<T extends DoubleAggregateFunction<T>>
        extends AggregateFunction<Double, Double, T>, DoubleSupplier {


    double resetDouble();

    double aggregateDouble(double input);
}
