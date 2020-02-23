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
package com.fluxtion.api.time;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.audit.Auditor;
import com.fluxtion.api.event.Event;
import com.fluxtion.api.event.GenericEvent;

/**
 * A clock instance in a static event processor, use the @Inject annotation to
 * ensure the same of instance of the clock is used for all nodes. Clock
 * provides time query functionality for the processor as follows:
 *
 * <ul>
 * <li>WallClock - current time UTC milliseconds</li>
 * <li>ProcessTime - the time the event was received for processing</li>
 * <li>EventTime - the time the event was created</li>
 * </ul>
 *
 * @author V12 Technology Ltd.
 */
public class Clock implements Auditor {

    private long eventTime;
    private long processTime;
    private ClockStrategy wallClock;

    @Override
    public void eventReceived(Event event) {
        processTime = getWallClockTime();
        eventTime = event.getEventTime();
    }

    @Override
    public void eventReceived(Object event) {
        processTime = getWallClockTime();
        eventTime = processTime;
    }

    @Override
    public void nodeRegistered(Object node, String nodeName) {/*NoOp*/
    }

    @EventHandler(propagate = false)
    public void setClockStrategy(GenericEvent<ClockStrategy> event) {
        this.wallClock = event.value;
    }

    /**
     * The time the last event was received by the processor
     *
     * @return time the last event was received for processing
     */
    public long getProcessTime() {
        return processTime;
    }

    /**
     * The time the latest event was created
     *
     * @return time the latest event was created
     */
    public long getEventTime() {
        return eventTime;
    }

    /**
     * Current wallclock time in milliseconds UTC
     *
     * @return time in milliseconds UTC
     */
    public long getWallClockTime() {
        return wallClock.getWallClockTime();
    }

    @Initialise
    @Override
    public void init() {
        wallClock = System::currentTimeMillis;
    }

}
