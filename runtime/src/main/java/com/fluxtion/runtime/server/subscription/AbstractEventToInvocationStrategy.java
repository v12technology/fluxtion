package com.fluxtion.runtime.server.subscription;

import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.feature.Experimental;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Abstract class to simplify create an EventToInvokeStrategy, by implementing two methods:
 *
 * <ul>
 *     <li>isValidTarget - is an eventProcessor a suitable target for callbacks</li>
 *     <li>dispatchEvent - process the event and dispatch to target eventProcessor's</li>
 * </ul>
 */
@Experimental
public abstract class AbstractEventToInvocationStrategy implements EventToInvokeStrategy {

    protected final List<StaticEventProcessor> eventProcessorSinks = new CopyOnWriteArrayList<>();

    @Override
    public void processEvent(Object event) {
        for (int i = 0, targetQueuesSize = eventProcessorSinks.size(); i < targetQueuesSize; i++) {
            StaticEventProcessor eventProcessor = eventProcessorSinks.get(i);
            dispatchEvent(event, eventProcessor);
        }
    }

    /**
     * Map the event to a callback invocation on the supplied eventProcessor
     *
     * @param event          the incoming event to map to a callback method
     * @param eventProcessor the target of the callback method
     */
    abstract protected void dispatchEvent(Object event, StaticEventProcessor eventProcessor);

    @Override
    public void registerProcessor(StaticEventProcessor eventProcessor) {
        if (isValidTarget(eventProcessor) && !eventProcessorSinks.contains(eventProcessor)) {
            eventProcessorSinks.add(eventProcessor);
        }
    }

    /**
     * Return true if the eventProcessor is a valid target for receiving callbacks from this invocation strategy.
     *
     * @param eventProcessor the potential target of this invocation strategy
     * @return is a valid target
     */
    abstract protected boolean isValidTarget(StaticEventProcessor eventProcessor);

    @Override
    public void deregisterProcessor(StaticEventProcessor eventProcessor) {
        eventProcessorSinks.remove(eventProcessor);
    }

    @Override
    public int listenerCount() {
        return eventProcessorSinks.size();
    }
}
