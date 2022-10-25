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
import com.fluxtion.runtime.stream.helpers.Mappers;

import java.util.Map;

public interface GroupByFunction {

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

    static <K, V, A, O, G extends GroupBy<K, V>, H extends GroupBy<K, A>> SerializableBiFunction<
            G, H, GroupBy<K, O>> biMapWithParamMap(
            SerializableBiFunction<V, A, O> mappingBiFunction
    ) {
        MapGroupByFunctionInvoker invoker = new MapGroupByFunctionInvoker(null, mappingBiFunction, null);
        return invoker::biMapWithParamMap;
    }

    static <K, V, A, O, G extends GroupBy<K, V>, H extends GroupBy<K, A>> SerializableBiFunction<
            G, H, GroupBy<K, O>> biMapWithParamMap(
            SerializableBiFunction<V, A, O> mappingBiFunction, A defaultValue
    ) {
        MapGroupByFunctionInvoker invoker = new MapGroupByFunctionInvoker(null, mappingBiFunction, defaultValue);
        return invoker::biMapWithParamMap;
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

    static <K1, V1, K2 extends K1, V2> EventStreamBuilder<GroupBy<K1, Tuple<V1, V2>>> innerJoinStreams(
            EventStreamBuilder<? extends GroupBy<K1, V1>> leftGroupBy,
            EventStreamBuilder<? extends GroupBy<K2, V2>> rightGroupBy) {
        return leftGroupBy.mapBiFunction(Mappers::innerJoin, rightGroupBy);
    }

    static <K1, V1, K2 extends K1, V2> EventStreamBuilder<GroupBy<K1, Tuple<V1, V2>>> outerJoinStreams(
            EventStreamBuilder<? extends GroupBy<K1, V1>> leftGroupBy,
            EventStreamBuilder<? extends GroupBy<K2, V2>> rightGroupBy) {
        return leftGroupBy.mapBiFunction(Mappers::outerJoin, rightGroupBy);
    }
}
