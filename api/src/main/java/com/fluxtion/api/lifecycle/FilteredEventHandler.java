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

import com.fluxtion.api.event.Event;

/**
 * Extends the concept of EventHandler by adding a user defined filter id. Only
 * events that match the filterID and the event type will be processed by an
 * instance of the FilteredEventHandler.
 *
 * <h2>Filtering</h2>
 * An EventHandler can optionally provide a filter value and to filter the
 * events that are accepted for processing. An {@link Event} can optionally
 * specify a filter value {@link Event#filterId()
 * }. The SEP will compare the filter values in the {@link Event} and the
 * handler and propagate the Event conditional upon the a match.
 * .<p>
 *
 * @author Greg Higgins
 */
public interface FilteredEventHandler<T extends Event> extends EventHandler<T> {

    int filterId();
    
    default String filterString(){
        return null;
    }

}
