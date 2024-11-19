package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;
import com.fluxtion.runtime.dataflow.function.MergeMapFlowFunction;
import com.fluxtion.runtime.dataflow.function.NodePropertyToFlowFunction;
import com.fluxtion.runtime.dataflow.function.NodeToFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.GroupByKey;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.event.NamedFeedEvent;
import com.fluxtion.runtime.event.Signal;
import com.fluxtion.runtime.node.DefaultEventHandlerNode;
import com.fluxtion.runtime.node.NamedFeedEventHandlerNode;
import com.fluxtion.runtime.node.NamedFeedTopicFilteredEventHandlerNode;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
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
     * @param <T>      The type of {@link NamedFeedEvent#getData()}
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T> FlowBuilder<T> subscribeToFeed(String feedName, Class<T> dataType) {
        NamedFeedEventHandlerNode<T> feedEventHandlerNode = new NamedFeedEventHandlerNode<>(feedName);
        return new FlowBuilder<>(EventProcessorBuilderService.service().addOrReuse(feedEventHandlerNode))
                .map(NamedFeedEvent::getData);
    }

    /**
     * Subscribes to events of type {@link NamedFeedEvent<T>} published by an {@link com.fluxtion.runtime.input.EventFeed} service
     * registered with feedName and converts events into instances of  {@literal <R>}
     *
     * @param feedName the service name of the registered {@link com.fluxtion.runtime.input.EventFeed}
     * @param <T>      The type of {@link NamedFeedEvent#getData()}
     * @param <R>      The output type of the map function
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T, R> FlowBuilder<R> subscribeToFeed(String feedName, SerializableFunction<T, R> mapFunction) {
        return new FlowBuilder<>(EventProcessorBuilderService.service().addOrReuse(new NamedFeedEventHandlerNode<T>(feedName)))
                .map(NamedFeedEvent::getData)
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
     * @param <T>      The type of {@link NamedFeedEvent#getData()}
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T> FlowBuilder<T> subscribeToFeed(String feedName, String topic, Class<T> dataType) {
        NamedFeedTopicFilteredEventHandlerNode<T> feedEventHandlerNode = new NamedFeedTopicFilteredEventHandlerNode<>(feedName, topic);
        return new FlowBuilder<>(EventProcessorBuilderService.service().addOrReuse(feedEventHandlerNode))
                .map(NamedFeedEvent::getData);
    }

    /**
     * Subscribes to events of type {@link NamedFeedEvent<T>} published by an {@link com.fluxtion.runtime.input.EventFeed} service
     * registered with feedName and converts events into instances of  {@literal <R>}. Applies a filter of topic to the
     * incoming {@link NamedFeedEvent}
     *
     * @param feedName the service name of the registered {@link com.fluxtion.runtime.input.EventFeed}
     * @param <T>      The type of {@link NamedFeedEvent#getData()}
     * @param <R>      The output type of the map function
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T, R> FlowBuilder<R> subscribeToFeed(String feedName, String topic, SerializableFunction<T, R> mapFunction) {
        return new FlowBuilder<>(EventProcessorBuilderService.service().addOrReuse(new NamedFeedTopicFilteredEventHandlerNode<T>(feedName, topic)))
                .map(NamedFeedEvent::getData)
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
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    static <T> FlowBuilder<T> mergeMap(MergeAndMapFlowBuilder<T> builder) {
        MergeMapFlowFunction<T> build = builder.build();
        return new FlowBuilder<>(build);
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
}
