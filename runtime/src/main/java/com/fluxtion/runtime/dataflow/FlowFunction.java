package com.fluxtion.runtime.dataflow;

import java.util.function.Supplier;

/**
 * A function step applied to a data flow.
 *
 * @param <R>
 */
public interface FlowFunction<R> extends Supplier<R>, ParallelFunction {

    default boolean hasDefaultValue() {
        return false;
    }

}
