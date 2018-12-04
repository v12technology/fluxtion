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
 * Extends the concept of EventHandler by adding a user defined filter id. Only
 * events that match the filterID and the event type will be processed by an 
 * instance of the FilteredEventHandler.
 * 
 * @author Greg Higgins
 */
public interface FilteredEventHandler<T extends Event> extends EventHandler<T> {
    
    int filterId();
    
}
