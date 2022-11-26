package com.fluxtion.runtime.callback;

/**
 * provides query capability to determine if a node indicates it was dirty during this calculation cycle. A dirty node
 * propagates the event notification to dependents.
 */
public interface DirtyStateMonitor {
    boolean isDirty(Object node);

    class Implementation implements DirtyStateMonitor {
        public CallbackDispatcherImpl callbackDispatcher;

        @Override
        public boolean isDirty(Object node) {
            return node != null && callbackDispatcher.isDirtyPredicate.test(node);
        }
    }
}
