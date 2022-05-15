package com.fluxtion.runtime.stream.groupby;

import lombok.Value;

import java.util.Collection;
import java.util.Map;

public interface GroupBy<K, V> {
    Map<K, V> map();

    V value();

    KeyValue<K, V> keyValue();

    Collection<V> values();

    @Value
    class KeyValue<K, V>{
        K key;
        V value;
    }
}
