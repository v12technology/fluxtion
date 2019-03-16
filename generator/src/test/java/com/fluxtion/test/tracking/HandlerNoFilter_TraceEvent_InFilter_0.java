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

import com.fluxtion.api.annotations.EventHandler;

/**
 *
 * @author Greg Higgins
 */
public class HandlerNoFilter_TraceEvent_InFilter_0 implements TraceEventHolder{
    

    public TraceEvent_InFilter_0 event;
    public String traceId;

    public HandlerNoFilter_TraceEvent_InFilter_0(String traceId) {
        this.traceId = traceId;
    }

    public HandlerNoFilter_TraceEvent_InFilter_0() {
    }
    
    @EventHandler()
    public void handleEvent(TraceEvent_InFilter_0 event){
        event.traceList.add(this);
        event.traceIdList.add(this.traceId);
        this.event = event;
    }

    @Override
    public TraceEvent getTraceEvent() {
        return event;
    }
}
