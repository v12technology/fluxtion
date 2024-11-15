/*
 * Copyright (C) 2018 2024 gregory higgins.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.runtime.node;

import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.event.Event;

/**
 * Acts as the root of an execution path in a {@link StaticEventProcessor}. A
 * user implements this class and registers it with the static event compiler,
 * to generate a StaticEventProcessor. Events will be routed to an instance of
 * this class by the generated StaticEventProcessor at runtime.
 *
 * <pre>
 * <h2>Filtering</h2>
 * An EventHandler can optionally provide a filter value to filter the
 * events that are accepted for processing. Usually the match is based solely
 * on event type to determine if instance of a FilteredEventHandler is on the
 * execution path for an event, filtering can further refine the match.
 * <p>
 *
 * An {@link Event} can optionally specify a filter value as an int {@link Event#filterId()
 * } or as a String {@link Event#filterString() . The SEP will compare the filter
 * values in the {@link Event} and the handler and propagate the Event conditional upon the a match.
 * .<p>
 *
 * Default values for filters indicate only match on type, no filters are applied:
 * <ul>
 * <li>int filter : Integer.MAX_VALUE = no filtering</li>
 * <li>String filter : null or "" = no filtering</li>
 * </ul>
 * </pre>
 * <p>
 * Child instances that refer to this instance receive update callbacks by invoking marking a method with an {@link com.fluxtion.runtime.annotations.OnTrigger}
 * annotation or implementing the {@link TriggeredNode} interface
 *
 * @param <T> The type of event processed by this handler
 * @author Greg Higgins
 */
public interface EventHandlerNode<T> {

    default int filterId() {
        return Event.NO_INT_FILTER;
    }

    default String filterString() {
        return Event.NO_STRING_FILTER;
    }

    /**
     * Called when a new event e is ready to be processed. Return flag indicates if a notification should be broadcast
     * and the onEvent methods of dependencies should be invoked
     *
     * @param e the {@link com.fluxtion.runtime.event.Event Event} to process.
     * @return event propagtion flag
     */
    <E extends T> boolean onEvent(E e);

    /**
     * called when all nodes that depend upon this EventHadler have successfully
     * completed their processing.
     */
    default void afterEvent() {
    }

    /**
     * The class of the Event processed by this handler, overrides the generic type parameter class. If null then the class
     * of the generic type T is used to determine the event type to be processed.
     *
     * @return Class of {@link com.fluxtion.runtime.event.Event Event} to process
     */
    default Class<? extends T> eventClass() {
        return null;
    }
}
