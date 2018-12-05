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
package com.fluxtion.test.tracking;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.annotations.OnParentUpdate;

/**
 *
 * @author Greg Higgins
 */
public class Node_DirtyFilter_TraceEvent implements TraceEventHolder {

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
    @OnEvent
    public boolean onEvent() {
        parentUpdated.getTraceEvent().getTraceList().add(this);
        parentUpdated.getTraceEvent().getTraceIdList().add(traceId);
        this.event = parentUpdated.getTraceEvent();
        return (!traceId.equalsIgnoreCase(event.getStrValue()));
    }

    @OnEventComplete
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

}
