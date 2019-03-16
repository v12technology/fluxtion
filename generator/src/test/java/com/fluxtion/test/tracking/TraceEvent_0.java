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
package com.fluxtion.test.tracking;

import com.fluxtion.api.event.Event;
import java.util.ArrayList;

/**
 *
 * @author Greg Higgins
 */
public class TraceEvent_0 extends Event{
    
    
    public static final int ID = 1;

    public TraceEvent_0() {
        super(ID);
    }
    
    public int intValue;
    public String strValue;
    public Object objValue;
    public ArrayList traceList = new ArrayList();
    public ArrayList<String> traceIdList = new ArrayList();
    
    public void reset(){
        intValue = 0;
        strValue = null;
        objValue = null;
        traceList.clear();
        traceIdList.clear();
    }
}
