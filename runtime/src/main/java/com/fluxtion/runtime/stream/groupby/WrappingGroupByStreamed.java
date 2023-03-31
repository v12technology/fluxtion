package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.stream.GroupByStreamed;

import java.util.Collection;
import java.util.Map;

public class WrappingGroupByStreamed<K, V> implements GroupByStreamed<K, V> {

    private KeyValue<K, V> keyValue;
    private GroupByStreamed<K, V> groupBy;

    public WrappingGroupByStreamed() {
    }

    public WrappingGroupByStreamed(KeyValue<K, V> keyValue, GroupByStreamed<K, V> groupBy) {
        this.keyValue = keyValue;
        this.groupBy = groupBy;
    }

    public KeyValue<K, V> getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(KeyValue<K, V> keyValue) {
        this.keyValue = keyValue;
    }

    public GroupByStreamed<K, V> getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(GroupByStreamed<K, V> groupBy) {
        this.groupBy = groupBy;
    }

    @Override
    public V lastValue() {
        return keyValue.getValue();
    }

    @Override
    public KeyValue<K, V> lastKeyValue() {
        return keyValue;
    }

    @Override
    public Map<K, V> toMap() {
        return groupBy.toMap();
    }

    @Override
    public Collection<V> values() {
        return groupBy.values();
    }

    public void reset() {
        groupBy = GroupByStreamed.emptyCollection();
        keyValue = GroupByStreamed.emptyKey();
    }
}

