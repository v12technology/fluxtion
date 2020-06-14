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

import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;

/**
 * Base class for a filter stage in a event pipeline
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public abstract class PipelineFilter implements EventConsumer {

    private final List<PipelineFilter> handlers;

    public PipelineFilter() {
        this.handlers = new ArrayList<>();
    }

    public final PipelineFilter next(PipelineFilter nextHandler) {
        handlers.add(nextHandler);
        return nextHandler;
    }

    @Override
    public abstract void processEvent(Object o);
    
    protected final void propagate(Object o){
        for (int i = 0; i < handlers.size(); i++) {
            PipelineFilter filter = handlers.get(i);
            filter.processEvent(o);
        }
    }

    /**
     *
     * Override in subclass to execute init methods on the handler instance
     */
    protected void initHandler() {
        log.info("init pipeline filter:{}", this.getClass().getSimpleName());
    }

    /**
     * Override in subclass to execute stop methods on the handler instance
     */
    protected void stopHandler() {
        log.info("stop pipeline filter:{}", this.getClass().getSimpleName());
    }
}
