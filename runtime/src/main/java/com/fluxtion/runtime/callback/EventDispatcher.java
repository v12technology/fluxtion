package com.fluxtion.runtime.callback;

/**
 * Dispatches events to the top of graph for a new processing cycle. Events are queued until the current cycle has
 * finished. Use the {@link com.fluxtion.runtime.annotations.builder.Inject} annotation to add a dependency in a user
 * class.
 *
 */
public interface EventDispatcher {

    void processEvent(Object event);

    void processEvents(Iterable<Object> iterable);
}
