package com.fluxtion.runtime.dataflow;

import java.util.function.DoubleSupplier;

/**
 * A primitive double function step applied to a data flow.
 */
public interface DoubleFlowFunction extends FlowFunction<Double>, DoubleSupplier {
    default Double get() {
        return getAsDouble();
    }
}
