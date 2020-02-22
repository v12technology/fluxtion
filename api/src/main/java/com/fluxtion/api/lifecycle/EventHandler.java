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
package com.fluxtion.api.lifecycle;

/**
 * processes all events of type T. An EventHandler is a node in a Simple Event
 * Processor (SEP), that is the root for processing events in a SEP. Events can
 * only be processed by a SEP if there is an EventHandler registered for that
 * specific type of event.
 *
 * @author Greg Higgins
 */
public interface EventHandler {

    EventHandler NULL_EVENTHANDLER = new EventHandler() {
        @Override
        public void onEvent(Object e) {
        }

        @Override
        public void afterEvent() {
        }
    };

    /**
     * Called when a new event e is ready to be processed.
     *
     * @param e the {@link com.fluxtion.api.event.Event Event} to process.
     */
    void onEvent(Object e);

    /**
     * called when all nodes that depend upon this EventHadler have successfully
     * completed their processing.
     *
     */
    default void afterEvent() {
    }


}
