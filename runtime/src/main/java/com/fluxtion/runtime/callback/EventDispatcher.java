package com.fluxtion.runtime.callback;

/**
 * Dispatches re-entrant events to the top of graph for a new processing cycle. The graph must be in a processing cycle
 * to queue a new event. Events are queued until the current cycle has  finished.
 * Use the {@link com.fluxtion.runtime.annotations.builder.Inject} annotation to add a dependency in a user class.
 */
public interface EventDispatcher {

    void processReentrantEvent(Object event);

    void processReentrantEvents(Iterable<Object> iterable);

    void processAsNewEventCycle(Object event);
}
