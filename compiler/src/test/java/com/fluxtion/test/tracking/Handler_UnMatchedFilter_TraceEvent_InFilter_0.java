/*
 * Copyright (c) 2019, 2024 gregory higgins.
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
import com.fluxtion.runtime.annotations.FilterType;

/**
 * Demonstrates use of the FilterType.unmatched value on the EventHandler.
 *
 * @author Greg Higgins
 */
public class Handler_UnMatchedFilter_TraceEvent_InFilter_0 extends HandlerNoFilter_TraceEvent_InFilter_0 {

    public Handler_UnMatchedFilter_TraceEvent_InFilter_0(String traceId) {
        super(traceId);
    }

    public Handler_UnMatchedFilter_TraceEvent_InFilter_0() {
    }

    @Override
    @OnEventHandler(FilterType.defaultCase)
    public boolean handleEvent(TraceEvent_InFilter_0 event) {
        super.handleEvent(event);
        return true;
    }

}
