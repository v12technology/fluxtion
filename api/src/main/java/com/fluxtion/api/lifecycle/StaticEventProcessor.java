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

import com.fluxtion.api.annotations.EventHandler;

/**
 * Processes events of any type and dispatches to registered {@link FilteredEventHandler}
 * and methods annotated with {@link EventHandler}
 *
 * @author Greg Higgins
 */
public interface StaticEventProcessor {

    StaticEventProcessor NULL_EVENTHANDLER = new StaticEventProcessor() {
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
