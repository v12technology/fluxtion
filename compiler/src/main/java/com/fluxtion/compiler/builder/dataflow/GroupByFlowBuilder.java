package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.dataflow.Tuple;
import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;
import com.fluxtion.runtime.dataflow.function.BinaryMapFlowFunction.BinaryMapToRefFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapRef2RefFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.*;
import com.fluxtion.runtime.dataflow.helpers.DefaultValue;
import com.fluxtion.runtime.dataflow.helpers.DefaultValue.DefaultValueFromSupplier;
import com.fluxtion.runtime.dataflow.helpers.Peekers;
import com.fluxtion.runtime.dataflow.helpers.Tuples;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

import java.util.Collection;
import java.util.Collections;
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

    public GroupByFlowBuilder<K, V> updateTrigger(Object... publishTrigger) {
        eventStream.setUpdateTriggerNode(PredicateBuilder.anyTriggered(publishTrigger));
        return identity();
    }

    public GroupByFlowBuilder<K, V> publishTrigger(Object publishTrigger) {
        eventStream.setPublishTriggerNode(StreamHelper.getSource(publishTrigger));
        return identity();
    }

    public GroupByFlowBuilder<K, V> publishTrigger(Object... publishTrigger) {
        eventStream.setPublishTriggerNode(PredicateBuilder.anyTriggered(publishTrigger));
        return identity();
    }

    public GroupByFlowBuilder<K, V> publishTriggerOverride(Object publishTrigger) {
        eventStream.setPublishTriggerOverrideNode(StreamHelper.getSource(publishTrigger));
        return identity();
    }

    public GroupByFlowBuilder<K, V> publishTriggerOverride(Object... publishTrigger) {
        eventStream.setPublishTriggerOverrideNode(PredicateBuilder.anyTriggered(publishTrigger));
        return identity();
    }

    public GroupByFlowBuilder<K, V> resetTrigger(Object resetTrigger) {
        eventStream.setResetTriggerNode(StreamHelper.getSource(resetTrigger));
        return identity();
    }

    public GroupByFlowBuilder<K, V> resetTrigger(Object... publishTrigger) {
        eventStream.setResetTriggerNode(PredicateBuilder.anyTriggered(publishTrigger));
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

    /**
     * @param supplierOfIdsToDelete a data flow of id's to delete
     * @return The GroupByFlowBuilder with delete function applied
     * @see #deleteByKey(FlowBuilder, boolean)
     */
    public GroupByFlowBuilder<K, V> deleteByKey(SerializableSupplier<Collection<K>> supplierOfIdsToDelete) {
        return deleteByKey(DataFlow.subscribeToNodeProperty(supplierOfIdsToDelete), false);
    }

    /**
     * @param supplierOfIdsToDelete       a data flow of id's to delete
     * @param clearDeleteIdsAfterApplying flag to clear the delete id's after applying
     * @return The GroupByFlowBuilder with delete function applied
     * @see #deleteByKey(FlowBuilder, boolean)
     */
    public GroupByFlowBuilder<K, V> deleteByKey(SerializableSupplier<Collection<K>> supplierOfIdsToDelete, boolean clearDeleteIdsAfterApplying) {
        return deleteByKey(DataFlow.subscribeToNodeProperty(supplierOfIdsToDelete), clearDeleteIdsAfterApplying);
    }

    /**
     * @param supplierOfIdsToDelete a data flow of id's to delete
     * @return The GroupByFlowBuilder with delete function applied
     * @see #deleteByKey(FlowBuilder, boolean)
     */
    public GroupByFlowBuilder<K, V> deleteByKey(FlowBuilder<Collection<K>> supplierOfIdsToDelete) {
        return deleteByKey(supplierOfIdsToDelete, false);
    }

    /**
     * Deletes items from a {@link GroupBy} collection by their id's. A supplier of keys to delete is applied to the
     * GroupBy. The collection of id's can be cleared after applying or remain in place with the clearDeleteIdsAfterApplying
     *
     * @param supplierOfIdsToDelete       a data flow of id's to delete
     * @param clearDeleteIdsAfterApplying flag to clear the delete id's after applying
     * @return The GroupByFlowBuilder with delete function applied
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public GroupByFlowBuilder<K, V> deleteByKey(FlowBuilder<Collection<K>> supplierOfIdsToDelete, boolean clearDeleteIdsAfterApplying) {
        return new GroupByFlowBuilder<>(
                new BinaryMapToRefFlowFunction<>(
                        eventStream,
                        supplierOfIdsToDelete.defaultValue(Collections::emptyList).eventStream,
                        new GroupByDeleteByKeyFlowFunction(supplierOfIdsToDelete.flowSupplier(), clearDeleteIdsAfterApplying)::deleteByKey))
                .defaultValue(new GroupBy.EmptyGroupBy<>());
    }

    /**
     * Deletes items from a {@link GroupBy} collection using a predicate function applied to an elements value.
     *
     * @param deletePredicateFunction the predicate function that determines if an element should  be deleted. Deletes if returns true
     * @return The GroupByFlowBuilder with delete function applied
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public GroupByFlowBuilder<K, V> deleteByValue(SerializableFunction<V, Boolean> deletePredicateFunction) {
        Object functionInstance = deletePredicateFunction.captured()[0];
        FlowBuilder<Object> deleteTestFlow = DataFlow.subscribeToNode(functionInstance);
        return new GroupByFlowBuilder<>(
                new BinaryMapToRefFlowFunction<>(
                        eventStream,
                        deleteTestFlow.defaultValue(functionInstance).eventStream,
                        new GroupByDeleteByNameFlowFunctionWrapper(deletePredicateFunction, functionInstance)::deleteByKey))
                .defaultValue(new GroupBy.EmptyGroupBy<>());
    }

    public GroupByFlowBuilder<K, V> filterValues(SerializableFunction<V, Boolean> mappingFunction) {
        return new GroupByFlowBuilder<>(new MapRef2RefFlowFunction<>(eventStream,
                new GroupByFilterFlowFunctionWrapper(mappingFunction)::filterValues));
    }

    public <K2 extends K, V2> GroupByFlowBuilder<K, Tuple<V, V2>> innerJoin(GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return mapBiFunction(new InnerJoin()::join, rightGroupBy);
    }

    public <K2 extends K, V2, R> GroupByFlowBuilder<K, R> innerJoin(
            GroupByFlowBuilder<K2, V2> rightGroupBy,
            SerializableBiFunction<V, V2, R> mergeFunction) {
        return mapBiFunction(new InnerJoin()::join, rightGroupBy).mapValues(Tuples.mapTuple(mergeFunction));
    }

    public <K2 extends K, V2> GroupByFlowBuilder<K, Tuple<V, V2>> outerJoin(GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return mapBiFunction(new OuterJoin()::join, rightGroupBy);
    }

    public <K2 extends K, V2, R> GroupByFlowBuilder<K, R> outerJoin(
            GroupByFlowBuilder<K2, V2> rightGroupBy,
            SerializableBiFunction<V, V2, R> mergeFunction) {
        return mapBiFunction(new OuterJoin()::join, rightGroupBy).mapValues(Tuples.mapTuple(mergeFunction));
    }

    public <K2 extends K, V2> GroupByFlowBuilder<K, Tuple<V, V2>> leftJoin(GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return mapBiFunction(new LeftJoin()::join, rightGroupBy);
    }

    public <K2 extends K, V2, R> GroupByFlowBuilder<K, R> leftJoin(
            GroupByFlowBuilder<K2, V2> rightGroupBy,
            SerializableBiFunction<V, V2, R> mergeFunction) {
        return mapBiFunction(new LeftJoin()::join, rightGroupBy).mapValues(Tuples.mapTuple(mergeFunction));
    }

    public <K2 extends K, V2> GroupByFlowBuilder<K, Tuple<V, V2>> rightJoin(GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return mapBiFunction(new RightJoin()::join, rightGroupBy);
    }

    public <K2 extends K, V2, R> GroupByFlowBuilder<K, R> rightJoin(
            GroupByFlowBuilder<K2, V2> rightGroupBy,
            SerializableBiFunction<V, V2, R> mergeFunction) {
        return mapBiFunction(new RightJoin()::join, rightGroupBy).mapValues(Tuples.mapTuple(mergeFunction));
    }

    public <K2 extends K, V2, KOUT, VOUT>
    GroupByFlowBuilder<KOUT, VOUT> mapBiFunction(
            SerializableBiFunction<GroupBy<K, V>, GroupBy<K2, V2>, GroupBy<KOUT, VOUT>> int2IntFunction,
            GroupByFlowBuilder<K2, V2> stream2Builder) {
        return new GroupByFlowBuilder<>(
                new BinaryMapToRefFlowFunction<>(eventStream, stream2Builder.eventStream, int2IntFunction)
                        .defaultValue(new GroupBy.EmptyGroupBy<>())
        );
    }

    public <V2, KOUT, VOUT>
    GroupByFlowBuilder<KOUT, VOUT> mapBiFlowFunction(
            SerializableBiFunction<GroupBy<K, V>, V2, GroupBy<KOUT, VOUT>> int2IntFunction,
            FlowBuilder<V2> stream2Builder) {
        return new GroupByFlowBuilder<>(
                new BinaryMapToRefFlowFunction<>(eventStream, stream2Builder.eventStream, int2IntFunction)
                        .defaultValue(new GroupBy.EmptyGroupBy<>())
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
