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
package com.fluxtion.api.audit;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.event.Event;
import com.fluxtion.api.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages and publishes a {@link LogRecord} to a {@link LogRecordListener}. The
 * LogRecord is hydrated from a list of {@link EventLogSource}'s. An
 * EventLogManager configures and supplies a EventLogger instance for each
 * registered EventLogSource, via
 * {@link EventLogSource#setLogger(com.fluxtion.runtime.plugin.logging.EventLogger)}.
 * The output from each EventLogSource is aggregated into the LogRecord and
 * published.
 * <br>
 *
 * By default all data in the LogRecord is cleared after a publish. Clearing
 * behaviour is controlled with clearAfterPublish flag.
 * <br>
 *
 * EventLogControlEvent events set the logging level for each registered
 * EventLogSource.
 *
 * @author Greg Higgins (greg.higgins@v12technology.com)
 */
public class EventLogManager implements Auditor {

    private LogRecordListener sink;
    private LogRecord logRecord;
    private Map<String, EventLogger> node2Logger;
    private boolean clearAfterPublish;
    private static final Logger LOGGER = Logger.getLogger(EventLogManager.class.getName());
    public boolean trace = false;
    public boolean printEventToString = true;
    public EventLogControlEvent.LogLevel traceLevel;
    @Inject
    public Clock clock;
    

    public EventLogManager() {
        this(new JULLogRecordListener());
    }

    public EventLogManager(LogRecordListener sink) {
        if (sink == null) {
            this.sink = l -> {};
        } else {
            this.sink = sink;
        }
    }

    public EventLogManager tracingOff() {
        trace = false;
        this.traceLevel = EventLogControlEvent.LogLevel.NONE;
        return this;
    }

    public EventLogManager tracingOn(EventLogControlEvent.LogLevel level) {
        trace = true;
        this.traceLevel = level;
        return this;
    }
    
    public EventLogManager printEventToString(boolean printEventToString){
        this.printEventToString = printEventToString;
        return this;
    }

    @Override
    public void nodeRegistered(Object node, String nodeName) {
        EventLogger logger = new EventLogger(logRecord, nodeName);
        if (node instanceof EventLogSource) {
            EventLogSource calcSource = (EventLogSource) node;
            calcSource.setLogger(logger);
        }
        node2Logger.put(nodeName, logger);
    }

    @Override
    public boolean auditInvocations() {
        return trace;
    }

    @Override
    public void nodeInvoked(Object node, String nodeName, String methodName, Object event) {
        EventLogger logger = node2Logger.getOrDefault(nodeName, NullEventLogger.INSTANCE);
        logger.logNodeInvoation(traceLevel);
        logger.log("method", methodName, traceLevel);
    }

    @EventHandler(propagate = false)
    public void calculationLogConfig(EventLogControlEvent newConfig) {
        if (newConfig.getLogRecordProcessor() != null) {
            this.sink = newConfig.getLogRecordProcessor();
        }
        final EventLogControlEvent.LogLevel level = newConfig.getLevel();
        if (level != null
                && (logRecord.groupingId == null || logRecord.groupingId.equals(newConfig.getGroupId()))) {
            LOGGER.log(Level.INFO, "updating event log config:{0}", newConfig);
            node2Logger.computeIfPresent(newConfig.getSourceId(), (t, u) -> {
                u.setLevel(level);
                return u;
            });
            if (newConfig.getSourceId() == null) {
                node2Logger.values().forEach((t) -> t.setLevel(newConfig.getLevel()));
            }
        }
    }

    public void setLogSink(LogRecordListener sink) {
        this.sink = sink;
    }

    public void setLogGroupId(String groupId) {
        logRecord.groupingId = groupId;
    }

    public void setClearAfterPublish(boolean clearAfterPublish) {
        this.clearAfterPublish = clearAfterPublish;
    }

    @Override
    public void processingComplete() {
        if (trace | logRecord.terminateRecord()) {
            sink.processLogRecord(logRecord);
        }
        if (clearAfterPublish) {
            logRecord.clear();
        }
    }

    @Override
    public void init() {
        logRecord = new LogRecord(clock);
        logRecord.printEventToString(printEventToString);
        node2Logger = new HashMap<>();
        clearAfterPublish = true;
    }

    @Override
    public void eventReceived(Event triggerEvent) {
        logRecord.triggerEvent(triggerEvent);
    }

    @Override
    public void eventReceived(Object triggerEvent) {
        logRecord.triggerObject(triggerEvent);
    }

}
