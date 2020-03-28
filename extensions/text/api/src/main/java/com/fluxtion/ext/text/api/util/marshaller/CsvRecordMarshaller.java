/*
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.ext.text.api.util.marshaller;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.ext.streaming.api.log.LogControlEvent;
import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.ext.text.api.csv.ValidationLogSink;
import com.fluxtion.ext.text.api.csv.ValidationLogger;
import com.fluxtion.ext.text.api.event.CharEvent;
import com.fluxtion.ext.text.api.event.EofEvent;
import com.fluxtion.ext.text.api.event.RegisterEventHandler;
import com.fluxtion.ext.text.api.util.EventPublsher;

/*
 * <pre>
 * generation time   : 2020-03-18T22:25:08.818697100
 * generator version : 1.9.6
 * api version       : 1.9.6
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class CsvRecordMarshaller implements StaticEventProcessor, Lifecycle {

    //Node declarations
    private final RowProcessor rowProcessor;
    private final EventPublsher eventPublsher = new EventPublsher();
    private final ValidationLogger validationLogger = new ValidationLogger("validationLog");
    private final ValidationLogSink validationLogSink = new ValidationLogSink("validationLogSink");
    //Dirty flags
    private boolean isDirty_RowProcessor = false;

    public CsvRecordMarshaller(RowProcessor rowProcessor) {
        this.rowProcessor = rowProcessor;
        this.rowProcessor.setErrorLog(validationLogger);
        validationLogSink.setPublishLogImmediately(true);
        validationLogger.logSink = validationLogSink;
        eventPublsher.publishOnValidate = (boolean) false;
    }

    @Override
    public void onEvent(Object event) {
        switch (event.getClass().getName()) {
            case ("com.fluxtion.ext.streaming.api.log.LogControlEvent"): {
                LogControlEvent typedEvent = (LogControlEvent) event;
                handleEvent(typedEvent);
                break;
            }
            case ("com.fluxtion.ext.text.api.event.CharEvent"): {
                CharEvent typedEvent = (CharEvent) event;
                handleEvent(typedEvent);
                break;
            }
            case ("com.fluxtion.ext.text.api.event.EofEvent"): {
                EofEvent typedEvent = (EofEvent) event;
                handleEvent(typedEvent);
                break;
            }
            case ("com.fluxtion.ext.text.api.event.RegisterEventHandler"): {
                RegisterEventHandler typedEvent = (RegisterEventHandler) event;
                handleEvent(typedEvent);
                break;
            }
        }
    }

    public void handleEvent(LogControlEvent typedEvent) {
        switch (typedEvent.filterString()) {
            //Event Class:[com.fluxtion.ext.streaming.api.log.LogControlEvent] filterString:[CHANGE_LOG_PROVIDER]
            case ("CHANGE_LOG_PROVIDER"):
                validationLogSink.controlLogProvider(typedEvent);
                afterEvent();
                return;
        }
        afterEvent();
    }

    public void handleEvent(CharEvent typedEvent) {
        //Default, no filter methods
        isDirty_RowProcessor = rowProcessor.charEvent(typedEvent);
        if (isDirty_RowProcessor) {
            eventPublsher.wrapperUpdate(rowProcessor);
        }
        //event stack unwind callbacks
        afterEvent();
    }

    public void handleEvent(EofEvent typedEvent) {
        //Default, no filter methods
        isDirty_RowProcessor = rowProcessor.eof(typedEvent);
        if (isDirty_RowProcessor) {
            eventPublsher.wrapperUpdate(rowProcessor);
        }
        //event stack unwind callbacks
        afterEvent();
    }

    public void handleEvent(RegisterEventHandler typedEvent) {
        //Default, no filter methods
        eventPublsher.registerEventHandler(typedEvent);
        //event stack unwind callbacks
        afterEvent();
    }

    private void afterEvent() {
        isDirty_RowProcessor = false;
    }

    @Override
    public void init() {
        rowProcessor.init();
        eventPublsher.init();
        validationLogSink.init();
    }

    @Override
    public void tearDown() {
    }

}
