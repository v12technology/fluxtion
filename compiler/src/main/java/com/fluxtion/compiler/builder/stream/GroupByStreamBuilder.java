package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.BinaryMapEventStream;
import com.fluxtion.runtime.stream.MapEventStream;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.groupby.GroupBy;
import com.fluxtion.runtime.stream.groupby.GroupByStreamed;
import com.fluxtion.runtime.stream.groupby.MapGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.groupby.Tuple;
import com.fluxtion.runtime.stream.helpers.Mappers;

public class GroupByStreamBuilder<K, V> extends AbstractGroupByBuilder<K, V, GroupByStreamed<K, V>> {

    GroupByStreamBuilder(TriggeredEventStream<GroupByStreamed<K, V>> eventStream) {
        super(eventStream);
    }

    <I, G extends GroupByStreamed<K, V>>
    GroupByStreamBuilder(MapEventStream<I, GroupByStreamed<K, V>, TriggeredEventStream<I>> eventStream) {
        super(eventStream);
    }

    public <O> GroupByStreamBuilder<K, O> mapValues(SerializableFunction<V, O> mappingFunction){
       return  new GroupByStreamBuilder<>(new MapEventStream.MapRef2RefEventStream<>(eventStream,
                new MapGroupByFunctionInvoker(mappingFunction)::mapValues));
    }


    public <O> GroupByStreamBuilder<O, V> mapKeys(SerializableFunction<K, O> mappingFunction){
        return  new GroupByStreamBuilder<>(new MapEventStream.MapRef2RefEventStream<>(eventStream,
                new MapGroupByFunctionInvoker(mappingFunction)::mapKeys));
    }

    public <K2 extends K, V2> GroupByStreamBuilder<K, Tuple<V, V2>> innerJoin(GroupByStreamBuilder<K2, V2> rightGroupBy){
        return mapBiFunction(Mappers::innerJoin, rightGroupBy);
    }
    public <K2 extends K, V2, KOUT, VOUT>
    GroupByStreamBuilder<KOUT, VOUT> mapBiFunction(SerializableBiFunction<GroupByStreamed<K, V>, GroupByStreamed<K2, V2>,  GroupByStreamed<KOUT, VOUT> > int2IntFunction,
                                                        GroupByStreamBuilder<K2, V2> stream2Builder) {
        return new GroupByStreamBuilder<>(
                new BinaryMapEventStream.BinaryMapToRefEventStream<>(
                        eventStream, stream2Builder.eventStream, int2IntFunction)
        );
    }

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

    static <K1, V1, K2 extends K1, V2> GroupByStreamBuilder<K1, Tuple<V1, V2>> innerJoinStreams(
            GroupByStreamBuilder<K1, V1> leftGroupBy,
            GroupByStreamBuilder<K2, V2> rightGroupBy) {
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
