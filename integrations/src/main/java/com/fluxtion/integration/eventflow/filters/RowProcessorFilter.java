/*
 * Copyright (c) 2020, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.integration.eventflow.filters;

import com.fluxtion.ext.streaming.api.log.LogControlEvent;
import com.fluxtion.ext.streaming.api.log.LogService;
import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.ext.text.api.event.CharEvent;
import com.fluxtion.ext.text.api.event.RegisterEventHandler;
import com.fluxtion.ext.text.api.util.marshaller.CharProcessor;
import com.fluxtion.ext.text.api.util.marshaller.CsvRecordMarshaller;
import com.fluxtion.integration.eventflow.PipelineFilter;
import lombok.extern.log4j.Log4j2;

/**
 * Integrates a {@link RowProcessor} into an {@link Pipeline}
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class RowProcessorFilter extends PipelineFilter implements CharProcessor {

    private final CsvRecordMarshaller marshaller;
    private RowProcessor target;
    private LogService validationLogger;

    public RowProcessorFilter(RowProcessor target) {
        this.target = target;
        marshaller = new CsvRecordMarshaller(target);
    }

    public RowProcessorFilter validationLogger(LogService validationLogger) {
        this.validationLogger = validationLogger;
        marshaller.handleEvent(LogControlEvent.setLogService(validationLogger));
        return this;
    }

    public static RowProcessorFilter of(Class<? extends RowProcessor> processorClass) {
        try {
            RowProcessor processor = processorClass.getDeclaredConstructor().newInstance();
            RowProcessorFilter instance = new RowProcessorFilter(processor);
            return instance;
        } catch (Exception ex) {
            throw new RuntimeException("cannot build RowProcessorHandler pipeline node ", ex);
        }
    }

    @Override
    public void handleEvent(CharEvent charEvent) {
        marshaller.handleEvent(charEvent);
    }

    @Override
    public void processEvent(Object o) {
        marshaller.onEvent(o);
    }

    @Override
    protected void initHandler() {
        log.info("init filter:{}", getClass().getSimpleName());
        marshaller.init();
        if (validationLogger != null) {
            marshaller.handleEvent(LogControlEvent.setLogService(validationLogger));
        }
        if (nextHandler != null) {
            marshaller.handleEvent(new RegisterEventHandler(nextHandler::processEvent));
        }
    }

    @Override
    protected void stopHandler() {
        log.info("stop filter:{}", getClass().getSimpleName());
        marshaller.tearDown();
    }

}
