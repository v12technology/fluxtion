package com.fluxtion.runtime.stream.groupby;

public interface GroupBy<K, V> extends GroupByBatched<K, V> {
    V value();

    KeyValue<K, V> keyValue();


}
