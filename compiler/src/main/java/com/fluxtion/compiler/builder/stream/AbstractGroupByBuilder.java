package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.stream.GroupByStreamed;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.impl.MapEventStream;

public class AbstractGroupByBuilder<K, V, T extends GroupByStreamed<K, V>> extends EventStreamBuilder<T> {

    AbstractGroupByBuilder(TriggeredEventStream<T> eventStream) {
        super(eventStream);
    }

    <I, G extends GroupByStreamed<K, V>>
    AbstractGroupByBuilder(MapEventStream<I, T, TriggeredEventStream<I>> eventStream) {
        super(eventStream);
    }


}
