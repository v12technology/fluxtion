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

import com.fluxtion.api.annotations.OnParentUpdate;

/**
 *
 * @author Greg Higgins
 */
public class Node_TraceEvent_Aggregator {

    public String traceId;
    public Node_TraceEvent_IntFilter_0[] nodeIntFilter_0;
    public Node_TraceEvent_0[] node_0;

    public Node_TraceEvent_Aggregator(String traceId, Node_TraceEvent_IntFilter_0... nodeIntFilter_0) {
        this.nodeIntFilter_0 = nodeIntFilter_0;
        this.traceId = traceId;
    }

    public Node_TraceEvent_Aggregator(String traceId, Node_TraceEvent_0... node_0) {
        this.traceId = traceId;
        this.node_0 = node_0;
    }

    
    
    public Node_TraceEvent_Aggregator() {
    }

    @OnParentUpdate
    public void onParentUpdate(Node_TraceEvent_IntFilter_0 parent) {
        parent.event.traceList.add(this);
        parent.event.traceIdList.add(traceId);
    }
    

    @OnParentUpdate
    public void onParentUpdate(Node_TraceEvent_0 parent) {
        parent.event.traceList.add(this);
        parent.event.traceIdList.add(traceId);
    }

}
