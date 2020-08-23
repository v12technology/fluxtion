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
package com.fluxtion.integration.eventflow.sources;

import com.fluxtion.integration.eventflow.EventConsumer;
import com.fluxtion.integration.eventflow.EventSource;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class ManualEventSource implements EventSource{

    private final String id;
    private EventConsumer target;
    private boolean cacheEventsBeforeStart = true;
    private List cacheEvents = new ArrayList();


    public ManualEventSource(String id) {
        this.id = id;
    }
    
    @Override
    public String id() {
        return id;
    }

    @Override
    public void start(EventConsumer target) {
        this.target = target;
        if(cacheEventsBeforeStart && !cacheEvents.isEmpty()){
            cacheEvents.forEach(this::publishToFlow);
        }
        cacheEvents.clear();
    }

    public void publishToFlow(Object event){
        if(target==null && cacheEventsBeforeStart){
            cacheEvents.add(event);
        }else{
            target.processEvent(event);
        }
    }

    public boolean cacheEventsBeforeStart() {
        return cacheEventsBeforeStart;
    }

    public void cacheEventsBeforeStart(boolean cacheEventsBeforeStart) {
        this.cacheEventsBeforeStart = cacheEventsBeforeStart;
    }
    
}
