package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.aggregate.AggregateFunction;
import com.fluxtion.runtime.stream.groupby.FilterGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.groupby.GroupBy;
import com.fluxtion.runtime.stream.groupby.GroupByStreamed;
import com.fluxtion.runtime.stream.groupby.MapGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.groupby.ReduceGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.groupby.Tuple;
import com.fluxtion.runtime.stream.helpers.Tuples;
import com.fluxtion.runtime.stream.helpers.Tuples.ReplaceNull;

import java.util.Map;

public interface GroupByFunction {


    static <K, F, S, TIN extends Tuple<? extends F, ? extends S>>
    SerializableFunction<GroupBy<K, TIN>, GroupBy<K, Tuple<F, S>>> replaceTupleNullInGroupBy(F first, S second) {
        return mapValues(new ReplaceNull<>(first, second)::replaceNull);
    }

    static <K, F, S, R>
    SerializableFunction<GroupBy<K, Tuple<F, S>>, GroupBy<K, R>> mapTuplesInGroupBy(SerializableBiFunction<F, S, R> tupleMapFunction) {
        return mapValues(Tuples.mapTuple(tupleMapFunction));
    }


    static <K, V, O, G extends GroupBy<K, V>> SerializableFunction<G, GroupBy<K, O>> mapValues(
            SerializableFunction<V, O> mappingFunction) {
        return new MapGroupByFunctionInvoker(mappingFunction)::mapValues;
    }

    static <K, V, A, O, G extends GroupBy<K, V>> SerializableBiFunction<
            G, A, GroupByStreamed<K, O>> mapValueByKey(
            SerializableBiFunction<V, A, O> mappingBiFunction,
            SerializableFunction<A, K> keyFunction
    ) {
        MapGroupByFunctionInvoker invoker = new MapGroupByFunctionInvoker(keyFunction, mappingBiFunction, null);
        return invoker::mapKeyedValue;
    }

    static <K, V, R, G extends GroupBy<K, V>, F extends AggregateFunction<V, R, F>> SerializableFunction<G, R> reduceValues(
            SerializableSupplier<F> aggregateFactory) {
        return new ReduceGroupByFunctionInvoker(aggregateFactory.get())::reduceValues;
    }

    static <K, V, A, O, G extends GroupBy<K, V>, H extends GroupBy<K, A>>
    SerializableBiFunction<G, H, GroupBy<K, O>> biMapFunction(SerializableBiFunction<V, A, O> mappingBiFunction) {
        MapGroupByFunctionInvoker invoker = new MapGroupByFunctionInvoker(null, mappingBiFunction, null);
        return invoker::biMapValuesWithParamMap;
    }

    static <K, V, A, O, G extends GroupBy<K, V>, H extends GroupBy<K, A>>
    SerializableBiFunction<G, H, GroupBy<K, O>> biMapFunction(
            SerializableBiFunction<V, A, O> mappingBiFunction, A defaultValue) {
        MapGroupByFunctionInvoker invoker = new MapGroupByFunctionInvoker(null, mappingBiFunction, defaultValue);
        return invoker::biMapValuesWithParamMap;
    }

    static <K, V, O, G extends GroupBy<K, V>> SerializableFunction<G, GroupBy<O, V>> mapKeys(
            SerializableFunction<K, O> mappingFunction) {
        return new MapGroupByFunctionInvoker(mappingFunction)::mapKeys;
    }

    static <K, V, K1, V1, G extends GroupBy<K, V>> SerializableFunction<G, GroupBy<K1, V1>> mapEntry(
            SerializableFunction<Map.Entry<K, V>, Map.Entry<K1, V1>> mappingFunction) {
        return new MapGroupByFunctionInvoker(mappingFunction)::mapEntry;
    }

    static <K, V, G extends GroupBy<K, V>> SerializableFunction<G, GroupBy<K, V>> filterValues(
            SerializableFunction<V, Boolean> mappingFunction) {
        return new FilterGroupByFunctionInvoker(mappingFunction)::filterValues;
    }

}
