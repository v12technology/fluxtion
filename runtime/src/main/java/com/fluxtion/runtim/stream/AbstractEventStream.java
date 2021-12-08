package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.TriggerEventOverride;
import lombok.Data;

/**
 *
 * @param <R> Type of input stream
 * @param <T> Output type of stream
 */
@Data
public abstract class AbstractEventStream<R, T> implements TriggeredEventStream<T>{

    private final EventStream<R> inputEventStream;

    @TriggerEventOverride
    private transient Object updateTriggerOverride;

    public AbstractEventStream(EventStream<R> inputEventStream) {
        this.inputEventStream = inputEventStream;
    }

}
