/*
 * Copyright (C) 2019 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.runtime.event;

import com.fluxtion.runtime.node.EventHandlerNode;
import com.fluxtion.runtime.annotations.OnEventHandler;

/**
 * <p>
 * Event class that feeds into a Simple Event Processor(SEP) providing additional
 * meta-data about the incoming event. The additional data provided is:</p>
 *
 * <ul>
 * <li>Filtering</li>
 * <li>Event creation time</li>
 * </ul>
 *
 * <h2>Dispatch</h2>
 * A user creates an Event and publishes it to a SEP for handling via the
 * {@link com.fluxtion.runtime.StaticEventProcessor#onEvent(Object)} method.<p>
 * <p>
 * To dispatch the events within the SEP Fluxtion uses the class name to perform
 * a dispatch. When class name is used, uniqueness is guaranteed by using the
 * fully qualified class name.
 * </p>
 *
 * <h2>Filtering</h2>
 * <p>
 * An event can provide a filter field as either an int or a String, this allow
 * {@link EventHandlerNode}'s or annotated event handler methods to filter
 * the type of events they receive. The SEP will compare the filter values in
 * the {@link Event} and the handler and propagate the Event conditional upon a
 * valid match.
 *
 * @author V12 Technology Ltd.
 * @see OnEventHandler
 * @see EventHandlerNode
 */
public interface Event {
    int NO_INT_FILTER = Integer.MAX_VALUE;
    String NO_STRING_FILTER = "";

    /**
     * The integer id of a filter for this event, can be used interchangeably
     * with filterString. The event handler decides whether it will filter using
     * Strings or integer's, calling this method if filtering is integer based.
     * Integer filtering will generally be more efficient than string filtering,
     * but this depends upon the underlying target platform processing
     * characteristics.
     *
     * @return optional event filter id as integer
     */
    default int filterId() {
        return NO_INT_FILTER;
    }

    /**
     * The String id of a filter for this event, can be used interchangeably
     * with filterId. The event handler decides whether it will filter using
     * Strings or integer's, calling this method if String filtering is string
     * based. Integer filtering will generally be more efficient than string
     * filtering, but this depends upon the underlying target platform
     * processing characteristics.
     *
     * @return optional event filter id as String
     */
    default String filterString() {
        return NO_STRING_FILTER;
    }

    /**
     * The time the event was created. By default this is implemented with {@link System#currentTimeMillis()
     * } during construction.
     *
     * @return creation time, if less than 0 no time of creation is recorded
     */
    default long getEventTime() {
        return -1;
    }
}
