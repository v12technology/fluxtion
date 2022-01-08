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
package com.fluxtion.runtime.lifecycle;

/**
 * A callback interface used to signify that a transaction of events have been
 * received and complete. A common usage pattern is to process a set of events
 * before publishing/exposing state changes outside of the Static Event
 * Processor. An example would be a set of bank transfers broken into separate
 * events:
 *
 * <ul>
 * <li>DeleteEvent - delete amount from account A
 * <li>AddEvent - add to account B from A
 * <li>DeleteEvent - delete amount from account C
 * <li>AddEvent - add to account B from C
 * <li>AddEvent - add to account A from D
 * <li>DeleteEvent - delete amount from account D
 * <li>batchEnd() - publish updated accounts for A,B,C,D
 * </ul>
 *
 * The batchPause callback is used to tell the static event processor more
 * messages are expected but have not been received yet. 
 *
 * @author Greg Higgins
 */
public interface BatchHandler {

    /**
     * Indicates more events are expected, but there is an unknown pause in the message flow
     */
    void batchPause();

    /**
     * Indicates all events for a transaction have been received.
     */
    void batchEnd();
}
