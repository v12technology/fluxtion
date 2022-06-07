package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
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
import com.fluxtion.runtime.stream.groupby.GroupBy;
import com.fluxtion.runtime.stream.groupby.TopNByValue;

import java.util.List;
import java.util.Map.Entry;

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

    //max
    public static SerializableSupplier<AggregateIntMax> intMax(){
        return AggregateIntMax::new;
    }

    public static SerializableSupplier<AggregateLongMax> longMax(){
        return AggregateLongMax::new;
    }

    public static SerializableSupplier<AggregateDoubleMax> doubleMax(){
        return AggregateDoubleMax::new;
    }

    //min
    public static SerializableSupplier<AggregateIntMin> intMin(){
        return AggregateIntMin::new;
    }

    public static SerializableSupplier<AggregateLongMin> longMin(){
        return AggregateLongMin::new;
    }

    public static SerializableSupplier<AggregateDoubleMin> doubleMin(){
        return AggregateDoubleMin::new;
    }

    public static <K, V extends Comparable<V>> SerializableFunction<GroupBy<K, V>, List<Entry<K, V>>> topNByValue(int count) {
        return new TopNByValue(count)::filter;
    }

    public static <K, V, T extends Comparable<T>> SerializableFunction<GroupBy<K, V>, List<Entry<K, V>>> topNByValue(
            int count, SerializableFunction<V, T> propertyAccesor) {
        TopNByValue topNByValue = new TopNByValue(count);
        topNByValue.comparing = propertyAccesor;
        return topNByValue::filter;
    }
}
