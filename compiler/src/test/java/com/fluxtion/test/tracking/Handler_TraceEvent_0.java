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

import com.fluxtion.runtime.annotations.OnEventHandler;

/**
 * @author Greg Higgins
 */
public class Handler_TraceEvent_0 {

    public TraceEvent_0 event;
    public String traceId;

    public Handler_TraceEvent_0(String traceId) {
        this.traceId = traceId;
    }

    public Handler_TraceEvent_0() {
    }

    @OnEventHandler
    public boolean handleEvent(TraceEvent_0 event) {
        event.traceList.add(this);
        event.traceIdList.add(this.traceId);
        this.event = event;
        return true;
    }
}
