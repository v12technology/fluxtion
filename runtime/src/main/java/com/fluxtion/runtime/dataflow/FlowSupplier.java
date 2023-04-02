package com.fluxtion.runtime.dataflow;

import java.util.function.Supplier;

/**
 * Makes the output of a {@link FlowFunction} available in a user class
 *
 * @param <R>
 */
public interface FlowSupplier<R> extends Supplier<R> {
    boolean hasChanged();
}
