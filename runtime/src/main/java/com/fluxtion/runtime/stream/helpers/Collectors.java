package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateToList;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateToList.AggregateToListFactory;

public interface Collectors {

    static <T> SerializableSupplier<AggregateToList<T>> listFactory(int maximumElementCount) {
        return new AggregateToListFactory(maximumElementCount)::newList;
    }

    static <T> SerializableSupplier<AggregateToList<T>> listFactory() {
        return listFactory(-1);
    }
}
