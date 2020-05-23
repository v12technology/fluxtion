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
package com.fluxtion.integration.log4j2;

import com.fluxtion.ext.streaming.api.log.LogService;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Data
public class Log4j2LogService implements LogService{

    private final Logger logger;

    public Log4j2LogService(String name) {
        logger = LogManager.getLogger(name);
    }

    public Log4j2LogService(Class name) {
        logger = LogManager.getLogger(name.getCanonicalName());
    }
    
    @Override
    public void debug(CharSequence msg) {
        logger.debug(msg);
    }

    @Override
    public void error(CharSequence msg) {
        logger.error(msg);
    }

    @Override
    public void fatal(CharSequence msg) {
        logger.fatal(msg);
    }

    @Override
    public void info(CharSequence msg) {
        logger.info(msg);
    }

    @Override
    public void trace(CharSequence msg) {
        logger.trace(msg);
    }

    @Override
    public void warn(CharSequence msg) {
        logger.warn(msg);
    }
    
}
