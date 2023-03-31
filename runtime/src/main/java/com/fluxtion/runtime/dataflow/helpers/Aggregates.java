package com.fluxtion.runtime.dataflow.helpers;

import com.fluxtion.runtime.dataflow.aggregate.function.AggregateIdentityFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.CountFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.DoubleAverageFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.DoubleIdentityFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.DoubleMaxFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.DoubleMinFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.DoubleSumFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.IntAverageFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.IntIdentityFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.IntMaxFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.IntMinFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.IntSumFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.LongAverageFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.LongIdentityFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.LongMaxFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.LongMinFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.LongSumFlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

public class Aggregates {

    public static <T> SerializableSupplier<AggregateIdentityFlowFunction<T>> identityFactory() {
        return AggregateIdentityFlowFunction::new;
    }

    public static SerializableSupplier<IntIdentityFlowFunction> intIdentityFactory() {
        return IntIdentityFlowFunction::new;
    }

    public static SerializableSupplier<DoubleIdentityFlowFunction> doubleIdentityFactory() {
        return DoubleIdentityFlowFunction::new;
    }

    public static SerializableSupplier<LongIdentityFlowFunction> longIdentityFactory() {
        return LongIdentityFlowFunction::new;
    }

    public static <T> SerializableSupplier<CountFlowFunction<T>> countFactory() {
        return CountFlowFunction::new;
    }

    //SUM
    public static SerializableSupplier<IntSumFlowFunction> intSumFactory() {
        return IntSumFlowFunction::new;
    }

    public static SerializableSupplier<DoubleSumFlowFunction> doubleSumFactory() {
        return DoubleSumFlowFunction::new;
    }

    public static SerializableSupplier<LongSumFlowFunction> longSumFactory() {
        return LongSumFlowFunction::new;
    }

    //max
    public static SerializableSupplier<IntMaxFlowFunction> intMaxFactory() {
        return IntMaxFlowFunction::new;
    }

    public static SerializableSupplier<LongMaxFlowFunction> longMaxFactory() {
        return LongMaxFlowFunction::new;
    }

    public static SerializableSupplier<DoubleMaxFlowFunction> doubleMaxFactory() {
        return DoubleMaxFlowFunction::new;
    }

    //min
    public static SerializableSupplier<IntMinFlowFunction> intMinFactory() {
        return IntMinFlowFunction::new;
    }

    public static SerializableSupplier<LongMinFlowFunction> longMinFactory() {
        return LongMinFlowFunction::new;
    }

    public static SerializableSupplier<DoubleMinFlowFunction> doubleMinFactory() {
        return DoubleMinFlowFunction::new;
    }

    //AVERAGE
    public static SerializableSupplier<IntAverageFlowFunction> intAverageFactory() {
        return IntAverageFlowFunction::new;
    }

    public static SerializableSupplier<DoubleAverageFlowFunction> doubleAverageFactory() {
        return DoubleAverageFlowFunction::new;
    }

    public static SerializableSupplier<LongAverageFlowFunction> longAverageFactory() {
        return LongAverageFlowFunction::new;
    }
}
