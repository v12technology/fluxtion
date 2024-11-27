package com.fluxtion.runtime.output;

import java.util.function.Consumer;

/**
 * A sink for an EventProcessor. Implement this interface and register with :
 *
 * {@link com.fluxtion.runtime.EventProcessor#registerService(Object, Class, String)}
 * @param <T> the type of message published to the Sink
 */
public interface MessageSink<T> extends Consumer<T> {
}
