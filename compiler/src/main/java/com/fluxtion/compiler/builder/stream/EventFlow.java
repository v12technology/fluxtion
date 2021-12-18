package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtim.SepContext;
import com.fluxtion.runtim.event.DefaultFilteredEventHandler;
import com.fluxtion.runtim.stream.NodeEventStream;

/**
 * Helper methods for subscribing and creating an {@link EventStreamBuilder} from external events or internal nodes
 * in the graph.
 */
public interface EventFlow {

    /**
     * Subscribes to events of type {@literal <T>}. Creates a handler method in the generated {@link com.fluxtion.runtim.StaticEventProcessor}
     * so that if {@link com.fluxtion.runtim.StaticEventProcessor#onEvent(Object)} is called an invocation is routed
     * to this {@link com.fluxtion.runtim.stream.EventStream}
     * @param classSubscription A class literal describing the subscription
     * @param <T> The actual type dispatched to this {@link com.fluxtion.runtim.stream.EventStream} by the generated {@link com.fluxtion.runtim.StaticEventProcessor}
     * @return An {@link EventStreamBuilder} that can used to construct stream processing logic
     */
    static <T> EventStreamBuilder<T> subscribe(Class<T> classSubscription) {
        return new EventStreamBuilder<>(
                SepContext.service().addOrReuse(new DefaultFilteredEventHandler<>(classSubscription))
        );
    }

    /**
     * Subscribes to an internal node within the processing graph and presents it as an {@link EventStreamBuilder}
     * for constructing stream processing logic.
     * @param source The node to be wrapped and made head of this stream
     * @param <T> The type of the node
     * @return An {@link EventStreamBuilder} that can used to construct stream processing logic
     */
    static <T> EventStreamBuilder<T> subscribeToNode(T source){
        return new EventStreamBuilder<>(new NodeEventStream<>(source));
    }
}
