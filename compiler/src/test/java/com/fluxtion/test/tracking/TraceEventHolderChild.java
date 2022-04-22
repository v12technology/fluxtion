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

import com.fluxtion.runtime.annotations.OnTrigger;

/**
 *
 * @author Greg Higgins
 */
public class TraceEventHolderChild implements TraceEventHolder{

    public TraceEventHolder parentHandler;
    public TraceEvent event;
    public String traceId;

    public TraceEventHolderChild(String traceId, TraceEventHolder parentHandler) {
        this.parentHandler = parentHandler;
        this.traceId = traceId;
    }

    public TraceEventHolderChild() {
    }

    @OnTrigger
    public void onEvent() {
        parentHandler.getTraceEvent().getTraceList().add(this);
        parentHandler.getTraceEvent().getTraceIdList().add(traceId);
        this.event = parentHandler.getTraceEvent();
    }

    @Override
    public TraceEvent getTraceEvent() {
        return event;
    }
}
