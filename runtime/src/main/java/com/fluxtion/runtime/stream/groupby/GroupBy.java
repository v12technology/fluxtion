package com.fluxtion.runtime.stream.groupby;

import lombok.Value;

public interface GroupBy<K, V> extends GroupByBatched<K, V> {
    V value();

    KeyValue<K, V> keyValue();


    @Value
    class KeyValue<K, V>{
        K key;
        V value;
    }
}
