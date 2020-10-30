/*
 * Copyright (c) 2020, V12 Technology Ltd.
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
package com.fluxtion.integration.eventflow.filters;

import com.fluxtion.integration.eventflow.PipelineFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

/**
 * Logs the event using Log4j2, and propagates to the next
 * {@link PipelineFilter}. The logging level is configurable with {@link #setLogLevel(org.apache.logging.log4j.Level)
 * }, default value is info.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
@Data
@EqualsAndHashCode(callSuper = false)
public class Log4j2Filter extends PipelineFilter {

    private Level logLevel = Level.INFO;

    @Override
    public void processEvent(Object o) {
        log.log(logLevel, o);
        propagate(o);
    }

}
