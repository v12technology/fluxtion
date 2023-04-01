package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.function.MergeMapFlowFunction;
import com.fluxtion.runtime.dataflow.function.NodePropertyToFlowFunction;
import com.fluxtion.runtime.dataflow.function.NodeToFlowFunction;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.event.Signal;
import com.fluxtion.runtime.node.DefaultEventHandlerNode;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

import java.lang.reflect.InvocationTargetException;
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

    static <T, K> GroupByFlowBuilder<K, T> groupBy(Class<T> classSubscription, SerializableFunction<T, K> keyFunction) {
        return subscribe(classSubscription).groupBy(keyFunction);
    }

    static <T, K> GroupByFlowBuilder<K, T> groupBy(SerializableFunction<T, K> keyFunction) {
        @SuppressWarnings("unchecked")
        Class<T> classSubscription = (Class<T>) keyFunction.method().getDeclaringClass();
        return subscribe(classSubscription).groupBy(keyFunction);
    }

    static <T, K> GroupByFlowBuilder<K, List<T>> groupByToList(SerializableFunction<T, K> keyFunction) {
        @SuppressWarnings("unchecked")
        Class<T> classSubscription = (Class<T>) keyFunction.method().getDeclaringClass();
        return subscribe(classSubscription).groupByToList(keyFunction);
    }

    static <T, K> GroupByFlowBuilder<K, Set<T>> groupByToSet(SerializableFunction<T, K> keyFunction) {
        @SuppressWarnings("unchecked")
        Class<T> classSubscription = (Class<T>) keyFunction.method().getDeclaringClass();
        return subscribe(classSubscription).groupByToSet(keyFunction);
    }

    static <T, K, V> GroupByFlowBuilder<K, V> groupBy(
            Class<T> classSubscription,
            SerializableFunction<T, K> keyFunction,
            SerializableFunction<T, V> valueFunction) {
        return subscribe(classSubscription).groupBy(keyFunction, valueFunction);
    }

    static <T, K, V> GroupByFlowBuilder<K, V> groupBy(
            SerializableFunction<T, K> keyFunction,
            SerializableFunction<T, V> valueFunction) {
        @SuppressWarnings("unchecked")
        Class<T> classSubscription = (Class<T>) keyFunction.method().getDeclaringClass();
        return subscribe(classSubscription).groupBy(keyFunction, valueFunction);
    }

    //SerializableFunction<T, V> valueFunction

    static FlowBuilder<Object> subscribeToSignal(String filterId) {
        return subscribeToSignal(filterId, Object.class);
    }

    static <T> FlowBuilder<T> subscribeToSignal(String filterId, Class<T> signalType) {
        return subscribe(Signal.class, filterId).map(Signal<T>::getValue);
    }

    static <T> FlowBuilder<T> subscribeToSignal(String filterId, Class<T> signalType, T defaultValue) {
        return subscribe(Signal.class, filterId).map(Signal<T>::getValue).defaultValue(defaultValue);
    }

    static IntFlowBuilder subscribeToIntSignal(String filterId) {
        return subscribe(Signal.IntSignal.class, filterId).mapToInt(Signal.IntSignal::getValue);
    }

    static IntFlowBuilder subscribeToIntSignal(String filterId, int defaultValue) {
        return subscribe(Signal.IntSignal.class, filterId).mapToInt(Signal.IntSignal::getValue)
                .defaultValue(defaultValue);
    }

    static DoubleFlowBuilder subscribeToDoubleSignal(String filterId) {
        return subscribe(Signal.DoubleSignal.class, filterId).mapToDouble(Signal.DoubleSignal::getValue);
    }

    static DoubleFlowBuilder subscribeToDoubleSignal(String filterId, double defaultValue) {
        return subscribe(Signal.DoubleSignal.class, filterId).mapToDouble(Signal.DoubleSignal::getValue)
                .defaultValue(defaultValue);
    }

    static LongFlowBuilder subscribeToLongSignal(String filterId) {
        return subscribe(Signal.LongSignal.class, filterId).mapToLong(Signal.LongSignal::getValue);
    }

    static LongFlowBuilder subscribeToLongSignal(String filterId, long defaultValue) {
        return subscribe(Signal.LongSignal.class, filterId).mapToLong(Signal.LongSignal::getValue)
                .defaultValue(defaultValue);
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

    static <R> FlowBuilder<R> subscribeToNodeProperty(SerializableSupplier<R> propertySupplier) {
        EventProcessorBuilderService.service().addOrReuse(propertySupplier.captured()[0]);
        return new FlowBuilder<>(new NodePropertyToFlowFunction<>(propertySupplier));
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
}
