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

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * Parses log records from a map structure as generated by {@link LogRecord},
 * and provides convenient accessors in a standard JavaBean pattern.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class StructuredLogRecord {

    private Map map;
    private List<AuditRecord> auditList;
    private List<String> nodeNameList;

    public StructuredLogRecord(Map map) {
        setMap(map);
    }

    public final void setMap(Map map) {
        this.map = map;
        auditList = null;
        nodeNameList = null;
    }

    public long getLogTime() {
        return ((Number) map.get("logTime")).longValue();
    }

    public long getEventTime() {
        return ((Number) map.get("eventTime")).longValue();
    }

    public long getEndTime() {
        return ((Number) map.get("eventTime")).longValue();
    }

    public String getGroupingId() {
        return (String) map.get("groupingId");
    }

    public String getEventType() {
        return (String) map.get("event");
    }

    public List<Map> nodeLogs() {
        return (List<Map>) map.get("nodeLogs");
    }

    public List<AuditRecord> getAuditLogs() {
        if (auditList == null) {
            LongAdder accumulator = new LongAdder();
            auditList = ((List<Map>) map.get("nodeLogs")).stream().map(m -> {
                Map.Entry entry = (Map.Entry) m.entrySet().iterator().next();
                final AuditRecord auditRecord = new AuditRecord(entry, accumulator.intValue());
                accumulator.increment();
                return auditRecord;
            }).collect(Collectors.toList());
        }
        return auditList;
    }

    public List<String> nodeNames() {
        if (nodeNameList == null) {
            nodeNameList = (List<String>) nodeLogs()
                    .stream()
                    .flatMap(f -> f.keySet().stream())
                    .map(k -> (String) k)
                    .distinct()
                    .collect(Collectors.toList());
        }
        return nodeNameList;
    }

    public int logCount() {
        return nodeLogs().size();
    }

    @Override
    public String toString() {
        return "EventLog{"
                + "logTime=" + getLogTime()
                + ", groupingId=" + getGroupingId()
                + ", event=" + getEventType()
                + ", logCount=" + logCount()
                + ", auditLogs=" + getAuditLogs()
                + ", nodeLogs=" + nodeLogs()
                + ", nodeNames=" + nodeNames()
                + '}';
    }

    public static class AuditRecord {

        private final String nodeId;
        private final Map propertyMap;
        private final int sequenceNumber;

        public AuditRecord(String nodeId, Map propertyMap, int sequenceNumber) {
            this.nodeId = nodeId;
            this.propertyMap = propertyMap;
            this.sequenceNumber = sequenceNumber;
        }

        public AuditRecord(Map.Entry entry, int sequenceNumber) {
            this.nodeId = (String) entry.getKey();
            this.propertyMap = (Map) entry.getValue();
            this.sequenceNumber = sequenceNumber;
        }

        public String getNodeId() {
            return nodeId;
        }

        public Map getPropertyMap() {
            return propertyMap;
        }

        public int getSequenceNumber() {
            return sequenceNumber;
        }

        @Override
        public String toString() {
            return "AuditRecord{"
                    + "nodeId=" + nodeId
                    + ", sequenceNumber=" + sequenceNumber
                    + ", propertyMap=" + propertyMap
                    + '}';
        }

    }

}
