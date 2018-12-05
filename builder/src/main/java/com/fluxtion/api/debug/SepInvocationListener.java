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
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.api.debug;

import com.fluxtion.runtime.event.Event;

/**
 * A callback interface that receives notifications when nodes and life-cycle
 * methods are being invoked by the SEP on the nodes. For the routing of
 * notifications a debug version of the SEP is required that the SepDebugger can
 * interact with.
 *
 *
 * @author Greg Higgins
 */
public interface SepInvocationListener {

    /**
     * Callback method, just prior to the actual invocation of the underlying node.
     * @param triggeringEvent
     * @param node
     * @param nodeName
     */
    void nodePreInvocation(Event triggeringEvent, Object node, String nodeName);
}
