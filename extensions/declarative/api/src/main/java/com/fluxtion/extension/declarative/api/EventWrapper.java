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
package com.fluxtion.extension.declarative.api;

import com.fluxtion.runtime.event.Event;

/**
 * Wraps an underlying event node in a SEP
 * 
 * @author Greg Higgins
 * @param <T> Event type
 */
public interface EventWrapper <T extends Event> extends Wrapper<T>{
    
    @Override
    T event();
    
    @Override
    Class<T> eventClass();
    
}
