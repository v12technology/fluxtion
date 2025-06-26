/*
 * Copyright (c) 2025 gregory higgins.
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

package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.node.BaseNode;

public class BatchDtoHandler extends BaseNode {
    
    @OnEventHandler
    public boolean processBatch(BatchDto batchEvent) {
        auditLog.debug("redispatchBatch", batchEvent);
        for (Object eventDto : batchEvent.getBatchData()) {
            auditLog.debug("redispatchEvent", eventDto);
            context.getStaticEventProcessor().onEvent(eventDto);
        }
        return false;
    }
}
