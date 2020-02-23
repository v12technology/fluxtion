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
package com.fluxtion.api;

import com.fluxtion.api.annotations.EventHandler;

/**
 * Processes events of any type and dispatches to registered {@link FilteredEventHandler}
 * and methods annotated with {@link EventHandler}. An subclass of a StaticEventProcessor is
 * the product of running the event stream compiler on user input. On receipt of an event
 * the processor selects an execution path that comprises a set of application nodes that
 * have a reference to an incoming {@link EventHandler} for the specific event.
 * <p>
 * The StaticEventProcessor 
 * has the following functionality:
 * 
 * <ul>
 * <li>StaticEventProcessor process events of multiple type in a predictable order</li>
 * <li>Application classes are nodes managed by StaticEventProcessor and are notified in a predictable manner when an event is processed</li>
 * <li>An execution path is the set of connected nodes to a matching {@link EventHandler} for the incoming event</li>
 * <li>Nodes on the execution path are invoked in topological order, where object reference determine precedence</li>
 * <li>The root of the execution path is an {@link EventHandler}</li>
 * <li>Dispatches events based on type to the correct handler</li>
 * <li>Optional String or int filtering can be supplied to narrow the handler selection in conjunction with event type</li>
 * <li>An execution path that is unique for the Event and filter is invoked when an event is received.</li>
 * <li>All node instances are created and managed by StaticEventProcessor</li>
 * </ul>
 * starting point of event dispatch
 *
 * @author Greg Higgins
 */
public interface StaticEventProcessor {

    StaticEventProcessor NULL_EVENTHANDLER = new StaticEventProcessor() {
        @Override
        public void onEvent(Object e) {
        }

    };

    /**
     * Called when a new event e is ready to be processed.
     *
     * @param e the {@link com.fluxtion.api.event.Event Event} to process.
     */
    void onEvent(Object e);


}
