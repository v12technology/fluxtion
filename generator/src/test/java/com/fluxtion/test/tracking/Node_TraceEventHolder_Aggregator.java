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

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.annotations.OnParentUpdate;

/**
 *
 * @author Greg Higgins
 */
public class Node_TraceEventHolder_Aggregator implements TraceEventHolder {

    public String traceId;
    public TraceEventHolder[] nodeIntFilter_0;
    private TraceEvent event;

    public Node_TraceEventHolder_Aggregator(String traceId, TraceEventHolder... nodeIntFilter_0) {
        this.nodeIntFilter_0 = nodeIntFilter_0;
        this.traceId = traceId;
    }

    public Node_TraceEventHolder_Aggregator() {
    }

    @OnEvent
    public boolean onEvent() {
        //no-op - boolean return for dirty filtering test
        getTraceEvent().getTraceList().add(this);
        getTraceEvent().getTraceIdList().add(traceId);
        return true;
    }

    @OnParentUpdate
    public void onParentUpdate(TraceEventHolder parentHandler) {
        this.event = parentHandler.getTraceEvent();
    }

    @Override
    public TraceEvent getTraceEvent() {
        return event;
    }

    public static class AfterEventTrace extends Node_TraceEventHolder_Aggregator {

        public AfterEventTrace(String traceId, TraceEventHolder... nodeIntFilter_0) {
            super(traceId, nodeIntFilter_0);
        }

        public AfterEventTrace() {
        }

        @Override
        @OnParentUpdate
        public void onParentUpdate(TraceEventHolder parentHandler) {
            super.onParentUpdate(parentHandler); 
        }

        @Override
        @OnEvent
        public boolean onEvent() {
            return super.onEvent(); 
        }
        
        @AfterEvent
        public void afterEvent(){
            if(getTraceEvent()!=null)
                getTraceEvent().getTraceAfterEventIdList().add(traceId);
        }

    }

    public static class EventCompleteTrace extends Node_TraceEventHolder_Aggregator {

        public EventCompleteTrace(String traceId, TraceEventHolder... nodeIntFilter_0) {
            super(traceId, nodeIntFilter_0);
        }

        public EventCompleteTrace() {
        }

        @Override
        @OnParentUpdate
        public void onParentUpdate(TraceEventHolder parentHandler) {
            super.onParentUpdate(parentHandler); 
        }

        @Override
        @OnEvent
        public boolean onEvent() {
            return super.onEvent(); 
        }
        
        @OnEventComplete
        public void onEventComplete(){
            getTraceEvent().getTraceEventCompleteIdList().add(traceId);
        }

    }
}
