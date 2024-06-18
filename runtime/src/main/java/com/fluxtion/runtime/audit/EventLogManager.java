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

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.EventLogControlEvent.LogLevel;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.node.ForkedTriggerTask;
import com.fluxtion.runtime.time.Clock;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ObjLongConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages and publishes a {@link LogRecord} to a {@link LogRecordListener}. The
 * LogRecord is hydrated from a list of {@link EventLogSource}'s. An
 * EventLogManager configures and supplies a EventLogger instance for each
 * registered EventLogSource, via
 * {@link EventLogSource#setLogger(EventLogger)} com.fluxtion.runtime.plugin.logging.EventLogger)}.
 * The output from each EventLogSource is aggregated into the LogRecord and
 * published.
 * <br>
 * <p>
 * By default all data in the LogRecord is cleared after a publish. Clearing
 * behaviour is controlled with clearAfterPublish flag.
 * <br>
 * <p>
 * EventLogControlEvent events set the logging level for each registered
 * EventLogSource.
 *
 * @author Greg Higgins (greg.higgins@v12technology.com)
 */
public class EventLogManager implements Auditor {

    public static final String NODE_NAME = "eventLogger";
    private LogRecordListener sink;
    private LogRecord logRecord;
    private Map<String, EventLogger> node2Logger;
    private Map<String, EventLogSource> name2LogSourceMap;
    private boolean clearAfterPublish;
    private static final Logger LOGGER = Logger.getLogger(EventLogManager.class.getName());
    public boolean trace = false;
    public boolean printEventToString = true;
    public boolean printThreadName = true;
    public EventLogControlEvent.LogLevel traceLevel;
    @Inject
    public Clock clock;
    private boolean canTrace = false;


    public EventLogManager() {
        this(new JULLogRecordListener());
    }

    public EventLogManager(LogRecordListener sink) {
        if (sink == null) {
            this.sink = l -> {
            };
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
        trace = level != LogLevel.NONE;
        this.traceLevel = level;
        return this;
    }

    public EventLogManager printEventToString(boolean printEventToString) {
        this.printEventToString = printEventToString;
        return this;
    }

    public EventLogManager printThreadName(boolean printThreadName) {
        this.printThreadName = printThreadName;
        return this;
    }

    @Override
    public void nodeRegistered(Object node, String nodeName) {
        EventLogger logger = new EventLogger(logRecord, nodeName);
        if (node instanceof EventLogSource) {
            EventLogSource calcSource = (EventLogSource) node;
            calcSource.setLogger(logger);
            name2LogSourceMap.put(nodeName, calcSource);
        }
        node2Logger.put(nodeName, logger);
        canTrace = trace && node2Logger.values().stream().filter(e -> e.canLog(traceLevel)).findAny().isPresent();
    }

    private void updateLogRecord() {
        for (Map.Entry<String, EventLogSource> stringEventLogSourceEntry : name2LogSourceMap.entrySet()) {
            String nodeName = stringEventLogSourceEntry.getKey();
            EventLogSource calcSource = stringEventLogSourceEntry.getValue();
            EventLogger logger = new EventLogger(logRecord, nodeName);
            calcSource.setLogger(logger);
            name2LogSourceMap.put(nodeName, calcSource);
            node2Logger.put(nodeName, logger);
        }
    }

    @Override
    public boolean auditInvocations() {
        return trace;
    }

    @Override
    public void nodeInvoked(Object node, String nodeName, String methodName, Object event) {
        EventLogger logger = node2Logger.getOrDefault(nodeName, NullEventLogger.INSTANCE);
        logger.logNodeInvocation(traceLevel);
        if (printThreadName) {
            logger.log("thread", Thread.currentThread().getName(), traceLevel);
        }
        if (node instanceof ForkedTriggerTask) {
            logger.log("forkedExecution", "true", traceLevel);
            logger.log("asyncMethod", methodName, traceLevel);
        } else {
            logger.log("method", methodName, traceLevel);
        }
    }

    @OnEventHandler(propagate = false)
    public void calculationLogConfig(EventLogControlEvent newConfig) {
        if (newConfig.getLogRecordProcessor() != null) {
            this.sink = newConfig.getLogRecordProcessor();
        }

        LogRecord newLogRecord = newConfig.getLogRecord();
        if (newLogRecord != null) {
            newLogRecord.updateLogLevel(logRecord.getLogLevel());
            newLogRecord.replaceBuffer(logRecord.sb);
            this.logRecord = newLogRecord;
            this.logRecord.setClock(clock);
            updateLogRecord();
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

        final ObjLongConsumer<StringBuilder> timeFormatter = newConfig.getTimeFormatter();
        if (timeFormatter != null) {
            logRecord.setTimeFormatter(timeFormatter);
        }

        canTrace = trace && node2Logger.values().stream().filter(e -> e.canLog(traceLevel)).findAny().isPresent();
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

    /**
     * makes best efforts to dump the current {@link LogRecord} to the registered sink. Useful when error handling
     * if an exception is thrown
     */
    public void publishLastRecord() {
        logRecord.terminateRecord();
        sink.processLogRecord(logRecord);
        logRecord.clear();
    }

    /**
     * makes best efforts to dump the current {@link LogRecord} to as a String. Useful when error handling
     * if an exception is thrown
     *
     * @return The lates {@link LogRecord} as a String
     */
    public String lastRecordAsString() {
        return logRecord.toString();
    }

    @Override
    public void processingComplete() {
        if (canTrace | logRecord.terminateRecord()) {
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
        logRecord.setPrintThreadName(printThreadName);
        node2Logger = new HashMap<>();
        name2LogSourceMap = new HashMap<>();
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
