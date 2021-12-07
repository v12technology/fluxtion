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
package com.fluxtion.test.event;

import com.fluxtion.runtim.annotations.EventHandler;
import com.fluxtion.runtim.annotations.FilterId;

/**
 *
 * @author Greg Higgins
 */
public class AnnotatedHandlerStringFilter {
    @FilterId
    public String filterString;

    public AnnotatedHandlerStringFilter() {
        filterString = "no filter";
    }
    
    
    
    @EventHandler
    public void onTime(TimeEvent e){
        
    }
    
    @EventHandler
    public void onTest(TestEvent e){
        
    }
}
