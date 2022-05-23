package com.fluxtion.runtime.stream.groupby;

public interface GroupByStreamed<K, V> extends GroupBy<K, V> {
    V value();

    KeyValue<K, V> keyValue();


}
