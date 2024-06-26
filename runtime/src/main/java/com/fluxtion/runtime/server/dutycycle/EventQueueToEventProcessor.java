package com.fluxtion.runtime.server.dutycycle;

import com.fluxtion.runtime.StaticEventProcessor;
import org.agrona.concurrent.Agent;

/**
 * Reads from an event queue and invokes callbacks on registered {@link StaticEventProcessor}'s. Acts as a multiplexer
 * for an event queue to registered StaticEventProcessor
 */
public interface EventQueueToEventProcessor extends Agent {

    int registerProcessor(StaticEventProcessor eventProcessor);

    int deregisterProcessor(StaticEventProcessor eventProcessor);

    int listenerCount();
}
