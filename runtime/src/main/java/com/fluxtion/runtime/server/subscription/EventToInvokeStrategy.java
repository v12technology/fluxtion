package com.fluxtion.runtime.server.subscription;

import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.feature.Experimental;

/**
 * Reads and transforms event flow into application level callbacks on registered {@link StaticEventProcessor}'s
 */
@Experimental
public interface EventToInvokeStrategy {

    void processEvent(Object event);

    void registerProcessor(StaticEventProcessor eventProcessor);

    void deregisterProcessor(StaticEventProcessor eventProcessor);

    int listenerCount();
}
