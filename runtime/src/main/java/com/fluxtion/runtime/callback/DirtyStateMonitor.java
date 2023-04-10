package com.fluxtion.runtime.callback;

import java.util.function.BooleanSupplier;

/**
 * provides query capability to determine if a node indicates it was dirty during this calculation cycle. A dirty node
 * propagates the event notification to dependents.
 */
public interface DirtyStateMonitor {
    boolean isDirty(Object node);

    BooleanSupplier dirtySupplier(Object node);

    void markDirty(Object node);

}
