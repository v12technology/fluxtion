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

import com.fluxtion.api.audit.LogRecord;
import com.fluxtion.api.audit.LogRecordListener;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2(topic = "fluxtion.eventLog")
public class Log4j2AuditLogger implements LogRecordListener{

    private Level level = Level.INFO;
    
    @Override
    public void processLogRecord(LogRecord logRecord) {
        log.log(level, logRecord.asCharSequence());
        log.log(level, "\n---\n");
    }
    
}
