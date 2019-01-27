/*
 * Copyright (C) 2019 V12 Technology Ltd.
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
package com.fluxtion.ext.futext.api.csv;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.declarative.api.log.LogControlEvent;
import com.fluxtion.ext.declarative.api.log.LogService;
import com.fluxtion.ext.declarative.api.log.ConsoleLogProvider;
import com.fluxtion.ext.declarative.api.util.Named;

/**
 * A sink for validation logs, publishing to the configured end point.</p>
 *
 * The LogProvider can be controlled by sending a log control {@link LogControlEvent#setLogService(LogService)
 * } event to the SEP managing the validation.
 *
 * @author V12 Technology Ltd.
 */
public class ValidationLogSink extends Named {

    private StringBuilder appendLog;
    private StringBuilder prependLog;
    private LogService logService;
    private boolean publishLogImmediately = true;

    public ValidationLogSink() {
        super("validationLogSink");
    }

    public ValidationLogSink(String name) {
        super(name);
    }

    public void addpendLog(ValidationLogger log) {
        appendLog.append(log.getSb());
        if (publishLogImmediately) {
            publishLog();
        }
    }

    public void prependLog(CharSequence log) {
        prependLog.append(log);
    }

    @EventHandler(filterString = LogControlEvent.PROVIDER)
    public void controlLogProvider(LogControlEvent control) {
        logService = control.getLogService();
    }

    /**
     * Immediately publishes to the log end point
     *
     * @param log
     */
    public void errorLog(ValidationLogger log) {
        logService.error(log.getSb());
    }

//    @OnEvent
    public void publishLog() {
        logService.info(prependLog.append(appendLog));
        prependLog.setLength(0);
        appendLog.setLength(0);
    }

    @Initialise
    public void init() {
        appendLog = new StringBuilder(512);
        prependLog = new StringBuilder(512);
        logService = new ConsoleLogProvider();
    }

    public boolean isPublishLogImmediately() {
        return publishLogImmediately;
    }

    public void setPublishLogImmediately(boolean publishLogImmediately) {
        this.publishLogImmediately = publishLogImmediately;
    }

    public static class LogNotifier {

        @Inject
        @NoEventReference
        public ValidationLogSink sink;
        private final Object logNotifier;
        @NoEventReference
        private final RowProcessor rowProcessor;

        public LogNotifier(Object logNotifier, RowProcessor rowProcessor) {
            this.logNotifier = logNotifier;
            this.rowProcessor = rowProcessor;
        }

        public LogNotifier(Object logNotifier) {
            this(logNotifier, null);
        }

        @OnEvent
        public void notifyLogSink() {
            if (rowProcessor != null) {
                sink.prependLog("row:" + rowProcessor.getRowNumber());
            }
            sink.publishLog();
        }
        
        @Initialise
        public void init(){
            sink.setPublishLogImmediately(false);
        }

    }

}
