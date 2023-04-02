package com.fluxtion.runtime.dataflow.aggregate;

import java.util.function.DoubleSupplier;

public interface AggregateDoubleFlowFunction<T extends AggregateDoubleFlowFunction<T>>
        extends AggregateFlowFunction<Double, Double, T>, DoubleSupplier {


    double resetDouble();

    double aggregateDouble(double input);
}
