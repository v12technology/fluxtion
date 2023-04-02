package com.fluxtion.runtime.dataflow;

import java.util.function.IntSupplier;

/**
 * Makes the output of a {@link IntFlowFunction} available in a user class
 */
public interface IntFlowSupplier extends FlowSupplier<Integer>, IntSupplier {
    default Integer get() {
        return getAsInt();
    }
}
