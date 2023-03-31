package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.stream.FlowSupplier;

interface EventSupplierAccessor<T extends FlowSupplier<?>> {
    T runtimeSupplier();
}
