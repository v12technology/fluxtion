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
package com.fluxtion.ext.streaming.api.window;

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.time.Clock;
import com.fluxtion.ext.streaming.api.Stateful;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
//@Data
public class TimeReset {

    @NoEventReference
    private final Stateful source;
    private final long millisWindowSize;
    @Inject
    @NoEventReference
    private final Clock clock;
    
    
    private long lastCheckTime;
    private boolean reset;

    public TimeReset(Stateful source, long millisWindowSize, Clock clock) {
        this.source = source;
        this.millisWindowSize = millisWindowSize;
        this.clock = clock;
    }
    
    @EventHandler
    public boolean anyEvent(Object o){
        long timeNow = clock.getWallClockTime();
        if(lastCheckTime < 0){
            lastCheckTime = timeNow;
        }
        if(lastCheckTime + millisWindowSize <= timeNow){
            reset = true;
            lastCheckTime = timeNow;
            return true;
        }
        return false;
    }

    @AfterEvent
    public void resetIfNecessary() {
        if (reset) {
            source.reset();
        }
        reset = false;
    }
    
    @Initialise
    public void init(){
        lastCheckTime = -1;
        reset = false;
    }
}
