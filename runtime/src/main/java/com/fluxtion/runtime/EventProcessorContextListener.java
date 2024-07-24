package com.fluxtion.runtime;

/**
 * Listener for the current {@link EventProcessorContext}
 */
public interface EventProcessorContextListener {

    void currentContext(EventProcessorContext currentContext);
}
