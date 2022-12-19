package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.node.NamedNode;

/**
 * provides query capability to determine if a node indicates it was dirty during this calculation cycle. A dirty node
 * propagates the event notification to dependents.
 */
public interface DirtyStateMonitor {
    boolean isDirty(Object node);

    void markDirty(Object node);

    class DirtyStateMonitorImp implements DirtyStateMonitor, NamedNode {
        public CallbackDispatcherImpl callbackDispatcher;

        @Override
        public boolean isDirty(Object node) {
            return node != null && callbackDispatcher.eventProcessor.isDirty(node);
        }

        @Override
        public void markDirty(Object node) {
            callbackDispatcher.eventProcessor.setDirty(node, true);
        }

        @Override
        public String getName() {
            return "dirtyStateMonitor";
        }
    }
}
