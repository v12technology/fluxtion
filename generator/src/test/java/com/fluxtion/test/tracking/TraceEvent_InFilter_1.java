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
package com.fluxtion.test.tracking;

import com.fluxtion.api.event.Event;
import java.util.ArrayList;

/**
 *
 * @author Greg Higgins
 */
public class TraceEvent_InFilter_1 extends Event{
    
    
    public static final int ID = 1;

    public TraceEvent_InFilter_1(int filterId) {
        super(ID, filterId);
    }
    
    public int intValue;
    public String strValue;
    public Object objValue;
    ArrayList traceList = new ArrayList();
    ArrayList cacheList = new ArrayList();
    
    public void reset(){
        intValue = 0;
        strValue = null;
        objValue = null;
        traceList.clear();
        cacheList.clear();
    }
}
