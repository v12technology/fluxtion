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
package com.fluxtion.integration.dispatch;

import lombok.extern.log4j.Log4j2;

/**
 * Base class for a filter stage in a event pipeline
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public abstract class EventFilter {

    protected EventFilter nextHandler;

    public final EventFilter next(EventFilter nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }

    public abstract void processEvent(Object o);


    /**
     *
     * Override in subclass to execute init methods on the handler instance
     */
    protected void initHandler() {
        log.info("init filter:{}", this.getClass().getSimpleName());
    }

    /**
     * Override in subclass to execute stop methods on the handler instance
     */
    protected void stopHandler() {
        log.info("stop filter:{}", this.getClass().getSimpleName());
    }
}
