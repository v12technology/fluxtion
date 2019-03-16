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

import com.fluxtion.api.annotations.OnEventComplete;
import java.util.ArrayList;

/**
 *
 * @author Greg Higgins
 */
public interface TraceEvent {

    int getIntValue();

    Object getObjValue();

    String getStrValue();

    ArrayList<String> getTraceIdList();

    ArrayList getTraceList();

    void reset();
    
    int filterId();
    
//    int eventId();

    ArrayList<String> getTraceAfterEventIdList();

    ArrayList<String> getTraceEventCompleteIdList();
    
    
    public static class TraceEvent_sub1 extends TraceEvent_InFilter_0 implements TraceEvent{

        public static final int ID = 2;
        
        public TraceEvent_sub1(int filterId) {
            super(ID, filterId);
        }
        
    }
    
    public static class TraceEvent_sub2 extends TraceEvent_InFilter_0 implements TraceEvent{

        public static final int ID = 3;
        
        public TraceEvent_sub2(int filterId) {
            super(ID, filterId);
        }
        
    }
}
