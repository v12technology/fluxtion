package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateCounting;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateDoubleMax;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateDoubleMin;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateDoubleSum;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateDoubleValue;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIdentity;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIntMax;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIntMin;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIntSum;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIntValue;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateLongMax;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateLongMin;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateLongSum;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateLongValue;

public class Aggregates {

    public static <T> SerializableSupplier<AggregateIdentity<T>> identityFactory() {
        return AggregateIdentity::new;
    }

    public static SerializableSupplier<AggregateIntValue> intIdentityFactory() {
        return AggregateIntValue::new;
    }

    public static SerializableSupplier<AggregateDoubleValue> doubleIdentityFactory() {
        return AggregateDoubleValue::new;
    }

    public static SerializableSupplier<AggregateLongValue> longIdentityFactory() {
        return AggregateLongValue::new;
    }

    public static <T> SerializableSupplier<AggregateCounting<T>> countFactory() {
        return AggregateCounting::new;
    }

    public static SerializableSupplier<AggregateIntSum> intSumFactory() {
        return AggregateIntSum::new;
    }

    public static SerializableSupplier<AggregateDoubleSum> doubleSumFactory() {
        return AggregateDoubleSum::new;
    }

    public static SerializableSupplier<AggregateLongSum> longSumFactory() {
        return AggregateLongSum::new;
    }

    //max
    public static SerializableSupplier<AggregateIntMax> intMaxFactory() {
        return AggregateIntMax::new;
    }

    public static SerializableSupplier<AggregateLongMax> longMaxFactory() {
        return AggregateLongMax::new;
    }

    public static SerializableSupplier<AggregateDoubleMax> doubleMaxFactory() {
        return AggregateDoubleMax::new;
    }

    //min
    public static SerializableSupplier<AggregateIntMin> intMinFactory() {
        return AggregateIntMin::new;
    }

    public static SerializableSupplier<AggregateLongMin> longMinFactory() {
        return AggregateLongMin::new;
    }

    public static SerializableSupplier<AggregateDoubleMin> doubleMinFactory() {
        return AggregateDoubleMin::new;
    }

}
