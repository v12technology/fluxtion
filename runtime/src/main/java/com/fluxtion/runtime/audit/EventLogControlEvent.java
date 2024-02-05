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

import com.fluxtion.runtime.event.Event;

/**
 * Control message to manage the audit logging of events by the
 * {@link EventLogManager} at runtime. Configurable options:
 * <ul>
 * <li> Log level of an EventLogSource instance with {@link #sourceId}.
 * <li> Log level of a group EventLogSource with {@link  #groupId}.
 * <li> Log level of all EventLogSource instances.
 * <li> The LogRecordListener to process LogRecords.
 * </ul>
 *
 * @author Greg Higgins (greg.higgins@v12technology.com)
 */
public class EventLogControlEvent implements Event {

    private LogLevel level;
    /**
     * The name of the node to apply the configuration to. A null value, the
     * default, is no filtering and configuration will be applied to all nodes.
     */
    private String sourceId;
    /**
     * The group Id of a SEP to apply the configuration to. A null value, the
     * default, is no filtering and configuration will be applied to all SEP's.
     */
    private String groupId;
    /**
     * user configured {@link LogRecordListener}
     */
    private LogRecordListener logRecordProcessor;
    /**
     * User configured {@link LogRecord}
     */
    private LogRecord logRecord;

    public EventLogControlEvent() {
        this(LogLevel.INFO);
    }

    public EventLogControlEvent(LogLevel level) {
        this(null, null, level);
    }

    public EventLogControlEvent(LogRecordListener logRecordProcessor) {
        this(null, null, null);
        this.logRecordProcessor = logRecordProcessor;
    }

    public EventLogControlEvent(LogRecord logRecord) {
        this(null, null, null);
        this.logRecord = logRecord;
    }

    public EventLogControlEvent(String sourceId, String groupId, LogLevel level) {
        this.sourceId = sourceId;
        this.groupId = groupId;
        this.level = level;
    }

    public EventLogControlEvent(String sourceId, String groupId, LogLevel level, LogRecordListener logRecordProcessor) {
        this.sourceId = sourceId;
        this.groupId = groupId;
        this.logRecordProcessor = logRecordProcessor;
        this.level = level;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getGroupId() {
        return groupId;
    }

    public LogRecordListener getLogRecordProcessor() {
        return logRecordProcessor;
    }

    public LogRecord getLogRecord() {
        return logRecord;
    }

    public enum LogLevel {
        NONE(0), ERROR(1), WARN(2), INFO(3), DEBUG(4), TRACE(5);

        private LogLevel(int level) {
            this.level = level;
        }

        public final int level;
    }

    @Override
    public String toString() {
        return "EventLogConfig{"
                + "level=" + level
                + ", logRecordProcessor=" + logRecordProcessor
                + ", sourceId=" + sourceId
                + ", groupId=" + groupId
                + '}';
    }

}
