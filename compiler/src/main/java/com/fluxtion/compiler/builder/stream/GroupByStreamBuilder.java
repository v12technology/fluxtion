package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.BinaryMapEventStream;
import com.fluxtion.runtime.stream.MapEventStream;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.aggregate.AggregateFunction;
import com.fluxtion.runtime.stream.groupby.FilterGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.groupby.GroupBy;
import com.fluxtion.runtime.stream.groupby.GroupBy.KeyValue;
import com.fluxtion.runtime.stream.groupby.GroupByStreamed;
import com.fluxtion.runtime.stream.groupby.MapGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.groupby.ReduceGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.groupby.Tuple;
import com.fluxtion.runtime.stream.helpers.DefaultValue;
import com.fluxtion.runtime.stream.helpers.DefaultValue.DefaultValueFromSupplier;
import com.fluxtion.runtime.stream.helpers.Mappers;

import java.util.Map;

public class GroupByStreamBuilder<K, V> extends AbstractGroupByBuilder<K, V, GroupByStreamed<K, V>> {

    GroupByStreamBuilder(TriggeredEventStream<GroupByStreamed<K, V>> eventStream) {
        super(eventStream);
    }

    <I, G extends GroupByStreamed<K, V>>
    GroupByStreamBuilder(MapEventStream<I, GroupByStreamed<K, V>, TriggeredEventStream<I>> eventStream) {
        super(eventStream);
    }

    @Override
    protected GroupByStreamBuilder<K, V> identity() {
        return this;
    }

    public GroupByStreamBuilder<K, V> defaultValue(GroupByStreamed<K, V> defaultValue) {
        return new GroupByStreamBuilder<>(new MapEventStream.MapRef2RefEventStream<>(eventStream,
                new DefaultValue<>(defaultValue)::getOrDefault));
    }

    public GroupByStreamBuilder<K, V> defaultValue(SerializableSupplier<GroupByStreamed<K, V>> defaultValue) {
        return new GroupByStreamBuilder<>(new MapEventStream.MapRef2RefEventStream<>(eventStream,
                new DefaultValueFromSupplier<>(defaultValue)::getOrDefault));
    }

    public <O> GroupByStreamBuilder<K, O> mapValues(SerializableFunction<V, O> mappingFunction) {
        return new GroupByStreamBuilder<>(new MapEventStream.MapRef2RefEventStream<>(eventStream,
                new MapGroupByFunctionInvoker(mappingFunction)::mapValues));
    }

    public <K2 extends K, V2, O> GroupByStreamBuilder<K, O> mapValuesByKey(
            SerializableBiFunction<V, V2, O> mappingBiFunction,
            EventStreamBuilder<KeyValue<K2, V2>> argumentStream) {
        MapGroupByFunctionInvoker invoker = new MapGroupByFunctionInvoker(mappingBiFunction);
        return new GroupByStreamBuilder<>(
                new BinaryMapEventStream.BinaryMapToRefEventStream<>(
                        eventStream, argumentStream.eventStream, invoker::mapValueWithKeyValue)
        );
    }

    public <K2 extends K, V2, VOUT>
    GroupByStreamBuilder<K, VOUT> biMapValues(
            SerializableBiFunction<V, V2, VOUT> mappingBiFunction,
            GroupByStreamBuilder<K2, V2> secondArgumentStream) {
        MapGroupByFunctionInvoker invoker = new MapGroupByFunctionInvoker(null, mappingBiFunction, null);
        return biMapValues(mappingBiFunction, secondArgumentStream, null);
    }

    public <K2 extends K, V2, VOUT>
    GroupByStreamBuilder<K, VOUT> biMapValues(
            SerializableBiFunction<V, V2, VOUT> mappingBiFunction,
            GroupByStreamBuilder<K2, V2> secondArgumentStream,
            V2 defaultSecondArgument) {
        MapGroupByFunctionInvoker invoker = new MapGroupByFunctionInvoker(null, mappingBiFunction, defaultSecondArgument);
        return mapBiFunction(invoker::biMapValuesWithParamMap, secondArgumentStream);
    }

    public <R, F extends AggregateFunction<V, R, F>> EventStreamBuilder<R> reduceValues(
            SerializableSupplier<F> aggregateFactory) {
        return new EventStreamBuilder<>(new MapEventStream.MapRef2RefEventStream<>(eventStream,
                new ReduceGroupByFunctionInvoker(aggregateFactory.get())::reduceValues));
    }


    public <O> GroupByStreamBuilder<O, V> mapKeys(SerializableFunction<K, O> mappingFunction) {
        return new GroupByStreamBuilder<>(new MapEventStream.MapRef2RefEventStream<>(eventStream,
                new MapGroupByFunctionInvoker(mappingFunction)::mapKeys));
    }

    public <K1, V1, G extends Map.Entry<K, V>> GroupByStreamBuilder<K1, V1> mapEntries(
            SerializableFunction<G, Map.Entry<K1, V1>> mappingFunction) {
        return new GroupByStreamBuilder<>(new MapEventStream.MapRef2RefEventStream<>(eventStream,
                new MapGroupByFunctionInvoker(mappingFunction)::mapEntry));
    }

    public <O> GroupByStreamBuilder<K, V> filterValues(SerializableFunction<V, Boolean> mappingFunction) {
        return new GroupByStreamBuilder<>(new MapEventStream.MapRef2RefEventStream<>(eventStream,
                new FilterGroupByFunctionInvoker(mappingFunction)::filterValues));
    }

    public <K2 extends K, V2> GroupByStreamBuilder<K, Tuple<V, V2>> innerJoin(GroupByStreamBuilder<K2, V2> rightGroupBy) {
        return mapBiFunction(Mappers::innerJoin, rightGroupBy);
    }

    public <K2 extends K, V2> GroupByStreamBuilder<K, Tuple<V, V2>> outerJoin(GroupByStreamBuilder<K2, V2> rightGroupBy) {
        return mapBiFunction(Mappers::outerJoin, rightGroupBy);
    }

    public <K2 extends K, V2> GroupByStreamBuilder<K, Tuple<V, V2>> leftJoin(GroupByStreamBuilder<K2, V2> rightGroupBy) {
        return mapBiFunction(Mappers::leftJoin, rightGroupBy);
    }

    public <K2 extends K, V2> GroupByStreamBuilder<K, Tuple<V, V2>> rightJoin(GroupByStreamBuilder<K2, V2> rightGroupBy) {
        return mapBiFunction(Mappers::rightJoin, rightGroupBy);
    }

    public <K2 extends K, V2, KOUT, VOUT>
    GroupByStreamBuilder<KOUT, VOUT> mapBiFunction(
            SerializableBiFunction<GroupByStreamed<K, V>, GroupByStreamed<K2, V2>, GroupByStreamed<KOUT, VOUT>> int2IntFunction,
            GroupByStreamBuilder<K2, V2> stream2Builder) {
        return new GroupByStreamBuilder<>(
                new BinaryMapEventStream.BinaryMapToRefEventStream<>(
                        eventStream, stream2Builder.eventStream, int2IntFunction)
        );
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

    static <K1, V1, K2 extends K1, V2> GroupByStreamBuilder<K1, Tuple<V1, V2>> outerJoinStreams(
            GroupByStreamBuilder<K1, V1> leftGroupBy,
            GroupByStreamBuilder<K2, V2> rightGroupBy) {
        return leftGroupBy.mapBiFunction(Mappers::outerJoin, rightGroupBy);
    }

    static <K1, V1, K2 extends K1, V2> GroupByStreamBuilder<K1, Tuple<V1, V2>> leftJoinStreams(
            GroupByStreamBuilder<K1, V1> leftGroupBy,
            GroupByStreamBuilder<K2, V2> rightGroupBy) {
        return leftGroupBy.mapBiFunction(Mappers::leftJoin, rightGroupBy);
    }

    static <K1, V1, K2 extends K1, V2> GroupByStreamBuilder<K1, Tuple<V1, V2>> rightJoinStreams(
            GroupByStreamBuilder<K1, V1> leftGroupBy,
            GroupByStreamBuilder<K2, V2> rightGroupBy) {
        return leftGroupBy.mapBiFunction(Mappers::rightJoin, rightGroupBy);
    }
}
