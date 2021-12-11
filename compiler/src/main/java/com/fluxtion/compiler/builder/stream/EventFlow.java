package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtim.event.DefaultFilteredEventHandler;
import com.fluxtion.runtim.stream.NodeEventStream;

public interface EventFlow {

    static <T> EventStreamBuilder<T> subscribe(Class<T> classSubscription) {
        return new EventStreamBuilder<>(new DefaultFilteredEventHandler<>(classSubscription));
    }

    static <T> EventStreamBuilder<T> streamFromNode(T source){
        return new EventStreamBuilder<>(new NodeEventStream<>(source));
    }
}
