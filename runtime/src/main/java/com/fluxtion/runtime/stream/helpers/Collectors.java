package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIdentity;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateToList;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateToList.AggregateToListFactory;
import com.fluxtion.runtime.stream.groupby.GroupByKeyFactory;
import com.fluxtion.runtime.stream.groupby.GroupByWindowedCollection;

import java.util.List;

public interface Collectors {

    static <T> SerializableSupplier<AggregateToList<T>> toList(int maximumElementCount) {
        return new AggregateToListFactory(maximumElementCount)::newList;
    }

    static <T> SerializableSupplier<AggregateToList<T>> toList() {
        return toList(-1);
    }

    static <T, K> SerializableSupplier<GroupByWindowedCollection<T, K, T, List<T>, AggregateToList<T>>> groupByAsList(SerializableFunction<T, K> keyFunction) {
        GroupByKeyFactory<T, K> factory = new GroupByKeyFactory<>(keyFunction);
        return factory::groupByToListFactory;
    }

    static <T, K> SerializableSupplier<GroupByWindowedCollection<T, K, T, T, AggregateIdentity<T>>> groupBy(SerializableFunction<T, K> keyFunction) {
        GroupByKeyFactory<T, K> factory = new GroupByKeyFactory<>(keyFunction);
        return factory::groupByFactory;
    }
}
