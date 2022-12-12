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

import com.fluxtion.runtime.node.NamedNode;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.OnParentUpdate;

/**
 * @author Greg Higgins
 */
public class Node_TraceEventHolder_Aggregator_NoFiltering implements TraceEventHolder, NamedNode {

    public String traceId;
    public TraceEventHolder[] nodeIntFilter_0;
    private TraceEvent event;

    public Node_TraceEventHolder_Aggregator_NoFiltering(String traceId, TraceEventHolder... nodeIntFilter_0) {
        this.nodeIntFilter_0 = nodeIntFilter_0;
        this.traceId = traceId;
    }

    public Node_TraceEventHolder_Aggregator_NoFiltering() {
    }

    @OnTrigger
    public void onEvent() {
        //no-op - boolean return for dirty filtering test
        getTraceEvent().getTraceList().add(this);
        getTraceEvent().getTraceIdList().add(traceId);
    }

    @OnParentUpdate
    public void onParentUpdate(TraceEventHolder parentHandler) {
        this.event = parentHandler.getTraceEvent();
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
