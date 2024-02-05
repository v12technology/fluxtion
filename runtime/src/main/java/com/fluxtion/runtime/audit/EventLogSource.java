/*
 * Copyright (C) 2020 2024 gregory higgins.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.runtime.audit;

/**
 * EventLogSource is registered with a {@link EventLogManager} at initialisation time. The
 * EventLogManager injects a configured {@link EventLogger} to this instance via
 * the {@link #setLogger(EventLogger)}  }. A
 * user implements this interface for a node in the execution graph to receive
 * a configured {@link EventLogger}.<br>
 * <p>
 * The node writes to the EventLogger in any of the lifecycle or event methods
 * to record data and the {@code EventLogManager} handles the formatting and
 * marshalling of log records.
 *
 * @author Greg Higgins (greg.higgins@v12technology.com)
 */
public interface EventLogSource {

    /**
     * A configured {@link EventLogger} this EventLogSource can write events to.
     *
     * @param log log target
     */
    void setLogger(EventLogger log);

}
