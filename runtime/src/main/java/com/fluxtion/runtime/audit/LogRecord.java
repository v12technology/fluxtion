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

import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.time.Clock;

/**
 * A structured log record that can be easily converted to a long term store,
 * such as a rdbms for later analysis. The LogRecord creates a yaml
 * representation of the LogRecord for simplified marshaling.
 * <br>
 *
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
     *
     */
    public String groupingId;
    public long eventId;
    private final StringBuilder sb;
    private String sourceId;
    private boolean firstProp;
    private Clock clock;
    private boolean printEventToString = false;

    public LogRecord(Clock clock) {
        sb = new StringBuilder();
        firstProp = true;
        this.clock = clock;
    }

    public void addRecord(String sourceId, String propertyKey, double value) {
        addSourceId(sourceId, propertyKey);
        if (value % 1 == 0) {
            sb.append((int) value);
        } else {
            sb.append(value);
        }
    }

    public void addRecord(String sourceId, String propertyKey, char value) {
        addSourceId(sourceId, propertyKey);
        sb.append(value);
    }

    public void addRecord(String sourceId, String propertyKey, CharSequence value) {
        addSourceId(sourceId, propertyKey);
        sb.append(value);
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
    
    private void addSourceId(String sourceId, String propertyKey) {
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

    public void clear() {
        firstProp = true;
        sourceId = null;
        sb.setLength(0);
    }

    public CharSequence asCharSequence() {
        return sb;
    }

    public void triggerEvent(Event event) {
        Class<? extends Event> aClass = event.getClass();
        sb.append("eventLogRecord: ");
        sb.append("\n    eventTime: ").append(clock.getEventTime());
        sb.append("\n    logTime: ").append(clock.getWallClockTime());
        sb.append("\n    groupingId: ").append(groupingId);
        sb.append("\n    event: ").append(aClass.getSimpleName());
        if (printEventToString) {
            sb.append("\n    eventToString: {").append(event.toString()).append('}');
        }
        if (event.filterString() != null && !event.filterString().isEmpty()) {
            sb.append("\n    eventFilter: ").append(event.filterString());
        }
        sb.append("\n    nodeLogs: ");
    }

    public void triggerObject(Object event) {
        if (event instanceof Event) {
            triggerEvent((Event) event);
        } else {
            Class<?> aClass = event.getClass();
            sb.append("eventLogRecord: ");
            sb.append("\n    eventTime: ").append(clock.getEventTime());
            sb.append("\n    logTime: ").append(clock.getWallClockTime());
            sb.append("\n    groupingId: ").append(groupingId);
            sb.append("\n    event: ").append(aClass.getSimpleName());
            if (printEventToString) {
                sb.append("\n    eventToString: {").append(event.toString()).append('}');
            }
            sb.append("\n    nodeLogs: ");
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
        if (this.sourceId != null) {
            sb.append("}");
        }
        sb.append("\n    endTime: ").append(clock.getWallClockTime());
        firstProp = true;
        sourceId = null;
        return logged;
    }

    @Override
    public String toString() {
        return asCharSequence().toString();
    }

}
