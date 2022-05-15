package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateCounting;

public class Aggregates {

    public  static <T> SerializableSupplier<AggregateCounting<T>> counting(){
        return AggregateCounting::new;
    }
}
