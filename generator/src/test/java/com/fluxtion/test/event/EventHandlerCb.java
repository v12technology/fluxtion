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
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.test.event;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.TearDown;
import com.fluxtion.runtime.lifecycle.AbstractFilteredEventHandler;

/**
 *
 * @author Greg Higgins
 */
public class EventHandlerCb extends AbstractFilteredEventHandler<TestEvent>{
    
    public String id;

    public EventHandlerCb(String id, int filterId) {
        super(filterId);
        this.id = id;
    }

    public EventHandlerCb() {
    }
    
        
    @TearDown
    public void tearDown(){
        
    }
    
    @OnEvent
    public void onParentChange(){
    }

    @Override
    public void onEvent(TestEvent e) {
    }

    @Override
    public String toString() {
        return "EventHandlerCb{" + "id=" + id + ", filterId=" + filterId + '}';
    }


}
