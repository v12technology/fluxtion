package com.fluxtion.runtime.node;

import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.callback.*;
import com.fluxtion.runtime.input.SubscriptionManager;
import com.fluxtion.runtime.time.Clock;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class MutableEventProcessorContext implements EventProcessorContext, NamedNode {

    private final transient Map<Object, Object> map = new HashMap<>();
    @Inject
    private final NodeNameLookup nodeNameLookup;
    @Inject
    private final EventProcessorCallbackInternal eventDispatcher;
    @Inject
    private final SubscriptionManager subscriptionManager;
    @Inject
    private final DirtyStateMonitor dirtyStateMonitor;
    @Getter
    @Setter
    private Clock clock = Clock.DEFAULT_CLOCK;
    private InternalEventProcessor eventProcessorCallback;

    public MutableEventProcessorContext(
            @AssignToField("nodeNameLookup") NodeNameLookup nodeNameLookup,
            @AssignToField("eventDispatcher") EventProcessorCallbackInternal eventDispatcher,
            @AssignToField("subscriptionManager") SubscriptionManager subscriptionManager,
            @AssignToField("dirtyStateMonitor") DirtyStateMonitor dirtyStateMonitor
    ) {
        this.nodeNameLookup = nodeNameLookup;
        this.eventDispatcher = eventDispatcher;
        this.subscriptionManager = subscriptionManager;
        this.dirtyStateMonitor = dirtyStateMonitor;
    }

    public MutableEventProcessorContext() {
        this(null, null, null, null);
    }

    public void replaceMappings(Map<Object, Object> newMap) {
        if (newMap != null) {
            map.clear();
            map.putAll(newMap);
        }
    }

    public <K, V> void addMapping(K key, V value) {
        map.put(key, value);
    }

    public void setEventProcessorCallback(InternalEventProcessor eventProcessorCallback) {
        this.eventProcessorCallback = eventProcessorCallback;
        eventDispatcher.setEventProcessor(eventProcessorCallback);
    }

    @Override
    public NodeNameLookup getNodeNameLookup() {
        return nodeNameLookup;
    }

    @Override
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public CallbackDispatcher getCallBackDispatcher() {
        return eventDispatcher;
    }

    @Override
    public DirtyStateMonitor getDirtyStateMonitor() {
        return dirtyStateMonitor;
    }

    @Override
    public SubscriptionManager getSubscriptionManager() {
        return subscriptionManager;
    }

    public Map<Object, Object> getMap() {
        return map;
    }

    public <K, V> V put(K key, V value) {
        return (V) map.put(key, value);
    }

    @Override
    public <T> T getExportedService(Class<T> exportedServiceClass) {
        return eventProcessorCallback.exportedService(exportedServiceClass);
    }

    @Override
    public <T> T getExportedService() {
        return eventProcessorCallback.exportedService();
    }

    @Override
    public <T> T getInjectedInstance(Class<T> instanceClass) {
        return Objects.requireNonNull(
                getContextProperty(instanceClass.getCanonicalName()),
                "no instance injected into context of type:" + instanceClass);
    }

    @Override
    public <T> T getInjectedInstance(Class<T> instanceClass, String name) {
        return Objects.requireNonNull(
                getContextProperty(instanceClass.getCanonicalName() + "_" + name),
                "no instance injected into context of type:" + instanceClass + " named:" + name);
    }

    @Override
    public <T> T getInjectedInstanceAllowNull(Class<T> instanceClass) {
        return getContextProperty(instanceClass.getCanonicalName());
    }

    @Override
    public <T> T getInjectedInstanceAllowNull(Class<T> instanceClass, String name) {
        return getContextProperty(instanceClass.getCanonicalName() + "_" + name);
    }

    @Override
    public <K, V> V getContextProperty(K key) {
        return (V) map.get(key);
    }

    @Override
    public String toString() {
        return "MutableEventProcessorContext{" +
                "map=" + map +
                '}';
    }

    @Override
    public String getName() {
        return EventProcessorContext.DEFAULT_NODE_NAME;
    }
}
