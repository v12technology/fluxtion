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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.runtime.lifecycle;

import com.fluxtion.runtime.event.Event;

/**
 * processes all events of type T. An EventHandler is a node in a Simple Event
 * Processor (SEP), that is the root for processing events in a SEP. Events can
 * only be processed by a SEP if there is an EventHandler registered for that
 * specific type of event.
 * @see com.fluxtion.runtime.event.Event
 * 
 * @author Greg Higgins
 * 
 * @param <T> The type of {@link com.fluxtion.runtime.event.Event Event} processed by this handler
 */
public interface EventHandler<T extends Event> {

    public static final EventHandler NULL_EVENTHANDLER = new EventHandler() {
        @Override
        public void onEvent(Event e) {
        }

        @Override
        public void afterEvent() {
        }
    };

    /**
     * Called when a new event e is ready to be processed.
     *
     * @param e the {@link com.fluxtion.runtime.event.Event Event} to process.
     */
    void onEvent(T e);

    /**
     * called when all nodes that depend upon this EventHadler have successfully
     * completed their processing.
     * 
     */
    default void afterEvent() {
    }
    
    /**
     * The class of the Event processed by this handler
     * 
     * @return Class of {@link com.fluxtion.runtime.event.Event Event} to process 
     */
    default Class<? extends Event> eventClass(){
        return null;
    }

}
