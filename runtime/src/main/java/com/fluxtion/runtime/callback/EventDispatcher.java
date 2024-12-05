/*
 * SPDX-FileCopyrightText: © 2024 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.annotations.builder.Inject;

/**
 * Dispatches re-entrant events to the top of graph for a new processing cycle. The graph must be in a processing cycle
 * to queue a new event. Events are queued until the current cycle has  finished.
 * Use the {@link Inject} annotation to add a dependency in a user class.
 */
public interface EventDispatcher {

    String DEFAULT_NODE_NAME = "eventDispatcher";

    /**
     * Inserts an event to the front of any queued events.
     *
     * @param event the event to add to the front of the queue
     */
    void processReentrantEvent(Object event);

    /**
     * Inserts an Iterable set of events to the front of any queued events.
     *
     * @param iterable the set event to add to the front of the queue
     */
    void processReentrantEvents(Iterable<Object> iterable);

    /**
     * Adds an event to end of the queued events
     *
     * @param event the event to add to the end of the queue
     */
    void queueReentrantEvent(Object event);

    /**
     * Processes the event as a new event in the event processor adding it to the end of any queued events. WIll force
     * an event cycle to execute if one is not currently executing
     *
     * @param event the event to add to the end of the queue
     */
    void processAsNewEventCycle(Object event);
}
