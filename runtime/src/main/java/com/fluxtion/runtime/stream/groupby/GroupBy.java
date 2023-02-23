package com.fluxtion.runtime.stream.groupby;

import lombok.Value;

import java.util.Collection;
import java.util.Map;

public interface GroupBy<K, V> {

    KeyValue KV_KEY_VALUE = new KeyValue<>(null, null);

    Map<K, V> map();

    Collection<V> values();

    static <K, V> KeyValue<K, V> emptyKey() {
        return KV_KEY_VALUE;
    }

    @Value
    class KeyValue<K, V> {
        K key;
        V value;

        public Double getValueAsDouble() {
            return (Double) value;
        }

        public Long getValueAsLong() {
            return (Long) value;
        }

        public Integer getValueAsInt() {
            return (Integer) value;
        }
    }
}
