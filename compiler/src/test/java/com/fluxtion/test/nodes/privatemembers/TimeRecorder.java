/* 
 * Copyright (c) 2019, V12 Technology Ltd.
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
package com.fluxtion.test.nodes.privatemembers;

import com.fluxtion.runtim.annotations.Initialise;
import com.fluxtion.runtim.annotations.OnEvent;

/**
 * Class for testing private member assignment and event dispatch.
 * 
 * @author Greg Higgins
 */
public class TimeRecorder {
    
    private final TimeHandler tickNotifier;
    private long recordedTime;
    private final long offset;
    
    public TimeRecorder(TimeHandler tickNotifier, long offset) {
        this.tickNotifier = tickNotifier;
        this.offset = offset;
    }
    
    @Initialise
    public void init(){
        recordedTime = 0;
    }
    
    @OnEvent
    public void update(){
        recordedTime = tickNotifier.getTickTime();
    }

    public long getRecordedTime() {
        return recordedTime + offset;
    }
    
}
