/*
 * Copyright (C) 2019 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.streaming.api.time;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.audit.Auditor;
import com.fluxtion.api.event.GenericEvent;

/**
 *
 * @author V12 Technology Ltd.
 */
public class Clock implements Auditor {

    private long eventTime;
    private long ingestTime;
    private ClockStrategy wallClock;

    @EventHandler
    public boolean tick(Tick tock) {
        return true;
    }

    @EventHandler
    public boolean timeUpdate(TimeEvent time) {
        eventTime = time.getEventTime();
        return true;
    }

    @EventHandler(propagate = false)
    public void setClockStrategy(GenericEvent<ClockStrategy> event) {
        this.wallClock = event.value;
    }

    @Override
    public void eventReceived(Object event) {
        ingestTime = getWallClockTime();
        if(!(event instanceof TimeEvent)){
            eventTime = ingestTime;
        }else{
            eventTime = ((TimeEvent)event).getEventTime();
        }
    }

    public long getEventTime() {
        return eventTime;
    }

    public long getWallClockTime() {
        return wallClock.getWallClockTIme();
    }

    public long getIngestTime() {
        return ingestTime;
    }

    @Override
    public void nodeRegistered(Object node, String nodeName) {
    }
    
    @Initialise
    public void init(){
        wallClock = System::currentTimeMillis;
    }

}
