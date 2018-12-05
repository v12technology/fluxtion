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
package com.fluxtion.runtime.audit;

import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.Lifecycle;

/**
 * Auditor for a static event processor. User registers an implementation of
 * this interface with SEPConfig.addAuditor(auditor, String). A registered
 * Auditor receives lifecycle callbacks for registration of nodes and processing
 * of events, see javadoc below for details.<br>
 *
 * An Auditor can provide various meta functions for the SEP they are registered
 * with, such as:
 * <ul>
 * <li> Generic event logger
 * <li> A performance monitor
 * <li> A realtime property tracer
 * <li> Commit/rollback functionality
 * <li> A profiler
 * </ul>
 *
 * @author V12 Technology Limited
 */
public interface Auditor extends Lifecycle {

    /**
     * Callback for each node registered in the SEP. This method will be invoked
     * after init, but before any event processing methods are invoked.
     *
     * @param node The node instance in the SEP
     * @param nodeName The unique name of the node in the SEP
     */
    void nodeRegistered(Object node, String nodeName);

    /**
     * Callback indicating the Event to be processed by the SEP nodes. Will be
     * called before any node has processed the event.
     *
     * @param event the event to be processed
     */
    default void eventReceived(Event event) {
    }

    /**
     * Callback indicating the Event to be processed by the SEP nodes as an
     * Object. Will be called before any node has processed the event.
     *
     * @param event the event to be processed
     */
    default void eventReceived(Object event) {
    }

    /**
     * Callback to indicate all nodes have processed the Event and the execution
     * path for that event is complete.
     */
    default void processingComplete() {
    }

    @Override
    default void init() {
    }

    @Override
    default void tearDown() {
    }

//    default void nodeInvoked(Object node, String nodeName, String methodName, Event typedEvent) {
//    }
    /**
     * Callback method received by the auditor due to processing an event. This
     * method is invoked before the node in the execution path receives a
     * notification.
     *
     * @param node The next node to process in the execution path
     * @param nodeName The name of the node, this is the same name as the
     * variable name of the node in the SEP
     * @param methodName The method of the node that is next to be invoked in
     * the execution path.
     * @param event The event that is the root of the of this execution path.
     */
    default void nodeInvoked(Object node, String nodeName, String methodName, Object event) {
    }

    /**
     * Indicates whether an auditor is interested in receiving nodeInvoked
     * event callback. Some auditors are not interested in granular monitoring
     * of the execution path and can opt out of node invocation callbacks.
     * <ul>
     * <li>true - auditor receives all lifecycle callbacks</li>
     * <li>false - auditor receives all lifecycle callbacks except:
     * nodeInvoked</li>
     * </ul>
     *
     * @return intention to receive all lifecycle callbacks.
     */
    default boolean auditInvocations() {
        return false;
    }
}
