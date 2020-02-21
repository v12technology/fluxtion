/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.api.event;

import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.FilteredEventHandler;

/**
 * <p>
 * Event class that feeds into a Simple Event Processor(SEP). Users should
 * extend this class to define their own events.</p>
 *
 * <h2>Dispatch</h2>
 * A user creates an Event and publishes it to a SEP for handling via the {@link EventHandler#onEvent(com.fluxtion.api.event.Event)
 * } method.<p>
 *
 * To dispatch the events within the SEP Fluxtion uses either a statically
 * defined ID, where the value of ID must be unique for the events in this SEP.
 * <pre>
 *     public static final int ID = 1;
 * </pre>
 * <p>
 * If no ID is defined then the SEP uses the class name to perform a dispatch,
 * generally this will be less efficient at runtime but is easier for the
 * developer at compile time. When class name is used, uniqueness is guaranteed
 * by using the fully qualified class name.
 * </p>
 *
 * <p>
 * The efficiency of dispatch depends upon the target platform, so for some
 * targets class name dispatch may be more efficient.
 * </p>
 *
 * <h2>Filtering</h2>
 *
 * An event can provide a filter field as either an int or a String, this allow
 * {@link FilteredEventHandler}'s or annotated event handler methods to filter
 * the type of events they receive. The SEP will compare the filter values in
 * the {@link Event} and the handler and propagate the Event conditional upon a
 * valid match.
 *
 *
 * @see com.fluxtion.api.annotations.EventHandler
 * @see EventHandler
 *
 * @author Greg Higgins
 *
 */
public abstract class Event implements TimeEvent {

    /**
     * default ID for an event when the user does not explicitly set an ID. Any
     * Event using this value for an ID will dispatch based on class name and
     * not on ID. User defined events should not use the value Integer.MAX_VALUE
     */
    public static final int NO_ID = Integer.MAX_VALUE;
    private final int id;
    protected int filterId;
    protected String filterString;
//    protected CharSequence filterString;
    protected long eventTime;

    public Event() {
        this(NO_ID);
    }

    public Event(int id) {
        this(id, NO_ID);
    }

    public Event(int id, int filterId) {
        this(id, filterId, "");
    }

    public Event(int id, String filterString) {
        this(id, NO_ID, filterString);
    }

    public Event(int id, int filterId, String filterString) {
        this.id = id;
        this.filterId = filterId;
        this.filterString = filterString;
        this.eventTime = System.currentTimeMillis();
    }

    /**
     * The unique int identifier for this event.
     *
     * @return id for this event as an integer
     */
    public final int eventId() {
        return id;
    }

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
    public final int filterId() {
        return filterId;
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
    public final String filterString() {
        return filterString.toString();
    }

    public final CharSequence filterCharSequence() {
        return filterString;
    }

    /**
     * The time the event was created. By default this is implemented with {@link System#currentTimeMillis()
     * } during construction.
     *
     * @return creation time
     */
    @Override
    public long getEventTime() {
        return eventTime;
    }

    /**
     * Override the default value for event creation time. The default value is
     * set with {@link System#currentTimeMillis()} during construction.
     *
     * @param eventTime
     */
    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

}
