package com.fluxtion.runtime.dataflow.groupby;

import java.util.Collection;
import java.util.Map;

public class GroupByView<K, V> implements GroupBy<K, V> {

    private KeyValue<K, V> keyValue;
    private GroupBy<K, V> groupBy;

    public GroupByView() {
    }

    public GroupByView(KeyValue<K, V> keyValue, GroupBy<K, V> groupBy) {
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
        groupBy = GroupBy.emptyCollection();
        keyValue = GroupBy.emptyKey();
    }
}

