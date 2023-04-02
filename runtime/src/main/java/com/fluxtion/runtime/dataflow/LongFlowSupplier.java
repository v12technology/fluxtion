package com.fluxtion.runtime.dataflow;

import java.util.function.LongSupplier;

/**
 * Makes the output of a {@link LongFlowFunction} available in a user class
 */
public interface LongFlowSupplier extends FlowSupplier<Long>, LongSupplier {
    default Long get() {
        return getAsLong();
    }
}
