package com.fluxtion.runtime.node;

import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.callback.CallbackDispatcher;
import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.callback.EventDispatcher;
import com.fluxtion.runtime.callback.EventProcessorCallbackInternal;
import com.fluxtion.runtime.callback.InternalEventProcessor;

import java.util.HashMap;
import java.util.Map;

public final class MutableEventProcessorContext implements EventProcessorContext, NamedNode {

    private final transient Map<Object, Object> map = new HashMap<>();
    @Inject
    private final NodeNameLookup nodeNameLookup;
    @Inject
    private final EventProcessorCallbackInternal eventDispatcher;
    @Inject
    private final DirtyStateMonitor dirtyStateMonitor;

    public MutableEventProcessorContext(
            NodeNameLookup nodeNameLookup,
            @AssignToField("eventDispatcher") EventProcessorCallbackInternal eventDispatcher,
            @AssignToField("dirtyStateMonitor") DirtyStateMonitor dirtyStateMonitor
    ) {
        this.nodeNameLookup = nodeNameLookup;
        this.eventDispatcher = eventDispatcher;
        this.dirtyStateMonitor = dirtyStateMonitor;
    }

    public MutableEventProcessorContext() {
        this(null, null, null);
    }

    public void replaceMappings(Map<Object, Object> newMap) {
        if (newMap != null) {
            map.clear();
            map.putAll(newMap);
        }
    }

    public void setEventProcessorCallback(InternalEventProcessor eventProcessorCallback) {
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

    @Override
    public CallbackDispatcher getCallBackDispatcher() {
        return eventDispatcher;
    }

    @Override
    public DirtyStateMonitor getDirtyStateMonitor() {
        return dirtyStateMonitor;
    }

    public Map<Object, Object> getMap() {
        return map;
    }

    public <K, V> V put(K key, V value) {
        return (V) map.put(key, value);
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
