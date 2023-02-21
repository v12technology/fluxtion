package com.fluxtion.runtime.node;

import com.fluxtion.runtime.EventProcessorContext;

import java.util.HashMap;
import java.util.Map;

public final class MutableEventProcessorContext extends SingletonNode implements EventProcessorContext {

    private final Map<Object, Object> map = new HashMap<>();

    public MutableEventProcessorContext() {
        super("CONTEXT");
    }

    public Map<Object, Object> getMap() {
        return map;
    }

    public <K, V> V put(K key, V value){
        return (V) map.put(key, value);
    }

    @Override
    public <K, V> V get(K key){
        return (V) map.get(key);
    }
}
