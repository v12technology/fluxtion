package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateCounting;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateDoubleSum;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateDoubleValue;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIdentity;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIntSum;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIntValue;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateLongSum;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateLongValue;

public class Aggregates {

    public  static <T> SerializableSupplier<AggregateIdentity<T>> identity(){
        return AggregateIdentity::new;
    }

    public static SerializableSupplier<AggregateIntValue> intIdentity(){
        return AggregateIntValue::new;
    }

    public static SerializableSupplier<AggregateDoubleValue> doubleIdentity(){
        return AggregateDoubleValue::new;
    }

    public static SerializableSupplier<AggregateLongValue> longIdentity(){
        return AggregateLongValue::new;
    }

    public  static <T> SerializableSupplier<AggregateCounting<T>> counting(){
        return AggregateCounting::new;
    }

    public static SerializableSupplier<AggregateIntSum> intSum(){
        return AggregateIntSum::new;
    }

    public static SerializableSupplier<AggregateDoubleSum> doubleSum(){
        return AggregateDoubleSum::new;
    }

    public static SerializableSupplier<AggregateLongSum> longSum(){
        return AggregateLongSum::new;
    }
}
