/* 
 * Copyright (C) 2016-2017 V12 Technology Limited. All rights reserved. 
 *
 * This software is subject to the terms and conditions of its EULA, defined in the
 * file "LICENCE.txt" and distributed with this software. All information contained
 * herein is, and remains the property of V12 Technology Limited and its licensors, 
 * if any. This source code may be protected by patents and patents pending and is 
 * also protected by trade secret and copyright law. Dissemination or reproduction 
 * of this material is strictly forbidden unless prior written permission is 
 * obtained from V12 Technology Limited.  
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
