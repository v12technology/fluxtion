package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.groupby.GroupBy.KeyValue;
import com.fluxtion.runtime.stream.groupby.GroupByCollection;

public interface Collectors {

    static <K, V> SerializableFunction<KeyValue<K, V>, GroupByCollection<K, V>> toGroupBy(){
        return new GroupByCollection<K, V>()::add;
    }
}
