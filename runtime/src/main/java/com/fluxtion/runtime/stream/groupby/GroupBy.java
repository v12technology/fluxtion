package com.fluxtion.runtime.stream.groupby;

import lombok.Value;

import java.util.Collection;
import java.util.Map;

public interface GroupBy<K, V>  {

    Map<K, V> map();

    Collection<V> values();

    static <K,V> KeyValue<K, V> emptyKey(){
        return new KeyValue<>(null, null);
    }

    @Value
    class KeyValue<K, V>{
        K key;
        V value;
        public Double getValueAsDouble(){
            return (Double) value;
        }

        public Long getValueAsLong(){
            return (Long) value;
        }

        public Integer getValueAsInt(){
            return (Integer) value;
        }
    }
}
