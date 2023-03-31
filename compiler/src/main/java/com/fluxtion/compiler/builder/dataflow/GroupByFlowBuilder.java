package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.dataflow.Tuple;
import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;
import com.fluxtion.runtime.dataflow.function.BinaryMapFlowFunction.BinaryMapToRefFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapRef2RefFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import com.fluxtion.runtime.dataflow.groupby.GroupByFilterFlowFunctionWrapper;
import com.fluxtion.runtime.dataflow.groupby.GroupByMapFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.GroupByReduceFlowFunction;
import com.fluxtion.runtime.dataflow.helpers.DefaultValue;
import com.fluxtion.runtime.dataflow.helpers.DefaultValue.DefaultValueFromSupplier;
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import com.fluxtion.runtime.dataflow.helpers.Peekers;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

import java.util.Map;

public class GroupByFlowBuilder<K, V> extends AbstractGroupByBuilder<K, V, GroupBy<K, V>> {

    GroupByFlowBuilder(TriggeredFlowFunction<GroupBy<K, V>> eventStream) {
        super(eventStream);
    }

    <I, G extends GroupBy<K, V>>
    GroupByFlowBuilder(MapFlowFunction<I, GroupBy<K, V>, TriggeredFlowFunction<I>> eventStream) {
        super(eventStream);
    }

    @Override
    protected GroupByFlowBuilder<K, V> identity() {
        return this;
    }

    //
    public GroupByFlowBuilder<K, V> updateTrigger(Object updateTrigger) {
        eventStream.setUpdateTriggerNode(StreamHelper.getSource(updateTrigger));
        return identity();
    }

    public GroupByFlowBuilder<K, V> publishTrigger(Object publishTrigger) {
        eventStream.setPublishTriggerNode(StreamHelper.getSource(publishTrigger));
        return identity();
    }

    public GroupByFlowBuilder<K, V> publishTriggerOverride(Object publishTrigger) {
        eventStream.setPublishTriggerOverrideNode(StreamHelper.getSource(publishTrigger));
        return identity();
    }

    public GroupByFlowBuilder<K, V> resetTrigger(Object resetTrigger) {
        eventStream.setResetTriggerNode(StreamHelper.getSource(resetTrigger));
        return identity();
    }

    public GroupByFlowBuilder<K, V> defaultValue(GroupBy<K, V> defaultValue) {
        return new GroupByFlowBuilder<>(new MapRef2RefFlowFunction<>(eventStream,
                new DefaultValue<>(defaultValue)::getOrDefault));
    }

    public GroupByFlowBuilder<K, V> defaultValue(SerializableSupplier<GroupBy<K, V>> defaultValue) {
        return new GroupByFlowBuilder<>(new MapRef2RefFlowFunction<>(eventStream,
                new DefaultValueFromSupplier<>(defaultValue)::getOrDefault));
    }

    public <O> GroupByFlowBuilder<K, O> mapValues(SerializableFunction<V, O> mappingFunction) {
        return new GroupByFlowBuilder<>(new MapRef2RefFlowFunction<>(eventStream,
                new GroupByMapFlowFunction(mappingFunction)::mapValues));
    }

    public <K2 extends K, V2, VOUT>
    GroupByFlowBuilder<K, VOUT> biMapValuesByKey(
            SerializableBiFunction<V, V2, VOUT> mappingBiFunction,
            GroupByFlowBuilder<K2, V2> secondArgumentStream) {
        GroupByMapFlowFunction invoker = new GroupByMapFlowFunction(null, mappingBiFunction, null);
        return biMapValuesByKey(mappingBiFunction, secondArgumentStream, null);
    }

    public <K2 extends K, V2, VOUT>
    GroupByFlowBuilder<K, VOUT> biMapValuesByKey(
            SerializableBiFunction<V, V2, VOUT> mappingBiFunction,
            GroupByFlowBuilder<K2, V2> secondArgumentStream,
            V2 defaultSecondArgument) {
        GroupByMapFlowFunction invoker = new GroupByMapFlowFunction(null, mappingBiFunction, defaultSecondArgument);
        return mapBiFunction(invoker::biMapValuesWithParamMap, secondArgumentStream);
    }

    public <R, F extends AggregateFlowFunction<V, R, F>> FlowBuilder<R> reduceValues(
            SerializableSupplier<F> aggregateFactory) {
        return new FlowBuilder<>(new MapRef2RefFlowFunction<>(eventStream,
                new GroupByReduceFlowFunction(aggregateFactory.get())::reduceValues));
    }


    public <O> GroupByFlowBuilder<O, V> mapKeys(SerializableFunction<K, O> mappingFunction) {
        return new GroupByFlowBuilder<>(new MapRef2RefFlowFunction<>(eventStream,
                new GroupByMapFlowFunction(mappingFunction)::mapKeys));
    }

    public <K1, V1, G extends Map.Entry<K, V>> GroupByFlowBuilder<K1, V1> mapEntries(
            SerializableFunction<G, Map.Entry<K1, V1>> mappingFunction) {
        return new GroupByFlowBuilder<>(new MapRef2RefFlowFunction<>(eventStream,
                new GroupByMapFlowFunction(mappingFunction)::mapEntry));
    }

    public <O> GroupByFlowBuilder<K, V> filterValues(SerializableFunction<V, Boolean> mappingFunction) {
        return new GroupByFlowBuilder<>(new MapRef2RefFlowFunction<>(eventStream,
                new GroupByFilterFlowFunctionWrapper(mappingFunction)::filterValues));
    }

    public <K2 extends K, V2> GroupByFlowBuilder<K, Tuple<V, V2>> innerJoin(GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return mapBiFunction(Mappers::innerJoin, rightGroupBy);
    }

    public <K2 extends K, V2> GroupByFlowBuilder<K, Tuple<V, V2>> outerJoin(GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return mapBiFunction(Mappers::outerJoin, rightGroupBy);
    }

    public <K2 extends K, V2> GroupByFlowBuilder<K, Tuple<V, V2>> leftJoin(GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return mapBiFunction(Mappers::leftJoin, rightGroupBy);
    }

    public <K2 extends K, V2> GroupByFlowBuilder<K, Tuple<V, V2>> rightJoin(GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return mapBiFunction(Mappers::rightJoin, rightGroupBy);
    }

    public <K2 extends K, V2, KOUT, VOUT>
    GroupByFlowBuilder<KOUT, VOUT> mapBiFunction(
            SerializableBiFunction<GroupBy<K, V>, GroupBy<K2, V2>, GroupBy<KOUT, VOUT>> int2IntFunction,
            GroupByFlowBuilder<K2, V2> stream2Builder) {
        return new GroupByFlowBuilder<>(
                new BinaryMapToRefFlowFunction<>(
                        eventStream, stream2Builder.eventStream, int2IntFunction)
        );
    }


    public GroupByFlowBuilder<K, V> console(String in) {
        peek(Peekers.console(in, null));
        return identity();
    }

    public GroupByFlowBuilder<K, V> console() {
        return console("{}");
    }


    //META-DATA
    public GroupByFlowBuilder<K, V> id(String nodeId) {
        EventProcessorBuilderService.service().add(eventStream, nodeId);
        return identity();
    }
}
