/*
 * Copyright (c) 2020, 2024 gregory higgins.
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
package com.fluxtion.runtime.audit;

/**
 * Base class that adds {@link EventLogger} functionality.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class EventLogNode implements EventLogSource {

    protected EventLogger auditLog = NullEventLogger.INSTANCE;

    @Override
    public void setLogger(EventLogger log) {
        this.auditLog = log;
    }

}
