package com.fluxtion.runtime.node;

import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.annotations.builder.FluxtionIgnore;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.EventLogNode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Implements {@link NamedNode} overriding hashcode and equals using the name as the equality test and hash code seed
 */
public abstract class SingleNamedNode extends EventLogNode implements NamedNode {

    @FluxtionIgnore
    private final String name;
    @Getter
    @Setter
    @Inject
    private EventProcessorContext eventProcessorContext;

    public SingleNamedNode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleNamedNode that = (SingleNamedNode) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}