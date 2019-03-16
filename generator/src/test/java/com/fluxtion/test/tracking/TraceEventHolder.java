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
import com.fluxtion.api.annotations.FilterId;

/**
 *
 * @author Greg Higgins
 */
public interface TraceEventHolder {

    TraceEvent getTraceEvent();

    public static class TraceEventHandler_generic  implements TraceEventHolder {

        @FilterId
        private int filter;
        public TraceEvent event;
        public String traceId;

        public TraceEventHandler_generic(String traceId, int filter) {
            this.filter = filter;
            this.traceId = traceId;
        }

        public TraceEventHandler_generic() {
        }

//        @EventHandler
        public void handleEvent(TraceEvent event) {
            event.getTraceList().add(this);
            event.getTraceIdList().add(this.traceId);
            this.event = event;
        }

        @Override
        public TraceEvent getTraceEvent() {
            return event;
        }

    }
    
    public static class TraceEventHandler_sub1 extends TraceEventHandler_generic{

        public TraceEventHandler_sub1(String traceId, int filter) {
            super(traceId, filter);
        }

        public TraceEventHandler_sub1() {
        }

        
        @EventHandler
        public void handleEvent(TraceEvent.TraceEvent_sub1 event) {
            super.handleEvent(event); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    public static class TraceEventHandler_sub2 extends TraceEventHandler_generic{

        public TraceEventHandler_sub2(String traceId, int filter) {
            super(traceId, filter);
        }

        public TraceEventHandler_sub2() {
        }

        @EventHandler
        public void handleEvent(TraceEvent.TraceEvent_sub2 event) {
            super.handleEvent(event); //To change body of generated methods, choose Tools | Templates.
        }
        
    }

}
