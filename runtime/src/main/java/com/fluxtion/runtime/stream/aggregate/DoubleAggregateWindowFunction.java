package com.fluxtion.runtime.stream.aggregate;

import java.util.function.DoubleSupplier;

public interface DoubleAggregateWindowFunction<T extends DoubleAggregateWindowFunction<T>>
        extends AggregateWindowFunction<Double, Double, T>, DoubleSupplier {


    double resetDouble();

    double aggregateDouble(double input);
}
