/*
 * Copyright (c) 2019-2025 gregory higgins.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */

package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;
import com.fluxtion.runtime.dataflow.function.*;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapRef2RefFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import com.fluxtion.runtime.dataflow.groupby.GroupByHashMap;
import com.fluxtion.runtime.dataflow.groupby.GroupByKey;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.event.NamedFeedEvent;
import com.fluxtion.runtime.event.Signal;
import com.fluxtion.runtime.node.DefaultEventHandlerNode;
import com.fluxtion.runtime.node.NamedFeedEventHandlerNode;
import com.fluxtion.runtime.node.NamedFeedTopicFilteredEventHandlerNode;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.*;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper methods for subscribing and creating an {@link FlowBuilder} from external events or internal nodes
 * in the graph.
 */
public interface DataFlow {

    /**
     * Subscribes to events of type {@literal <T>}. Creates a handler method in the generated {@link com.fluxtion.runtime.StaticEventProcessor}
     * so that if {@link com.fluxtion.runtime.StaticEventProcessor#onEvent(Object)} is called an invocation is routed
     * to this {@link FlowFunction}
     *
     * @param classSubscription A class literal describing the subscription
     * @param <T>               The actual type dispatched to this {@link FlowFunction} by the generated {@link com.fluxtion.runtime.StaticEventProcessor}
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T> FlowBuilder<T> subscribe(Class<T> classSubscription) {
        return new FlowBuilder<>(
                EventProcessorBuilderService.service().addOrReuse(new DefaultEventHandlerNode<>(classSubscription))
        );
    }

    /**
     * Subscribes to events of type {@literal <T>} apply an instance function publishing the return value. Creates a
     * handler method in the generated {@link com.fluxtion.runtime.StaticEventProcessor}
     * so that if {@link com.fluxtion.runtime.StaticEventProcessor#onEvent(Object)} is called an invocation is routed
     * to this {@link FlowFunction}
     * <p>
     * In effect this is a compound subscribe().map() combinations
     *
     * @param function A class literal describing the subscription
     * @param <T>      The type subscribed to by the generated {@link com.fluxtion.runtime.StaticEventProcessor}
     * @param <R>      The output type of the map function applied to input instances
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T, R> FlowBuilder<R> subscribe(SerializableFunction<T, R> function) {
        Class<T> classSubscription = (Class<T>) function.method().getDeclaringClass();
        return subscribe(classSubscription).map(function);
    }

    /**
     * Subscribes to {@link NamedFeedEvent}'s published by an {@link com.fluxtion.runtime.input.EventFeed} service
     * registered with feedName.
     *
     * @param feedName the service name of the registered {@link com.fluxtion.runtime.input.EventFeed}
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static FlowBuilder<NamedFeedEvent<ByteBuffer>> subscribeToFeed(String feedName) {
        NamedFeedEventHandlerNode<ByteBuffer> feedEventHandlerNode = new NamedFeedEventHandlerNode<>(feedName);
        return new FlowBuilder<>(EventProcessorBuilderService.service().addOrReuse(feedEventHandlerNode));
    }

    /**
     * Subscribes to events of type {@link NamedFeedEvent<T>} published by an {@link com.fluxtion.runtime.input.EventFeed} service
     * registered with feedName.
     *
     * @param feedName the service name of the registered {@link com.fluxtion.runtime.input.EventFeed}
     * @param <T>      The type of {@link NamedFeedEvent#data()}
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T> FlowBuilder<T> subscribeToFeed(String feedName, Class<T> dataType) {
        NamedFeedEventHandlerNode<T> feedEventHandlerNode = new NamedFeedEventHandlerNode<>(feedName);
        return new FlowBuilder<>(EventProcessorBuilderService.service().addOrReuse(feedEventHandlerNode))
                .map(NamedFeedEvent::data);
    }

    /**
     * Subscribes to events of type {@link NamedFeedEvent<T>} published by an {@link com.fluxtion.runtime.input.EventFeed} service
     * registered with feedName and converts events into instances of  {@literal <R>}
     *
     * @param feedName the service name of the registered {@link com.fluxtion.runtime.input.EventFeed}
     * @param <T>      The type of {@link NamedFeedEvent#data()}
     * @param <R>      The output type of the map function
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T, R> FlowBuilder<R> subscribeToFeed(String feedName, SerializableFunction<T, R> mapFunction) {
        return new FlowBuilder<>(EventProcessorBuilderService.service().addOrReuse(new NamedFeedEventHandlerNode<T>(feedName)))
                .map(NamedFeedEvent::data)
                .map(mapFunction);
    }

    /**
     * Subscribes to {@link NamedFeedEvent}'s published by an {@link com.fluxtion.runtime.input.EventFeed} service
     * registered with feedName and filters by topic on the NamedFeedEvent.
     *
     * @param feedName the service name of the registered {@link com.fluxtion.runtime.input.EventFeed}
     * @param topic    the filtering topic to apply to the input {@link NamedFeedEvent}
     * @return A {@link FlowBuilder} that can used to construct stream processing logic
     */
    static FlowBuilder<NamedFeedEvent<ByteBuffer>> subscribeToFeed(String feedName, String topic) {
        NamedFeedTopicFilteredEventHandlerNode<ByteBuffer> feedEventHandlerNode = new NamedFeedTopicFilteredEventHandlerNode<>(feedName, topic);
        return new FlowBuilder<>(EventProcessorBuilderService.service().addOrReuse(feedEventHandlerNode));
    }

    /**
     * Subscribes to events of type{@link NamedFeedEvent<T>} published by an {@link com.fluxtion.runtime.input.EventFeed} service
     * registered with feedName and filters by topic on the NamedFeedEvent.
     *
     * @param feedName the service name of the registered {@link com.fluxtion.runtime.input.EventFeed}
     * @param topic    the filtering topic to apply to the input {@link NamedFeedEvent}
     * @param <T>      The type of {@link NamedFeedEvent#data()}
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T> FlowBuilder<T> subscribeToFeed(String feedName, String topic, Class<T> dataType) {
        NamedFeedTopicFilteredEventHandlerNode<T> feedEventHandlerNode = new NamedFeedTopicFilteredEventHandlerNode<>(feedName, topic);
        return new FlowBuilder<>(EventProcessorBuilderService.service().addOrReuse(feedEventHandlerNode))
                .map(NamedFeedEvent::data);
    }

    /**
     * Subscribes to events of type {@link NamedFeedEvent<T>} published by an {@link com.fluxtion.runtime.input.EventFeed} service
     * registered with feedName and converts events into instances of  {@literal <R>}. Applies a filter of topic to the
     * incoming {@link NamedFeedEvent}
     *
     * @param feedName the service name of the registered {@link com.fluxtion.runtime.input.EventFeed}
     * @param <T>      The type of {@link NamedFeedEvent#data()}
     * @param <R>      The output type of the map function
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T, R> FlowBuilder<R> subscribeToFeed(String feedName, String topic, SerializableFunction<T, R> mapFunction) {
        return new FlowBuilder<>(EventProcessorBuilderService.service().addOrReuse(new NamedFeedTopicFilteredEventHandlerNode<T>(feedName, topic)))
                .map(NamedFeedEvent::data)
                .map(mapFunction);
    }

    /**
     * Subscribes to events of type {@literal <T>} filtering by {@link Event#filterString()}. Creates a handler method in the generated {@link com.fluxtion.runtime.StaticEventProcessor}
     * so that if {@link com.fluxtion.runtime.StaticEventProcessor#onEvent(Object)} is called an invocation is routed
     * to this {@link FlowFunction}
     *
     * @param classSubscription A class literal describing the subscription
     * @param <T>               The actual type dispatched to this {@link FlowFunction} by the generated {@link com.fluxtion.runtime.StaticEventProcessor}
     * @param filter            The filter string to apply
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T extends Event> FlowBuilder<T> subscribe(Class<T> classSubscription, String filter) {
        return new FlowBuilder<>(
                EventProcessorBuilderService.service().addOrReuse(new DefaultEventHandlerNode<>(filter, classSubscription))
        );
    }

    /**
     * Subscribes to events of type {@literal <T>} filtering by {@link Event#filterId()}. Creates a handler method in the generated {@link com.fluxtion.runtime.StaticEventProcessor}
     * so that if {@link com.fluxtion.runtime.StaticEventProcessor#onEvent(Object)} is called an invocation is routed
     * to this {@link FlowFunction}
     *
     * @param classSubscription A class literal describing the subscription
     * @param <T>               The actual type dispatched to this {@link FlowFunction} by the generated {@link com.fluxtion.runtime.StaticEventProcessor}
     * @param filter            The filter int to apply
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T extends Event> FlowBuilder<T> subscribe(Class<T> classSubscription, int filter) {
        return new FlowBuilder<>(
                EventProcessorBuilderService.service().addOrReuse(new DefaultEventHandlerNode<>(filter, classSubscription))
        );
    }

    /**
     * Subscribes to an internal node within the processing graph and presents it as an {@link FlowBuilder}
     * for constructing stream processing logic.
     *
     * @param source The node to be wrapped and made head of this stream
     * @param <T>    The type of the node
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T> FlowBuilder<T> subscribeToNode(T source) {
        return new FlowBuilder<>(new NodeToFlowFunction<>(source));
    }

    /**
     * Subscribes to a property on an internal node within the processing graph and presents it as an {@link FlowBuilder}
     * for constructing stream processing logic. The node will be created and added to the graph
     *
     * @param sourceProperty The property accessor
     * @param <T>            The type of the node
     * @param <R>            The type of the property that will be supplied in the stream
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T, R> FlowBuilder<R> subscribeToNodeProperty(SerializableFunction<T, R> sourceProperty) {
        T source;
        if (sourceProperty.captured().length == 0) {
            try {
                source = (T) sourceProperty.getContainingClass().getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException("no default constructor found for class:"
                                           + sourceProperty.getContainingClass()
                                           + " either add default constructor or pass in a node instance");
            }
        } else {
            source = (T) sourceProperty.captured()[0];
        }
        return subscribeToNode(source).map(sourceProperty);
    }

    /**
     * Subscribes to a property on an internal node within the processing graph and presents it as an {@link FlowBuilder}
     * for constructing stream processing logic.
     *
     * @param propertySupplier The property accessor
     * @param <R>              The type of the property that will be supplied in the stream
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <R> FlowBuilder<R> subscribeToNodeProperty(SerializableSupplier<R> propertySupplier) {
        EventProcessorBuilderService.service().addOrReuse(propertySupplier.captured()[0]);
        return new FlowBuilder<>(new NodePropertyToFlowFunction<>(propertySupplier));
    }

    /**
     * See {@link DataFlow#subscribe(Class)} shortcut method to create a stream that subscribes to a filtered
     * {@link Signal} event. Useful for invoking triggers on flows.
     *
     * @param filterId The filter string to apply
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static FlowBuilder<Signal> subscribeToSignal(String filterId) {
        return subscribe(Signal.class, filterId);
    }

    /**
     * See {@link DataFlow#subscribe(Class)} shortcut method to create a stream that subscribes to a filtered
     * {@link Signal} event, containing an event of the type specified. Useful for invoking triggers on flows.
     *
     * @param filterId   The filter string to apply
     * @param signalType The type of value that is held by the {@link Signal} event
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T> FlowBuilder<T> subscribeToSignal(String filterId, Class<T> signalType) {
        return subscribe(Signal.class, filterId).map(Signal<T>::getValue);
    }

    /**
     * See {@link DataFlow#subscribe(Class)} shortcut method to create a stream that subscribes to a filtered
     * {@link Signal} event, containing an event of the type specified. A default value is provided if the signal
     * event contains a null value. Useful for invoking triggers on flows.
     *
     * @param filterId     The filter string to apply
     * @param signalType   The type of value that is held by the {@link Signal} event
     * @param defaultValue the value to use if the signal event value is null
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T> FlowBuilder<T> subscribeToSignal(String filterId, Class<T> signalType, T defaultValue) {
        return subscribe(Signal.class, filterId).map(Signal<T>::getValue).defaultValue(defaultValue);
    }

    /**
     * See {@link DataFlow#subscribe(Class)} shortcut method to create a int stream that subscribes to a filtered
     * {@link Signal} event. Useful for invoking triggers on flows.
     *
     * @param filterId The filter string to apply
     * @return An {@link IntFlowBuilder} that can used to construct stream processing logic
     */
    static IntFlowBuilder subscribeToIntSignal(String filterId) {
        return subscribe(Signal.IntSignal.class, filterId).mapToInt(Signal.IntSignal::getValue);
    }

    /**
     * See {@link DataFlow#subscribe(Class)} shortcut method to create a int stream that subscribes to a filtered
     * {@link Signal} event. Useful for invoking triggers on flows. A default value is provided if the signal
     * event contains a 0 value
     *
     * @param filterId     The filter string to apply
     * @param defaultValue to use if the signal event value is 0
     * @return An {@link IntFlowBuilder} that can used to construct stream processing logic
     */
    static IntFlowBuilder subscribeToIntSignal(String filterId, int defaultValue) {
        return subscribe(Signal.IntSignal.class, filterId).mapToInt(Signal.IntSignal::getValue)
                .defaultValue(defaultValue);
    }

    /**
     * See {@link DataFlow#subscribe(Class)} shortcut method to create a double stream that subscribes to a filtered
     * {@link Signal} event. Useful for invoking triggers on flows.
     *
     * @param filterId The filter string to apply
     * @return An {@link DoubleFlowBuilder} that can used to construct stream processing logic
     */
    static DoubleFlowBuilder subscribeToDoubleSignal(String filterId) {
        return subscribe(Signal.DoubleSignal.class, filterId).mapToDouble(Signal.DoubleSignal::getValue);
    }

    /**
     * See {@link DataFlow#subscribe(Class)} shortcut method to create a double stream that subscribes to a filtered
     * {@link Signal} event. Useful for invoking triggers on flows. A default value is provided if the signal
     * event contains a 0 value
     *
     * @param filterId     The filter string to apply
     * @param defaultValue to use if the signal event value is 0
     * @return An {@link DoubleFlowBuilder} that can used to construct stream processing logic
     */
    static DoubleFlowBuilder subscribeToDoubleSignal(String filterId, double defaultValue) {
        return subscribe(Signal.DoubleSignal.class, filterId).mapToDouble(Signal.DoubleSignal::getValue)
                .defaultValue(defaultValue);
    }

    /**
     * See {@link DataFlow#subscribe(Class)} shortcut method to create a long stream that subscribes to a filtered
     * {@link Signal} event. Useful for invoking triggers on flows.
     *
     * @param filterId The filter string to apply
     * @return An {@link LongFlowBuilder} that can used to construct stream processing logic
     */
    static LongFlowBuilder subscribeToLongSignal(String filterId) {
        return subscribe(Signal.LongSignal.class, filterId).mapToLong(Signal.LongSignal::getValue);
    }

    /**
     * See {@link DataFlow#subscribe(Class)} shortcut method to create a long stream that subscribes to a filtered
     * {@link Signal} event. Useful for invoking triggers on flows. A default value is provided if the signal
     * event contains a 0 value
     *
     * @param filterId     The filter string to apply
     * @param defaultValue to use if the signal event value is 0
     * @return An {@link LongFlowBuilder} that can used to construct stream processing logic
     */
    static LongFlowBuilder subscribeToLongSignal(String filterId, long defaultValue) {
        return subscribe(Signal.LongSignal.class, filterId).mapToLong(Signal.LongSignal::getValue)
                .defaultValue(defaultValue);
    }


    /**
     * Merges and maps several  {@link FlowFunction}'s into a single event stream of type T
     *
     * @param builder The builder defining the merge operations
     * @param <T>     The output type of the merged stream
     * @return An {@link FlowBuilder} wrapping merged output that can used to construct stream processing logic
     */
    static <T> FlowBuilder<T> mergeMap(MergeAndMapFlowBuilder<T> builder) {
        return new FlowBuilder<>(builder.build());
    }

    /**
     * Builds a {@link FlowBuilder} that is formed from merging multiple {@link FlowBuilder} inputs of differernt types pushing
     * to a target instance. To create {@link com.fluxtion.compiler.builder.dataflow.MergeAndMapFlowBuilder.MergeInput}
     * legs us the {@link MergeAndMapFlowBuilder#requiredMergeInput(FlowBuilder, LambdaReflection.SerializableBiConsumer)},
     * {@link MergeAndMapFlowBuilder#optionalMergeInput(FlowBuilder, LambdaReflection.SerializableBiConsumer)}
     * utility functions
     *
     * @param target      Supplier of target instances that store the result of the push
     * @param mergeInputs The legs that supply the inputs to the merge consumer operations
     * @param <T>         The target class
     * @return An {@link FlowBuilder} wrapping merged output that can used to construct stream processing logic
     */
    @SafeVarargs
    static <T> FlowBuilder<T> mergeMap(LambdaReflection.SerializableSupplier<T> target, MergeAndMapFlowBuilder.MergeInput<T, ?>... mergeInputs) {
        return MergeAndMapFlowBuilder.merge(target, mergeInputs);
    }

    /**
     * Builds a {@link FlowBuilder} that is formed from merging multiple {@link FlowBuilder} inputs of different types pushing
     * to a target instance. To create {@link com.fluxtion.compiler.builder.dataflow.MergeAndMapFlowBuilder.MergeInput}
     * legs us the {@link MergeAndMapFlowBuilder#requiredMergeInput(FlowBuilder, LambdaReflection.SerializableBiConsumer)},
     * {@link MergeAndMapFlowBuilder#optionalMergeInput(FlowBuilder, LambdaReflection.SerializableBiConsumer)}
     * utility functions
     *
     * @param target      target instance that store the result of the push
     * @param mergeInputs The legs that supply the inputs to the merge consumer operations
     * @param <T>         The target class
     * @return An {@link FlowBuilder} wrapping merged output that can used to construct stream processing logic
     */
    @SafeVarargs
    static <K, T> FlowBuilder<T> mergeMapToNode(T target, MergeAndMapFlowBuilder.MergeInput<T, ?>... mergeInputs) {
        return MergeAndMapFlowBuilder.mergeToNode(target, mergeInputs);
    }

    /**
     * Merges two {@link FlowBuilder}'s into a single event stream of type T
     *
     * @param streamAToMerge stream A to merge
     * @param streamBToMerge stream B to merge
     * @param <T>            type of stream A
     * @param <S>            type of stream B
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T, S extends T> FlowBuilder<T> merge(FlowBuilder<T> streamAToMerge, FlowBuilder<S> streamBToMerge) {
        return streamAToMerge.merge(streamBToMerge);
    }

    /**
     * Merges multiple {@link FlowBuilder}'s into a single event stream of type T
     *
     * @param streamAToMerge stream A to merge
     * @param streamBToMerge stream B to merge
     * @param streamsToMerge streams to merge
     * @param <T>            type of stream A
     * @param <S>            type of stream B
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    @SuppressWarnings("unchecked")
    static <T, S extends T> FlowBuilder<T> merge(
            FlowBuilder<T> streamAToMerge,
            FlowBuilder<S> streamBToMerge,
            FlowBuilder<? extends T>... streamsToMerge) {
        return streamAToMerge.merge(streamBToMerge, streamsToMerge);
    }

    /**
     * Applies a mapping bi function to a pair of streams, creating a stream that is the output of the function. The
     * mapping function will be invoked whenever either stream triggers a notification
     *
     * @param biFunction The mapping {@link java.util.function.BiFunction}
     * @param streamArg1 Stream providing the first argument to the mapping function
     * @param streamArg2 Stream providing the second argument to the mapping function
     * @param <T>        The type of argument 1 stream
     * @param <S>        The type of argument 2 stream
     * @param <R>        The return type of the mapping function
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T, S, R> FlowBuilder<R> mapBiFunction(SerializableBiFunction<T, S, R> biFunction,
                                                  FlowBuilder<T> streamArg1,
                                                  FlowBuilder<S> streamArg2) {
        return streamArg1.mapBiFunction(biFunction, streamArg2);
    }

    static <T, K> GroupByFlowBuilder<K, T> groupBy(SerializableFunction<T, K> keyFunction) {
        @SuppressWarnings("unchecked")
        Class<T> classSubscription = (Class<T>) keyFunction.method().getDeclaringClass();
        return subscribe(classSubscription).groupBy(keyFunction);
    }

    static <T, K, V> GroupByFlowBuilder<K, V> groupByFromMap(SerializableFunction<T, Map<K, V>> mapSupplier) {
        MapRef2RefFlowFunction<Map<K, V>, GroupBy<K, V>, TriggeredFlowFunction<Map<K, V>>> triggered = new MapRef2RefFlowFunction<>(
                subscribe(mapSupplier).eventStream,
                new GroupByHashMap<K, V>()::fromMap
        );
        triggered.defaultValue(new GroupBy.EmptyGroupBy<>());
        return new GroupByFlowBuilder<>(triggered);
    }

    @SafeVarargs
    static <T> GroupByFlowBuilder<GroupByKey<T>, T> groupByFields(SerializableFunction<T, ?>... keyFunction) {
        @SuppressWarnings("unchecked")
        Class<T> classSubscription = (Class<T>) keyFunction[0].method().getDeclaringClass();
        return subscribe(classSubscription).groupByFields(keyFunction);
    }

    static <T, K, O, F extends AggregateFlowFunction<T, O, F>> GroupByFlowBuilder<K, O> groupBy(
            SerializableFunction<T, K> keyFunction, SerializableSupplier<F> aggregateFunctionSupplier) {
        @SuppressWarnings("unchecked")
        Class<T> classSubscription = (Class<T>) keyFunction.method().getDeclaringClass();
        return subscribe(classSubscription).groupBy(keyFunction, aggregateFunctionSupplier);
    }

    /**
     * Aggregates a set of instances into a multimap style structure. The key is a compound key made up from an accessor
     * of the input data
     *
     * @param keyFunction The accessor that makes up the key
     * @param <T>         The item to aggregate
     * @return The GroupByFlowBuilder that represents the multimap
     */
    static <T, K> GroupByFlowBuilder<K, List<T>> groupByToList(SerializableFunction<T, K> keyFunction) {
        @SuppressWarnings("unchecked")
        Class<T> classSubscription = (Class<T>) keyFunction.method().getDeclaringClass();
        return subscribe(classSubscription).groupByToList(keyFunction);
    }

    static <T> FlowBuilder<Collection<T>> collectionFromSubscribe(Class<T> classSubscription) {
        return subscribe(classSubscription).mapToCollection();
    }

    static <T> FlowBuilder<List<T>> listFromSubscribe(Class<T> classSubscription) {
        return subscribe(classSubscription).mapToList();
    }

    static <T> FlowBuilder<Set<T>> setFromSubscribe(Class<T> classSubscription) {
        return subscribe(classSubscription).mapToSet();
    }

    /**
     * Aggregates a set of instances into a multimap style structure. The key is a compound key made up from the accessors
     * of the input data
     *
     * @param keyFunctions The accessors that make up the compound key
     * @param <T>          The item to aggregate
     * @return The GroupByFlowBuilder that represents the multimap
     */
    @SafeVarargs
    static <T> GroupByFlowBuilder<GroupByKey<T>, List<T>> groupByToList(LambdaReflection.SerializableFunction<T, ?>... keyFunctions) {
        @SuppressWarnings("unchecked")
        Class<T> classSubscription = (Class<T>) keyFunctions[0].method().getDeclaringClass();
        return subscribe(classSubscription).groupByToList(keyFunctions);
    }

    static <T, K> GroupByFlowBuilder<K, Set<T>> groupByToSet(SerializableFunction<T, K> keyFunction) {
        @SuppressWarnings("unchecked")
        Class<T> classSubscription = (Class<T>) keyFunction.method().getDeclaringClass();
        return subscribe(classSubscription).groupByToSet(keyFunction);
    }

    /**
     * Aggregates a set of instances into a multiset style structure. The key is a compound key made up from the accessors
     * of the input data
     *
     * @param keyFunctions The accessors that make up the compound key
     * @param <T>          The item to aggregate
     * @return The GroupByFlowBuilder that represents the multimap
     */
    @SafeVarargs
    static <T> GroupByFlowBuilder<GroupByKey<T>, Set<T>> groupByToSet(LambdaReflection.SerializableFunction<T, ?>... keyFunctions) {
        @SuppressWarnings("unchecked")
        Class<T> classSubscription = (Class<T>) keyFunctions[0].method().getDeclaringClass();
        return subscribe(classSubscription).groupByToSet(keyFunctions);
    }

    static <T, K, V> GroupByFlowBuilder<K, V> groupBy(
            SerializableFunction<T, K> keyFunction,
            SerializableFunction<T, V> valueFunction) {
        @SuppressWarnings("unchecked")
        Class<T> classSubscription = (Class<T>) keyFunction.method().getDeclaringClass();
        return subscribe(classSubscription).groupBy(keyFunction, valueFunction);
    }

    static <T, V, K1, A, F extends AggregateFlowFunction<V, A, F>> GroupByFlowBuilder<K1, A>
    groupBy(SerializableFunction<T, K1> keyFunction,
            SerializableFunction<T, V> valueFunction,
            SerializableSupplier<F> aggregateFunctionSupplier) {
        @SuppressWarnings("unchecked")
        Class<T> classSubscription = (Class<T>) keyFunction.method().getDeclaringClass();
        return subscribe(classSubscription).groupBy(keyFunction, valueFunction, aggregateFunctionSupplier);
    }

    /**
     * Builds a GroupByFlowBuilder that is formed from multiple joins and pushed to a target instance.
     *
     * @param target   Supplier of target instances that store the result of the join
     * @param joinLegs The legs that supply the inputs to the join
     * @param <K>      The key class
     * @param <T>      The join target class
     * @return The GroupByFlow with a new instance of the target allocated to every key
     */
    @SafeVarargs
    static <K, T> GroupByFlowBuilder<K, T> multiJoin(LambdaReflection.SerializableSupplier<T> target, MultiJoinBuilder.MultiJoinLeg<K, T, ?>... joinLegs) {
        return MultiJoinBuilder.multiJoin(target, joinLegs);
    }

    /**
     * Gathers two input {@link FlowSupplier} and pushes them to a single binary consumer method.
     *
     * @param instancePushMethod consumer method
     * @param flowBuilderA       supply for argument 1
     * @param flowBuilderB       supply for argument 2
     * @param <T>                The target push instance type
     * @param <A>                Type of argument 1
     * @param <B>                Type of argument 2
     * @return A {@link FlowBuilder} wrapping an instance of T that can used to construct stream processing logic
     */
    static <T, A, B> FlowBuilder<T> push(
            SerializableBiConsumer<A, B> instancePushMethod,
            FlowDataSupplier<? extends FlowSupplier<A>> flowBuilderA,
            FlowDataSupplier<? extends FlowSupplier<B>> flowBuilderB) {
        BiPushFunction<T, A, B> pushFunction = new BiPushFunction<>(
                instancePushMethod,
                flowBuilderA.flowSupplier(),
                flowBuilderB.flowSupplier());
        return new FlowBuilder<>(pushFunction);
    }

    /**
     * Gathers three input {@link FlowSupplier} and pushes them to a single 3 argument consumer method.
     *
     * @param instancePushMethod consumer method
     * @param flowBuilderA       supply for argument 1
     * @param flowBuilderB       supply for argument 2
     * @param flowBuilderC       supply for argument 3
     * @param <T>                The target push instance type
     * @param <A>                Type of argument 1
     * @param <B>                Type of argument 2
     * @param <C>                Type of argument 3
     * @return A {@link FlowBuilder} wrapping an instance of T that can used to construct stream processing logic
     */
    static <T, A, B, C> FlowBuilder<T> push(
            SerializableTriConsumer<A, B, C> instancePushMethod,
            FlowDataSupplier<? extends FlowSupplier<A>> flowBuilderA,
            FlowDataSupplier<? extends FlowSupplier<B>> flowBuilderB,
            FlowDataSupplier<? extends FlowSupplier<C>> flowBuilderC) {
        TriPushFunction<T, A, B, C> pushFunction = new TriPushFunction<>(
                instancePushMethod,
                flowBuilderA.flowSupplier(),
                flowBuilderB.flowSupplier(),
                flowBuilderC.flowSupplier());
        return new FlowBuilder<>(pushFunction);
    }

    /**
     * Gathers four input {@link FlowSupplier} and pushes them to a single 4 argument consumer method.
     *
     * @param instancePushMethod consumer method
     * @param flowBuilderA       supply for argument 1
     * @param flowBuilderB       supply for argument 2
     * @param flowBuilderC       supply for argument 3
     * @param flowBuilderD       supply for argument 4
     * @param <T>                The target push instance type
     * @param <A>                Type of argument 1
     * @param <B>                Type of argument 2
     * @param <C>                Type of argument 3
     * @param <D>                Type of argument 4
     * @return A {@link FlowBuilder} wrapping an instance of T that can used to construct stream processing logic
     */
    static <T, A, B, C, D> FlowBuilder<T> push(
            SerializableQuadConsumer<A, B, C, D> instancePushMethod,
            FlowDataSupplier<? extends FlowSupplier<A>> flowBuilderA,
            FlowDataSupplier<? extends FlowSupplier<B>> flowBuilderB,
            FlowDataSupplier<? extends FlowSupplier<C>> flowBuilderC,
            FlowDataSupplier<? extends FlowSupplier<D>> flowBuilderD) {
        QuadPushFunction<T, A, B, C, D> pushFunction = new QuadPushFunction<>(
                instancePushMethod,
                flowBuilderA.flowSupplier(),
                flowBuilderB.flowSupplier(),
                flowBuilderC.flowSupplier(),
                flowBuilderD.flowSupplier());
        return new FlowBuilder<>(pushFunction);
    }

    /**
     * Gathers five input {@link FlowSupplier} and pushes them to a single 5 5argument consumer method.
     *
     * @param instancePushMethod consumer method
     * @param flowBuilderA       supply for argument 1
     * @param flowBuilderB       supply for argument 2
     * @param flowBuilderC       supply for argument 3
     * @param flowBuilderD       supply for argument 4
     * @param flowBuilderE       supply for argument 5
     * @param <T>                The target push instance type
     * @param <A>                Type of argument 1
     * @param <B>                Type of argument 2
     * @param <C>                Type of argument 3
     * @param <D>                Type of argument 4
     * @param <E>                Type of argument 5
     * @return A {@link FlowBuilder} wrapping an instance of T that can used to construct stream processing logic
     */
    static <T, A, B, C, D, E> FlowBuilder<T> push(
            SerializableQuinConsumer<A, B, C, D, E> instancePushMethod,
            FlowDataSupplier<? extends FlowSupplier<A>> flowBuilderA,
            FlowDataSupplier<? extends FlowSupplier<B>> flowBuilderB,
            FlowDataSupplier<? extends FlowSupplier<C>> flowBuilderC,
            FlowDataSupplier<? extends FlowSupplier<D>> flowBuilderD,
            FlowDataSupplier<? extends FlowSupplier<E>> flowBuilderE) {
        QuinPushFunction<T, A, B, C, D, E> pushFunction = new QuinPushFunction<>(
                instancePushMethod,
                flowBuilderA.flowSupplier(),
                flowBuilderB.flowSupplier(),
                flowBuilderC.flowSupplier(),
                flowBuilderD.flowSupplier(),
                flowBuilderE.flowSupplier());
        return new FlowBuilder<>(pushFunction);
    }

    /**
     * Gathers six input {@link FlowSupplier} and pushes them to a single 5 5argument consumer method.
     *
     * @param instancePushMethod consumer method
     * @param flowBuilderA       supply for argument 1
     * @param flowBuilderB       supply for argument 2
     * @param flowBuilderC       supply for argument 3
     * @param flowBuilderD       supply for argument 4
     * @param flowBuilderE       supply for argument 5
     * @param flowBuilderF       supply for argument 6
     * @param <T>                The target push instance type
     * @param <A>                Type of argument 1
     * @param <B>                Type of argument 2
     * @param <C>                Type of argument 3
     * @param <D>                Type of argument 4
     * @param <E>                Type of argument 5
     * @param <F>                Type of argument 6
     * @return A {@link FlowBuilder} wrapping an instance of T that can used to construct stream processing logic
     */
    static <T, A, B, C, D, E, F> FlowBuilder<T> push(
            SerializableSextConsumer<A, B, C, D, E, F> instancePushMethod,
            FlowDataSupplier<? extends FlowSupplier<A>> flowBuilderA,
            FlowDataSupplier<? extends FlowSupplier<B>> flowBuilderB,
            FlowDataSupplier<? extends FlowSupplier<C>> flowBuilderC,
            FlowDataSupplier<? extends FlowSupplier<D>> flowBuilderD,
            FlowDataSupplier<? extends FlowSupplier<E>> flowBuilderE,
            FlowDataSupplier<? extends FlowSupplier<F>> flowBuilderF) {
        SextPushFunction<T, A, B, C, D, E, F> pushFunction = new SextPushFunction<>(
                instancePushMethod,
                flowBuilderA.flowSupplier(),
                flowBuilderB.flowSupplier(),
                flowBuilderC.flowSupplier(),
                flowBuilderD.flowSupplier(),
                flowBuilderE.flowSupplier(),
                flowBuilderF.flowSupplier());
        return new FlowBuilder<>(pushFunction);
    }

    /**
     * Gathers two input {@link FlowSupplier} and pushes them to a single binary consumer method.
     * Implicitly creates a target instance from the
     *
     * @param classPushMethod consumer method
     * @param flowBuilderA    supply for argument 1
     * @param flowBuilderB    supply for argument 2
     * @param <T>             The target push instance type
     * @param <A>             Type of argument 1
     * @param <B>             Type of argument 1
     * @return A {@link FlowBuilder} wrapping an instance of T that can used to construct stream processing logic
     */
    static <T, A, B> FlowBuilder<T> push(
            SerializableTriConsumer<T, A, B> classPushMethod,
            FlowDataSupplier<? extends FlowSupplier<A>> flowBuilderA,
            FlowDataSupplier<? extends FlowSupplier<B>> flowBuilderB) {
        BiPushFunction<T, A, B> pushFunction = new BiPushFunction<>(
                classPushMethod,
                flowBuilderA.flowSupplier(),
                flowBuilderB.flowSupplier());
        return new FlowBuilder<>(pushFunction);
    }

    /**
     * Gathers three input {@link FlowSupplier} and pushes them to a single 3 argument consumer method.
     * Implicitly creates a target instance from the
     *
     * @param classPushMethod consumer method
     * @param flowBuilderA    supply for argument 1
     * @param flowBuilderB    supply for argument 2
     * @param flowBuilderC    supply for argument 3
     * @param <T>             The target push instance type
     * @param <A>             Type of argument 1
     * @param <B>             Type of argument 2
     * @param <C>             Type of argument 3
     * @return A {@link FlowBuilder} wrapping an instance of T that can used to construct stream processing logic
     */
    static <T, A, B, C> FlowBuilder<T> push(
            SerializableQuadConsumer<T, A, B, C> classPushMethod,
            FlowDataSupplier<? extends FlowSupplier<A>> flowBuilderA,
            FlowDataSupplier<? extends FlowSupplier<B>> flowBuilderB,
            FlowDataSupplier<? extends FlowSupplier<C>> flowBuilderC) {
        TriPushFunction<T, A, B, C> pushFunction = new TriPushFunction<>(
                classPushMethod,
                flowBuilderA.flowSupplier(),
                flowBuilderB.flowSupplier(),
                flowBuilderC.flowSupplier());
        return new FlowBuilder<>(pushFunction);
    }

    /**
     * Gathers four input {@link FlowSupplier} and pushes them to a single 4 argument consumer method.
     * Implicitly creates a target instance from the
     *
     * @param classPushMethod consumer method
     * @param flowBuilderA    supply for argument 1
     * @param flowBuilderB    supply for argument 2
     * @param flowBuilderC    supply for argument 3
     * @param flowBuilderD    supply for argument 4
     * @param <T>             The target push instance type
     * @param <A>             Type of argument 1
     * @param <B>             Type of argument 2
     * @param <C>             Type of argument 3
     * @param <D>             Type of argument 4
     * @return A {@link FlowBuilder} wrapping an instance of T that can used to construct stream processing logic
     */
    static <T, A, B, C, D> FlowBuilder<T> push(
            SerializableQuinConsumer<T, A, B, C, D> classPushMethod,
            FlowDataSupplier<? extends FlowSupplier<A>> flowBuilderA,
            FlowDataSupplier<? extends FlowSupplier<B>> flowBuilderB,
            FlowDataSupplier<? extends FlowSupplier<C>> flowBuilderC,
            FlowDataSupplier<? extends FlowSupplier<D>> flowBuilderD) {
        QuadPushFunction<T, A, B, C, D> pushFunction = new QuadPushFunction<>(
                classPushMethod,
                flowBuilderA.flowSupplier(),
                flowBuilderB.flowSupplier(),
                flowBuilderC.flowSupplier(),
                flowBuilderD.flowSupplier());
        return new FlowBuilder<>(pushFunction);
    }

    /**
     * Gathers five input {@link FlowSupplier} and pushes them to a single 5 argument consumer method.
     * Implicitly creates a target instance from the
     *
     * @param classPushMethod consumer method
     * @param flowBuilderA    supply for argument 1
     * @param flowBuilderB    supply for argument 2
     * @param flowBuilderC    supply for argument 3
     * @param flowBuilderD    supply for argument 4
     * @param flowBuilderE    supply for argument 5
     * @param <T>             The target push instance type
     * @param <A>             Type of argument 1
     * @param <B>             Type of argument 2
     * @param <C>             Type of argument 3
     * @param <D>             Type of argument 4
     * @param <E>             Type of argument 5
     * @return A {@link FlowBuilder} wrapping an instance of T that can used to construct stream processing logic
     */
    static <T, A, B, C, D, E> FlowBuilder<T> push(
            SerializableSextConsumer<T, A, B, C, D, E> classPushMethod,
            FlowDataSupplier<? extends FlowSupplier<A>> flowBuilderA,
            FlowDataSupplier<? extends FlowSupplier<B>> flowBuilderB,
            FlowDataSupplier<? extends FlowSupplier<C>> flowBuilderC,
            FlowDataSupplier<? extends FlowSupplier<D>> flowBuilderD,
            FlowDataSupplier<? extends FlowSupplier<E>> flowBuilderE) {
        QuinPushFunction<T, A, B, C, D, E> pushFunction = new QuinPushFunction<>(
                classPushMethod,
                flowBuilderA.flowSupplier(),
                flowBuilderB.flowSupplier(),
                flowBuilderC.flowSupplier(),
                flowBuilderD.flowSupplier(),
                flowBuilderE.flowSupplier());
        return new FlowBuilder<>(pushFunction);
    }

    /**
     * Gathers five input {@link FlowSupplier} and pushes them to a single 5 argument consumer method.
     * Implicitly creates a target instance from the
     *
     * @param classPushMethod consumer method
     * @param flowBuilderA    supply for argument 1
     * @param flowBuilderB    supply for argument 2
     * @param flowBuilderC    supply for argument 3
     * @param flowBuilderD    supply for argument 4
     * @param flowBuilderE    supply for argument 5
     * @param flowBuilderF    supply for argument 6
     * @param <T>             The target push instance type
     * @param <A>             Type of argument 1
     * @param <B>             Type of argument 2
     * @param <C>             Type of argument 3
     * @param <D>             Type of argument 4
     * @param <E>             Type of argument 5
     * @param <F>             Type of argument 6
     * @return A {@link FlowBuilder} wrapping an instance of T that can used to construct stream processing logic
     */
    static <T, A, B, C, D, E, F> FlowBuilder<T> push(
            SerializableSeptConsumer<T, A, B, C, D, E, F> classPushMethod,
            FlowDataSupplier<? extends FlowSupplier<A>> flowBuilderA,
            FlowDataSupplier<? extends FlowSupplier<B>> flowBuilderB,
            FlowDataSupplier<? extends FlowSupplier<C>> flowBuilderC,
            FlowDataSupplier<? extends FlowSupplier<D>> flowBuilderD,
            FlowDataSupplier<? extends FlowSupplier<E>> flowBuilderE,
            FlowDataSupplier<? extends FlowSupplier<F>> flowBuilderF) {
        SextPushFunction<T, A, B, C, D, E, F> pushFunction = new SextPushFunction<>(
                classPushMethod,
                flowBuilderA.flowSupplier(),
                flowBuilderB.flowSupplier(),
                flowBuilderC.flowSupplier(),
                flowBuilderD.flowSupplier(),
                flowBuilderE.flowSupplier(),
                flowBuilderF.flowSupplier());
        return new FlowBuilder<>(pushFunction);
    }
}
