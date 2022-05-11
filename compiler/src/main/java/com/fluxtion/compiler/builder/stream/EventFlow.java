package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.SepContext;
import com.fluxtion.runtime.event.DefaultFilteredEventHandler;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.MergeMapEventStream;
import com.fluxtion.runtime.stream.NodeEventStream;
import com.fluxtion.runtime.stream.NodePropertyStream;

import java.lang.reflect.InvocationTargetException;

/**
 * Helper methods for subscribing and creating an {@link EventStreamBuilder} from external events or internal nodes
 * in the graph.
 */
public interface EventFlow {

    /**
     * Subscribes to events of type {@literal <T>}. Creates a handler method in the generated {@link com.fluxtion.runtime.StaticEventProcessor}
     * so that if {@link com.fluxtion.runtime.StaticEventProcessor#onEvent(Object)} is called an invocation is routed
     * to this {@link com.fluxtion.runtime.stream.EventStream}
     *
     * @param classSubscription A class literal describing the subscription
     * @param <T>               The actual type dispatched to this {@link com.fluxtion.runtime.stream.EventStream} by the generated {@link com.fluxtion.runtime.StaticEventProcessor}
     * @return An {@link EventStreamBuilder} that can used to construct stream processing logic
     */
    static <T> EventStreamBuilder<T> subscribe(Class<T> classSubscription) {
        return new EventStreamBuilder<>(
                SepContext.service().addOrReuse(new DefaultFilteredEventHandler<>(classSubscription))
        );
    }

    /**
     * Subscribes to events of type {@literal <T>} filtering by {@link Event#filterString()}. Creates a handler method in the generated {@link com.fluxtion.runtime.StaticEventProcessor}
     * so that if {@link com.fluxtion.runtime.StaticEventProcessor#onEvent(Object)} is called an invocation is routed
     * to this {@link com.fluxtion.runtime.stream.EventStream}
     *
     * @param classSubscription A class literal describing the subscription
     * @param <T>               The actual type dispatched to this {@link com.fluxtion.runtime.stream.EventStream} by the generated {@link com.fluxtion.runtime.StaticEventProcessor}
     * @param filter            The filter string to apply
     * @return An {@link EventStreamBuilder} that can used to construct stream processing logic
     */
    static <T extends Event> EventStreamBuilder<T> subscribe(Class<T> classSubscription, String filter) {
        return new EventStreamBuilder<>(
                SepContext.service().addOrReuse(new DefaultFilteredEventHandler<>(filter, classSubscription))
        );
    }

    /**
     * Subscribes to events of type {@literal <T>} filtering by {@link Event#filterId()}. Creates a handler method in the generated {@link com.fluxtion.runtime.StaticEventProcessor}
     * so that if {@link com.fluxtion.runtime.StaticEventProcessor#onEvent(Object)} is called an invocation is routed
     * to this {@link com.fluxtion.runtime.stream.EventStream}
     *
     * @param classSubscription A class literal describing the subscription
     * @param <T>               The actual type dispatched to this {@link com.fluxtion.runtime.stream.EventStream} by the generated {@link com.fluxtion.runtime.StaticEventProcessor}
     * @param filter            The filter int to apply
     * @return An {@link EventStreamBuilder} that can used to construct stream processing logic
     */
    static <T extends Event> EventStreamBuilder<T> subscribe(Class<T> classSubscription, int filter) {
        return new EventStreamBuilder<>(
                SepContext.service().addOrReuse(new DefaultFilteredEventHandler<>(filter, classSubscription))
        );
    }

    /**
     * Subscribes to an internal node within the processing graph and presents it as an {@link EventStreamBuilder}
     * for constructing stream processing logic.
     *
     * @param source The node to be wrapped and made head of this stream
     * @param <T>    The type of the node
     * @return An {@link EventStreamBuilder} that can used to construct stream processing logic
     */
    static <T> EventStreamBuilder<T> subscribeToNode(T source) {
        return new EventStreamBuilder<>(new NodeEventStream<>(source));
    }

    static <T, R> EventStreamBuilder<R> subscribeToNodeProperty(SerializableFunction<T, R> sourceProperty) {
        T source;
        if(sourceProperty.captured().length == 0){
            try {
                source = (T) sourceProperty.getContainingClass().getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException("no default constructor found for class:"
                        + sourceProperty.getContainingClass()
                        + " either add default constructor or pass in a node instance");
            }
        }else{
            source = (T) sourceProperty.captured()[0];
        }
        return subscribeToNode(source).map(sourceProperty);
    }

    static <R> EventStreamBuilder<R> subscribeToNodeProperty(SerializableSupplier<R> propertySupplier){
        SepContext.service().addOrReuse(propertySupplier.captured()[0]);
        return new EventStreamBuilder<>(new NodePropertyStream<>(propertySupplier));
    }

    /**
     * Merges and maps several  {@link com.fluxtion.runtime.stream.EventStream}'s into a single event stream of type T
     *
     * @param builder The builder defining the merge operations
     * @param <T>     The output type of the merged stream
     * @return An {@link EventStreamBuilder} that can used to construct stream processing logic
     */
    static <T> EventStreamBuilder<T> mergeMap(MergeMapStreamBuilder<T> builder) {
        MergeMapEventStream<T> build = builder.build();
        return new EventStreamBuilder<>(build);
    }
}
