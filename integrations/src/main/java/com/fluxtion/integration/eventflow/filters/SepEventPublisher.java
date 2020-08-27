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
package com.fluxtion.integration.eventflow.filters;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.event.RegisterEventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.integration.eventflow.EventSink;
import com.fluxtion.integration.eventflow.PipelineFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

/**
 * Propagates events into a StaticEventProcessor as part of the pipeline. If the
 * StaticEventProcessor generates events they can be propagated along the filter
 * chain. The registered SEP must accept event subscribers by listening to
 * {@link RegisterEventHandler} events.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Data(staticConstructor = "of")
@EqualsAndHashCode(callSuper = false)
@Log4j2
public class SepEventPublisher extends PipelineFilter {

    private final StaticEventProcessor target;
    private boolean propagate = true;

    @Override
    public void processEvent(Object o) {
        target.onEvent(o);
    }

    @Override
    protected void stopHandler() {
        log.info("stop sep:'{}'", target.getClass().getSimpleName());
        if (target instanceof Lifecycle) {
            ((Lifecycle) target).tearDown();
        }
    }

    @Override
    protected void registeEventSink(EventSink sink) {
        super.registeEventSink(sink);
        log.info("registering EventSink id:'{}' with sep:'{}'", sink.id(), target.getClass().getSimpleName());
        target.onEvent(new RegisterEventHandler(sink::publish));
    }
    
    @Override
    protected void initHandler() {
        log.info("init sep:'{}'", target.getClass().getSimpleName());
        if (target instanceof Lifecycle) {
            ((Lifecycle) target).init();
        }
        if (propagate) {
            log.info("registering a propagation endpoint to push events along the pipeline");
            target.onEvent(new RegisterEventHandler(this::propagate));
        } else {
            log.info("No propagation along the pipeline, all events will be consumed");
        }
    }

}
