package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.stream.groupby.GroupBy;
import com.fluxtion.runtime.stream.groupby.MapGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.groupby.Tuple;
import com.fluxtion.runtime.stream.helpers.Mappers;

public interface GroupByStreamBuilder {

    static <K1, K2 extends K1, V1, V2, R, G extends GroupBy<K1, V1>, H extends GroupBy<K2, V2>>
    EventStreamBuilder<GroupBy<K1, R>> biMapStreams(
            SerializableBiFunction<V1, V2, R> mappingBiFunction,
            EventStreamBuilder<G> firstArgument,
            EventStreamBuilder<H> secondArgument
    ) {
        MapGroupByFunctionInvoker invoker = new MapGroupByFunctionInvoker(null, mappingBiFunction, null);
        return firstArgument.mapBiFunction(invoker::biMapValuesWithParamMap, secondArgument);
    }

    static <K1, K2 extends K1, V1, V2, R, G extends GroupBy<K1, V1>, H extends GroupBy<K2, V2>>
    EventStreamBuilder<GroupBy<K1, R>> biMapStreams(
            SerializableBiFunction<V1, V2, R> mappingBiFunction,
            EventStreamBuilder<G> firstArgument,
            EventStreamBuilder<H> secondArgument,
            V2 defaultSecondArgument
    ) {
        MapGroupByFunctionInvoker invoker = new MapGroupByFunctionInvoker(null, mappingBiFunction, defaultSecondArgument);
        return firstArgument.mapBiFunction(invoker::biMapValuesWithParamMap, secondArgument);
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

    static <K1, V1, K2 extends K1, V2> EventStreamBuilder<GroupBy<K1, Tuple<V1, V2>>> leftJoinStreams(
            EventStreamBuilder<? extends GroupBy<K1, V1>> leftGroupBy,
            EventStreamBuilder<? extends GroupBy<K2, V2>> rightGroupBy) {
        return leftGroupBy.mapBiFunction(Mappers::leftJoin, rightGroupBy);
    }

    static <K1, V1, K2 extends K1, V2> EventStreamBuilder<GroupBy<K1, Tuple<V1, V2>>> rightJoinStreams(
            EventStreamBuilder<? extends GroupBy<K1, V1>> leftGroupBy,
            EventStreamBuilder<? extends GroupBy<K2, V2>> rightGroupBy) {
        return leftGroupBy.mapBiFunction(Mappers::rightJoin, rightGroupBy);
    }
}
