package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.stream.MapEventStream;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.groupby.GroupBy;
import com.fluxtion.runtime.stream.groupby.GroupByStreamed;

public class AbstractGroupByBuilder<K, V, T extends GroupBy<K, V>> extends EventStreamBuilder<T> {

    AbstractGroupByBuilder(TriggeredEventStream<T> eventStream) {
        super(eventStream);
    }

    <I, G extends GroupByStreamed<K, V>>
    AbstractGroupByBuilder(MapEventStream<I, T, TriggeredEventStream<I>> eventStream) {
        super(eventStream);
    }


}
