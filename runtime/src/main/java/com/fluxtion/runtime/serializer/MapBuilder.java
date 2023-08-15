package com.fluxtion.runtime.serializer;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder {

    private MapBuilder() {
    }

    public static MapBuilder builder() {
        return new MapBuilder();
    }

    private final HashMap map = new HashMap();

    public <K, V> Map<K, V> build() {
        return map;
    }

    public MapBuilder put(Object key, Object value) {
        map.put(key, value);
        return this;
    }
}
