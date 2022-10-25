package com.fluxtion.runtime.stream.groupby;

import java.util.Collection;
import java.util.Map;

public class WrappingGroupByStreamed<K, V> implements GroupByStreamed<K, V> {

    private KeyValue<K, V> keyValue;
    private GroupBy<K, V> groupBy;

    public WrappingGroupByStreamed() {
    }

    public WrappingGroupByStreamed(KeyValue<K, V> keyValue, GroupBy<K, V> groupBy) {
        this.keyValue = keyValue;
        this.groupBy = groupBy;
    }

    public KeyValue<K, V> getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(KeyValue<K, V> keyValue) {
        this.keyValue = keyValue;
    }

    public GroupBy<K, V> getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(GroupBy<K, V> groupBy) {
        this.groupBy = groupBy;
    }

    @Override
    public V value() {
        return keyValue.getValue();
    }

    @Override
    public KeyValue<K, V> keyValue() {
        return keyValue;
    }

    @Override
    public Map<K, V> map() {
        return groupBy.map();
    }

    @Override
    public Collection<V> values() {
        return groupBy.values();
    }

    public void reset() {
        groupBy = GroupByStreamed.emptyCollection();
        keyValue = GroupBy.emptyKey();
    }
}

