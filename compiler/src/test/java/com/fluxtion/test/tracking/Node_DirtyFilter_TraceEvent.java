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

import com.fluxtion.runtime.Named;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.AfterTrigger;
import com.fluxtion.runtime.annotations.OnParentUpdate;

/**
 *
 * @author Greg Higgins
 */
public class Node_DirtyFilter_TraceEvent implements TraceEventHolder, Named {

    public TraceEventHolder parentUpdated;
    public TraceEvent event;
    public String traceId;
    public TraceEventHolder[] parents;

    public Node_DirtyFilter_TraceEvent(String traceId, TraceEventHolder... parents) {
        this.traceId = traceId;
        this.parents = parents;
    }

    public Node_DirtyFilter_TraceEvent() {
    }

    /**
     * boolean return controls dirty filtering. Returns false if traceId matches
     * strValue of the TraceEvent, should arrest the call chain when matched.
     *
     * @return
     */
    @OnTrigger
    public boolean onEvent() {
        parentUpdated.getTraceEvent().getTraceList().add(this);
        parentUpdated.getTraceEvent().getTraceIdList().add(traceId);
        this.event = parentUpdated.getTraceEvent();
        return (!traceId.equalsIgnoreCase(event.getStrValue()));
    }

    @AfterTrigger
    public void OnEventComplete(){
        
    }
    
    @OnParentUpdate
    public void onParentUpdate(TraceEventHolder parent) {
        parentUpdated = parent;
    }

    @Override
    public TraceEvent getTraceEvent() {
        return event;
    }

    @Override
    public String getName() {
        return traceId;
    }
}
