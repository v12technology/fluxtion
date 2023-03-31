package com.fluxtion.runtime.stream;

import java.util.function.Supplier;

public interface FlowSupplier<R> extends Supplier<R> {
    boolean hasChanged();
}
