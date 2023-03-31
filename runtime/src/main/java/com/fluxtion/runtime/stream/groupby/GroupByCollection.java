package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.stream.GroupByStreamed;
import com.fluxtion.runtime.stream.Stateful;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GroupByCollection<K, V> implements GroupByStreamed<K, V>, Stateful<GroupByStreamed<K, V>> {
    private final Map<K, V> map = new HashMap<>();

    public GroupByCollection<K, V> add(KeyValue<K, V> keyValue) {
        map.put(keyValue.getKey(), keyValue.getValue());
        return this;
    }

    public GroupByCollection<K, V> fromMap(Map<K, V> fromMap) {
        reset();
        map.putAll(fromMap);
        return this;
    }

    @Override
    public GroupByStreamed<K, V> reset() {
        map.clear();
        return this;
    }

    @Override
    public Map<K, V> toMap() {
        return map;
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public String toString() {
        return "GroupByCollection{" +
                "map=" + map +
                '}';
    }
}
