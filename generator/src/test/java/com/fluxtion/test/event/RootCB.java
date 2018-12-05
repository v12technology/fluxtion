/* 
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.test.event;

import com.fluxtion.api.annotations.OnBatchEnd;
import com.fluxtion.api.annotations.OnBatchPause;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.TearDown;
import com.fluxtion.runtime.event.Event;

/**
 *
 * @author Greg Higgins
 */
public class RootCB {
    
    public Object[] parents;
    public String id;
    public boolean onEventCalled = false;

    public RootCB(String id, Object... parents) {
        this.parents = parents;
        this.id = id;
    }

    public RootCB(String id) {
        this.id = id;
    }

    public RootCB() {
    }
        
    @TearDown
    public void tearDown(){
        
    }
    
    @OnEvent
    public void onEvent(){
        onEventCalled = true;
    }

    @OnBatchEnd
    public void onRootBatchEnd(){
        
    }
    
    @OnBatchPause
    public void onRootBatchPause(){
        
    }
    
    @Override
    public String toString() {
        return "EventCB{" + "id=" + id + '}';
    }

    
}
