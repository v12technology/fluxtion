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

import com.fluxtion.runtime.audit.EventLogControlEvent.LogLevel;

/**
 * A logger for an individual {@link EventLogSource} node. Users write values with
 * keys using one of the convenience methods. The {@link EventLogManager} will aggregate
 * all data into a {@link LogRecord} and publish to {@link LogRecordListener}.
 * <br>
 * <p>
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

    public EventLogger setLevel(LogLevel level) {
        logLevel = level;
        return this;
    }

    public EventLogger error() {
        logNodeInvocation(LogLevel.ERROR);
        return this;
    }

    public EventLogger warn() {
        logNodeInvocation(LogLevel.WARN);
        return this;
    }

    public EventLogger info() {
        logNodeInvocation(LogLevel.INFO);
        return this;
    }

    public EventLogger debug() {
        logNodeInvocation(LogLevel.DEBUG);
        return this;
    }

    public EventLogger trace() {
        logNodeInvocation(LogLevel.TRACE);
        return this;
    }

    public EventLogger error(String key, String value) {
        log(key, value, LogLevel.ERROR);
        return this;
    }

    public EventLogger warn(String key, String value) {
        log(key, value, LogLevel.WARN);
        return this;
    }

    public EventLogger info(String key, String value) {
        log(key, value, LogLevel.INFO);
        return this;
    }

    public EventLogger debug(String key, String value) {
        log(key, value, LogLevel.DEBUG);
        return this;
    }

    public EventLogger trace(String key, String value) {
        log(key, value, LogLevel.TRACE);
        return this;
    }

    public EventLogger error(String key, boolean value) {
        log(key, value, LogLevel.ERROR);
        return this;
    }

    public EventLogger warn(String key, boolean value) {
        log(key, value, LogLevel.WARN);
        return this;
    }

    public EventLogger error(String key, Object value) {
        log(key, value, LogLevel.ERROR);
        return this;
    }

    public EventLogger warn(String key, Object value) {
        log(key, value, LogLevel.WARN);
        return this;
    }

    public EventLogger info(String key, Object value) {
        log(key, value, LogLevel.INFO);
        return this;
    }

    public EventLogger debug(String key, Object value) {
        log(key, value, LogLevel.DEBUG);
        return this;
    }

    public EventLogger trace(String key, Object value) {
        log(key, value, LogLevel.TRACE);
        return this;
    }

    public EventLogger info(String key, boolean value) {
        log(key, value, LogLevel.INFO);
        return this;
    }

    public EventLogger debug(String key, boolean value) {
        log(key, value, LogLevel.DEBUG);
        return this;
    }

    public EventLogger trace(String key, boolean value) {
        log(key, value, LogLevel.TRACE);
        return this;
    }

    public EventLogger error(String key, double value) {
        log(key, value, LogLevel.ERROR);
        return this;
    }

    public EventLogger warn(String key, double value) {
        log(key, value, LogLevel.WARN);
        return this;
    }

    public EventLogger info(String key, double value) {
        log(key, value, LogLevel.INFO);
        return this;
    }

    public EventLogger debug(String key, double value) {
        log(key, value, LogLevel.DEBUG);
        return this;
    }

    public EventLogger trace(String key, double value) {
        log(key, value, LogLevel.TRACE);
        return this;
    }

    public EventLogger error(String key, int value) {
        log(key, value, LogLevel.ERROR);
        return this;
    }

    public EventLogger warn(String key, int value) {
        log(key, value, LogLevel.WARN);
        return this;
    }

    public EventLogger info(String key, int value) {
        log(key, value, LogLevel.INFO);
        return this;
    }

    public EventLogger debug(String key, long value) {
        log(key, value, LogLevel.DEBUG);
        return this;
    }

    public EventLogger trace(String key, long value) {
        log(key, value, LogLevel.TRACE);
        return this;
    }

    public EventLogger error(String key, long value) {
        log(key, value, LogLevel.ERROR);
        return this;
    }

    public EventLogger warn(String key, long value) {
        log(key, value, LogLevel.WARN);
        return this;
    }

    public EventLogger info(String key, long value) {
        log(key, value, LogLevel.INFO);
        return this;
    }

    public EventLogger debug(String key, int value) {
        log(key, value, LogLevel.DEBUG);
        return this;
    }

    public EventLogger trace(String key, int value) {
        log(key, value, LogLevel.TRACE);
        return this;
    }

    public EventLogger error(String key, char value) {
        log(key, value, LogLevel.ERROR);
        return this;
    }

    public EventLogger warn(String key, char value) {
        log(key, value, LogLevel.WARN);
        return this;
    }

    public EventLogger info(String key, char value) {
        log(key, value, LogLevel.INFO);
        return this;
    }

    public EventLogger debug(String key, char value) {
        log(key, value, LogLevel.DEBUG);
        return this;
    }

    public EventLogger trace(String key, char value) {
        log(key, value, LogLevel.TRACE);
        return this;
    }

    public EventLogger logNodeInvocation(LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addTrace(logSourceId);
        }
        return this;
    }

    public EventLogger log(String key, Object value, LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addRecord(logSourceId, key, value);
        }
        return this;
    }

    public EventLogger log(String key, double value, LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addRecord(logSourceId, key, value);
        }
        return this;
    }

    public EventLogger log(String key, int value, LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addRecord(logSourceId, key, value);
        }
        return this;
    }

    public EventLogger log(String key, long value, LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addRecord(logSourceId, key, value);
        }
        return this;
    }

    public EventLogger log(String key, char value, LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addRecord(logSourceId, key, value);
        }
        return this;
    }

    public EventLogger log(String key, CharSequence value, LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addRecord(logSourceId, key, value);
        }
        return this;
    }

    public EventLogger log(String key, boolean value, LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addRecord(logSourceId, key, value);
        }
        return this;
    }

    public boolean canLog(LogLevel logLevel) {
        return this.logLevel != null && this.logLevel.level >= logLevel.level;
    }
}
