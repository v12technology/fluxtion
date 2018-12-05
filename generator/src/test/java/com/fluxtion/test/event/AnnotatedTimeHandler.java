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

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.FilterId;

/**
 *
 * @author Greg Higgins
 */
public class AnnotatedTimeHandler {
    @FilterId
    private final int stateId;

    public AnnotatedTimeHandler() {
        this(0);
    }

    public AnnotatedTimeHandler(int id) {
        this.stateId = id;
    }
    
    @EventHandler
    public void onTime(TimeEvent e){
        
    }
    
    @EventHandler
    public void onTest(TestEvent e){
        
    }
}
