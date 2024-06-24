package com.fluxtion.runtime.server.subscription;

import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.feature.Experimental;

@Experimental
public class EventToOnEventInvokeStrategy extends AbstractEventToInvocationStrategy {
    @Override
    protected void dispatchEvent(Object event, StaticEventProcessor eventProcessor) {
        eventProcessor.onEvent(event);
    }

    @Override
    protected boolean isValidTarget(StaticEventProcessor eventProcessor) {
        return true;
    }
}
