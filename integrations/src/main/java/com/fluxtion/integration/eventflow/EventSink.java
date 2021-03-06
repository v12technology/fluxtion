/*
 * Copyright (c) 2020, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.integration.eventflow;

import com.fluxtion.api.lifecycle.Lifecycle;

/**
 * Consumes events and publishes to an external endpoint. Events sent to an
 * EventSink do not propagate along the pipeline.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public interface EventSink extends Lifecycle {

    /**
     * A unique identifier for this {@link EventSource}
     *
     * @return identifier
     */
    default String id(){
        return EventSink.class.getSimpleName();
    }

    void publish(Object o);

    @Override
    default void tearDown(){}

    @Override
    default void init(){}
    
    
}
