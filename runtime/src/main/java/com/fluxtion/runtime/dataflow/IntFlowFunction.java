package com.fluxtion.runtime.dataflow;

import java.util.function.IntSupplier;

/**
 * A primitive int function step applied to a data flow.
 */
public interface IntFlowFunction extends FlowFunction<Integer>, IntSupplier {
    default Integer get() {
        return getAsInt();
    }
}
