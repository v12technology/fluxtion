/*
 * Copyright (C) 2020 V12 Technology Ltd.
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

import com.fluxtion.runtime.audit.EventLogControlEvent.LogLevel;

/**
 * A logger for an individual {@link EventLogSource} node. Users write values with
 * keys using one of the convenience methods. The {@link EventLogManager} will aggregate
 * all data into a {@link LogRecord} and publish to {@link LogRecordListener}.
 * <br>
 * 
 * The generated {@code LogRecord} is a structure that can be read by machines
 * and humans. 
 *
 * @author Greg Higgins (greg.higgins@v12technology.com)
 */
public class EventLogger {

    private final LogRecord logrecord;
    private final String logSourceId;
    private LogLevel logLevel;

    public EventLogger(LogRecord logrecord, String logSourceId) {
        this.logrecord = logrecord;
        this.logSourceId = logSourceId;
        logLevel = LogLevel.INFO;
    }

    public void setLevel(LogLevel level) {
        logLevel = level;
    }

    public void error() {
        logNodeInvocation(LogLevel.ERROR);
    }

    public void warn() {
        logNodeInvocation(LogLevel.WARN);
    }

    public void info() {
        logNodeInvocation(LogLevel.INFO);
    }

    public void debug() {
        logNodeInvocation(LogLevel.DEBUG);
    }

    public void trace() {
        logNodeInvocation(LogLevel.TRACE);
    }

    public void error(String key, String value) {
        log(key, value, LogLevel.ERROR);
    }

    public void warn(String key, String value) {
        log(key, value, LogLevel.WARN);
    }

    public void info(String key, String value) {
        log(key, value, LogLevel.INFO);
    }

    public void debug(String key, String value) {
        log(key, value, LogLevel.DEBUG);
    }

    public void trace(String key, String value) {
        log(key, value, LogLevel.TRACE);
    }

    public void error(String key, boolean value) {
        log(key, value, LogLevel.ERROR);
    }

    public void warn(String key, boolean value) {
        log(key, value, LogLevel.WARN);
    }
    
    public void error(String key, Object value) {
        log(key, value, LogLevel.ERROR);
    }

    public void warn(String key, Object value) {
        log(key, value, LogLevel.WARN);
    }

    public void info(String key, Object value) {
        log(key, value, LogLevel.INFO);
    }

    public void debug(String key, Object value) {
        log(key, value, LogLevel.DEBUG);
    }

    public void trace(String key, Object value) {
        log(key, value, LogLevel.TRACE);
    }

    public void info(String key, boolean value) {
        log(key, value, LogLevel.INFO);
    }

    public void debug(String key, boolean value) {
        log(key, value, LogLevel.DEBUG);
    }

    public void trace(String key, boolean value) {
        log(key, value, LogLevel.TRACE);
    }

    public void error(String key, double value) {
        log(key, value, LogLevel.ERROR);
    }

    public void warn(String key, double value) {
        log(key, value, LogLevel.WARN);
    }

    public void info(String key, double value) {
        log(key, value, LogLevel.INFO);
    }

    public void debug(String key, double value) {
        log(key, value, LogLevel.DEBUG);
    }

    public void trace(String key, double value) {
        log(key, value, LogLevel.TRACE);
    }

    public void error(String key, char value) {
        log(key, value, LogLevel.ERROR);
    }

    public void warn(String key, char value) {
        log(key, value, LogLevel.WARN);
    }

    public void info(String key, char value) {
        log(key, value, LogLevel.INFO);
    }

    public void debug(String key, char value) {
        log(key, value, LogLevel.DEBUG);
    }

    public void trace(String key, char value) {
        log(key, value, LogLevel.TRACE);
    }

    public void logNodeInvocation(LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addTrace(logSourceId);
        }
    }

    public void log(String key, Object value, LogLevel logLevel){
        log(key, value==null?"NULL":value.toString(), logLevel);
    }
    
    public void log(String key, double value, LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addRecord(logSourceId, key, value);
        }
    }

    public void log(String key, char value, LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addRecord(logSourceId, key, value);
        }
    }

    public void log(String key, CharSequence value, LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addRecord(logSourceId, key, value);
        }
    }

    public void log(String key, boolean value, LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addRecord(logSourceId, key, value);
        }
    }
}
