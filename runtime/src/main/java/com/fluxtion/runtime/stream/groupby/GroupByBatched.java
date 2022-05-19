package com.fluxtion.runtime.stream.groupby;

import lombok.Value;

import java.util.Collection;
import java.util.Map;

public interface GroupByBatched<K, V>  {

    Map<K, V> map();

    Collection<V> values();

    @Value
    class KeyValue<K, V>{
        K key;
        V value;
    }
}
