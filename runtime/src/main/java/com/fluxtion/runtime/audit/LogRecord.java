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
import com.fluxtion.runtime.time.Clock;
import lombok.Getter;
import lombok.Setter;

import java.util.function.ObjLongConsumer;

/**
 * A structured log record that can be easily converted to a long term store,
 * such as a rdbms for later analysis. The LogRecord creates a yaml
 * representation of the LogRecord for simplified marshaling.
 * <br>
 * <p>
 * A log record is triggered when an event is processed and written at the end
 * of the execution loop. A sample record:
 *
 * <pre>
 * eventLogRecord:
 *   eventTime: 1578236915413
 *   logTime: 1578236915413
 *   groupingId: null
 *   event: CharEvent
 *   nodeLogs:
 *     - parentNode_1: { char: a, prop: 32}
 *     - childNode_3: { child: true}
 *   endTime: 1578236915413
 * <pre>
 * <ul>
 * <li>eventTime: the time the event was created
 * <li>logTime: the time the log record is created i.e. when the event processing began
 * <li>endTime: the time the log record is complete i.e. when the event processing completed
 * <li>groupingId: a set of nodes can have a groupId and their configuration is controlled as a group
 * <li>event: The simple class name of the event that created the execution
 * <li>nodeLogs: are recorded in the order the event wave passes through the graph.
 * Only nodes that on the execution path have an entry in the nodeList element.
 * The key of a node is the instance name in the java code, followed by a set of key/value properties the node logs.
 * </ul>
 *
 * @author Greg Higgins (greg.higgins@v12technology.com)
 */
public class LogRecord {

    /**
     * The id of the instance producing the record. GroupingId can be used to
     * group LogRecord's together.
     */
    public String groupingId;
    @Getter
    private EventLogControlEvent.LogLevel logLevel;
    protected final StringBuilder sb;
    protected String sourceId;
    protected boolean firstProp;
    @Setter
    protected Clock clock;
    protected boolean printEventToString = false;
    @Setter
    protected boolean printThreadName = false;
    @Setter
    protected ObjLongConsumer<StringBuilder> timeFormatter = StringBuilder::append;

    public LogRecord(Clock clock) {
        this(clock, EventLogControlEvent.LogLevel.INFO);
    }

    public LogRecord(Clock clock, EventLogControlEvent.LogLevel logLevel) {
        sb = new StringBuilder();
        firstProp = true;
        this.clock = clock;
        updateLogLevel(logLevel);
    }

    public void updateLogLevel(EventLogControlEvent.LogLevel newLogLevel) {
        this.logLevel = newLogLevel == null ? EventLogControlEvent.LogLevel.NONE : newLogLevel;
        if (!loggingEnabled()) {
            sb.setLength(0);
        }
    }

    public void replaceBuffer(CharSequence newBuffer) {
        sb.setLength(0);
        sb.append(newBuffer);
    }

    public void addRecord(String sourceId, String propertyKey, double value) {
        addSourceId(sourceId, propertyKey);
        sb.append(value);
    }

    public void addRecord(String sourceId, String propertyKey, long value) {
        addSourceId(sourceId, propertyKey);
        sb.append(value);
    }

    public void addRecord(String sourceId, String propertyKey, int value) {
        addSourceId(sourceId, propertyKey);
        sb.append(value);
    }

    public void addRecord(String sourceId, String propertyKey, char value) {
        addSourceId(sourceId, propertyKey);
        sb.append(value);
    }

    public void addRecord(String sourceId, String propertyKey, CharSequence value) {
        addSourceId(sourceId, propertyKey);
        sb.append(value);
    }

    public void addRecord(String sourceId, String propertyKey, Object value) {
        addSourceId(sourceId, propertyKey);
        sb.append(value == null ? "NULL" : value);
    }

    public void addRecord(String sourceId, String propertyKey, boolean value) {
        addSourceId(sourceId, propertyKey);
        sb.append(value);
    }

    public void addTrace(String sourceId) {
        if (this.sourceId != null) {
            sb.append("}");
        }
        firstProp = true;
        this.sourceId = null;
        addSourceId(sourceId, null);
    }

    public void printEventToString(boolean printEventToString) {
        this.printEventToString = printEventToString;
    }

    protected void addSourceId(String sourceId, String propertyKey) {
        if (loggingEnabled()) {
            if (this.sourceId == null) {
                sb.append("\n        - ").append(sourceId).append(": {");
                this.sourceId = sourceId;
            } else if (!this.sourceId.equals(sourceId)) {
                sb.append("}\n        - ").append(sourceId).append(": {");
                this.sourceId = sourceId;
                firstProp = true;
            }
            if (!firstProp) {
                sb.append(",");
            }
            if (propertyKey != null) {
                firstProp = false;
                sb.append(" ").append(propertyKey).append(": ");
            }
        }
    }

    public void clear() {
        firstProp = true;
        sourceId = null;
        sb.setLength(0);
    }

    public CharSequence asCharSequence() {
        return sb;
    }

    public void triggerEvent(Event event) {
        if (loggingEnabled()) {
            Class<? extends Event> aClass = event.getClass();
            sb.append("eventLogRecord: ");
            sb.append("\n    eventTime: ");
            timeFormatter.accept(sb, clock.getEventTime());

            sb.append("\n    logTime: ");
            timeFormatter.accept(sb, clock.getWallClockTime());

            sb.append("\n    groupingId: ").append(groupingId);
            sb.append("\n    event: ").append(aClass.getSimpleName());
            if (printEventToString) {
                sb.append("\n    eventToString: ").append(event.toString());
            }
            if (printThreadName) {
                sb.append("\n    thread: ").append(Thread.currentThread().getName());
            }
            if (event.filterString() != null && !event.filterString().isEmpty()) {
                sb.append("\n    eventFilter: ").append(event.filterString());
            }
            sb.append("\n    nodeLogs: ");
        }
    }

    public void triggerObject(Object event) {
        if (loggingEnabled()) {
            if (event instanceof Event) {
                triggerEvent((Event) event);
            } else {
                Class<?> aClass = event.getClass();
                sb.append("eventLogRecord: ");
                sb.append("\n    eventTime: ");
                timeFormatter.accept(sb, clock.getEventTime());

                sb.append("\n    logTime: ");
                timeFormatter.accept(sb, clock.getWallClockTime());

                sb.append("\n    groupingId: ").append(groupingId);

                sb.append("\n    event: ").append(aClass.getSimpleName());
                if (printEventToString) {
                    sb.append("\n    eventToString: ").append(event.toString());
                }
                sb.append("\n    nodeLogs: ");
            }
        }
    }

    /**
     * complete record processing, the return value indicates if any log values
     * were written.
     *
     * @return flag to indicate properties were logged
     */
    public boolean terminateRecord() {
        boolean logged = !firstProp;
        if (loggingEnabled()) {
            if (this.sourceId != null) {
                sb.append("}");
            }
            sb.append("\n    endTime: ").append(clock.getWallClockTime());
        }
        firstProp = true;
        sourceId = null;
        return logged;
    }

    @Override
    public String toString() {
        return asCharSequence().toString();
    }

    protected boolean loggingEnabled() {
        return logLevel != EventLogControlEvent.LogLevel.NONE;
    }
}
