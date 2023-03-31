package com.fluxtion.runtime.stream;

import java.util.function.IntSupplier;

public interface IntFlowSupplier extends FlowSupplier<Integer>, IntSupplier {
    default Integer get() {
        return getAsInt();
    }
}
