package com.fluxtion.runtime.dataflow;

import java.util.function.DoubleSupplier;

/**
 * Makes the output of a {@link DoubleFlowFunction} available in a user class
 */
public interface DoubleFlowSupplier extends FlowSupplier<Double>, DoubleSupplier {
    default Double get() {
        return getAsDouble();
    }
}
