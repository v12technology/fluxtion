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
package com.fluxtion.integrations.dispatch;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.event.EventPublisher;
import com.fluxtion.api.event.RegisterEventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class ForwardingSep implements StaticEventProcessor, Lifecycle{

    private EventPublisher<?> publisher;

    @Override
    public void init() {
        log.info("init forwarding SEP");
        publisher = new EventPublisher<>();
        publisher.init();
    }
    
    @Override
    public void onEvent(Object e) {
        log.debug("processing event:'{}'", e);
        if(e instanceof RegisterEventHandler){
            publisher.registerEventHandler((RegisterEventHandler) e);
        }else{
            publisher.nodeUpdate(e);
        }
    }

    @Override
    public void tearDown() {
        log.info("teardown forwarding SEP");
    }
    
}
