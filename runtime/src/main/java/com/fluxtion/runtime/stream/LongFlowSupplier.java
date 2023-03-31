package com.fluxtion.runtime.stream;

import java.util.function.LongSupplier;

public interface LongFlowSupplier extends FlowSupplier<Long>, LongSupplier {
    default Long get() {
        return getAsLong();
    }
}
