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

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.event.RegisterEventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

/**
 * Pushes events published by a {@link StaticEventProcessor} along the filter
 * chain. The registered SEP must accept event subscribers by listening to 
 * {@link RegisterEventHandler} events.
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Data(staticConstructor = "of")
@EqualsAndHashCode(callSuper = false)
@Log4j2
public class SepFilter extends EventFilter {

    private final StaticEventProcessor target;

    @Override
    public void processEvent(Object o) {
        target.onEvent(o);
    }

    @Override
    protected void stopHandler() {
        log.info("stop filter:{}", getClass().getSimpleName());
        if (target instanceof Lifecycle) {
            ((Lifecycle) target).tearDown();
        }
    }

    @Override
    protected void initHandler() {
        log.info("init filter:{}", getClass().getSimpleName());
        if (target instanceof Lifecycle) {
            ((Lifecycle) target).init();
        }
        if (nextHandler != null) {
            target.onEvent(new RegisterEventHandler(nextHandler::processEvent));
        }
    }

}
