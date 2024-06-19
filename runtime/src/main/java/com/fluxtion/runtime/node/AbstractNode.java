package com.fluxtion.runtime.node;

import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.time.Clock;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractNode extends EventLogNode
        implements
        LifecycleNode,
        TriggeredNode {

    @Getter
    @Setter
    @Inject
    private EventProcessorContext eventProcessorContext;

    protected void processReentrantEvent(Object event) {
        getEventProcessorContext().getEventDispatcher().processReentrantEvent(event);
    }

    protected void processAsNewEventCycle(Object event) {
        getEventProcessorContext().getEventDispatcher().processAsNewEventCycle(event);
    }

    protected void processAsNewEventCycle(Iterable<Object> iterable) {
        getEventProcessorContext().getEventDispatcher().processAsNewEventCycle(iterable);
    }

    protected void isDirty(Object node) {
        getEventProcessorContext().getDirtyStateMonitor().isDirty(node);
    }

    protected void markDirty(Object node) {
        getEventProcessorContext().getDirtyStateMonitor().markDirty(node);
    }

    protected <V> V getContextProperty(String key) {
        return getEventProcessorContext().getContextProperty(key);
    }

    protected <T> T getInjectedInstance(Class<T> instanceClass) {
        return getEventProcessorContext().getInjectedInstance(instanceClass);
    }

    protected <T> T getInjectedInstance(Class<T> instanceClass, String name) {
        return getEventProcessorContext().getInjectedInstance(instanceClass, name);
    }

    protected <T> T getInjectedInstanceAllowNull(Class<T> instanceClass) {
        return getEventProcessorContext().getInjectedInstanceAllowNull(instanceClass);
    }

    protected <T> T getInjectedInstanceAllowNull(Class<T> instanceClass, String name) {
        return getEventProcessorContext().getInjectedInstanceAllowNull(instanceClass, name);
    }

    protected String lookupInstanceName(Object node) {
        return getEventProcessorContext().getNodeNameLookup().lookupInstanceName(node);
    }

    protected <V> V getInstanceById(String instanceId) throws NoSuchFieldException {
        return getEventProcessorContext().getNodeNameLookup().getInstanceById(instanceId);
    }

    protected Clock getClock() {
        return getEventProcessorContext().getClock();
    }
}
