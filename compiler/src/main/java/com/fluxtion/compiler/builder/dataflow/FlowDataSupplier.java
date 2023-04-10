package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.dataflow.FlowSupplier;

public interface FlowDataSupplier<T extends FlowSupplier<?>> {
    T flowSupplier();
}
