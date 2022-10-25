package com.fluxtion.runtime.stream.groupby;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public interface GroupByStreamed<K, V> extends GroupBy<K, V> {
    V value();

    KeyValue<K, V> keyValue();


    static <K, V> GroupByStreamed<K, V> emptyCollection() {
        return new EmptyGroupByStreamed<>();
    }

    class EmptyGroupByStreamed<K, V> implements GroupByStreamed<K, V> {
        @Override
        public V value() {
            return null;
        }

        @Override
        public KeyValue<K, V> keyValue() {
            return null;
        }

        @Override
        public Map<K, V> map() {
            return Collections.emptyMap();
        }

        @Override
        public Collection<V> values() {
            return Collections.emptyList();
        }
    }
}
