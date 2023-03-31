package com.fluxtion.runtime.stream;

import java.util.function.DoubleSupplier;

public interface DoubleFlowSupplier extends FlowSupplier<Double>, DoubleSupplier {
    default Double get() {
        return getAsDouble();
    }
}
