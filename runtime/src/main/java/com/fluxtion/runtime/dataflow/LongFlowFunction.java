package com.fluxtion.runtime.dataflow;

import java.util.function.LongSupplier;

/**
 * A primitive double function step applied to a data flow.
 */
public interface LongFlowFunction extends FlowFunction<Long>, LongSupplier, LongFlowSupplier {
    default Long get() {
        return getAsLong();
    }
}
