package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.AggregateFunction;
import com.fluxtion.runtime.stream.GroupByStreamed;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.Tuple;
import com.fluxtion.runtime.stream.groupby.FilterGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.groupby.MapGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.groupby.ReduceGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.helpers.DefaultValue;
import com.fluxtion.runtime.stream.helpers.DefaultValue.DefaultValueFromSupplier;
import com.fluxtion.runtime.stream.helpers.Mappers;
import com.fluxtion.runtime.stream.helpers.Peekers;
import com.fluxtion.runtime.stream.impl.BinaryMapEventStream;
import com.fluxtion.runtime.stream.impl.MapEventStream;

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

    //
    public GroupByStreamBuilder<K, V> updateTrigger(Object updateTrigger) {
        eventStream.setUpdateTriggerNode(StreamHelper.getSource(updateTrigger));
        return identity();
    }

    public GroupByStreamBuilder<K, V> publishTrigger(Object publishTrigger) {
        eventStream.setPublishTriggerNode(StreamHelper.getSource(publishTrigger));
        return identity();
    }

    public GroupByStreamBuilder<K, V> publishTriggerOverride(Object publishTrigger) {
        eventStream.setPublishTriggerOverrideNode(StreamHelper.getSource(publishTrigger));
        return identity();
    }

    public GroupByStreamBuilder<K, V> resetTrigger(Object resetTrigger) {
        eventStream.setResetTriggerNode(StreamHelper.getSource(resetTrigger));
        return identity();
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

    public <K2 extends K, V2, VOUT>
    GroupByStreamBuilder<K, VOUT> biMapValuesByKey(
            SerializableBiFunction<V, V2, VOUT> mappingBiFunction,
            GroupByStreamBuilder<K2, V2> secondArgumentStream) {
        MapGroupByFunctionInvoker invoker = new MapGroupByFunctionInvoker(null, mappingBiFunction, null);
        return biMapValuesByKey(mappingBiFunction, secondArgumentStream, null);
    }

    public <K2 extends K, V2, VOUT>
    GroupByStreamBuilder<K, VOUT> biMapValuesByKey(
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


    public GroupByStreamBuilder<K, V> console(String in) {
        peek(Peekers.console(in, null));
        return identity();
    }

    public GroupByStreamBuilder<K, V> console() {
        return console("{}");
    }


    //META-DATA
    public GroupByStreamBuilder<K, V> id(String nodeId) {
        EventProcessorBuilderService.service().add(eventStream, nodeId);
        return identity();
    }
}
