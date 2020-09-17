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
package com.fluxtion.integration.eventflow.sources;

import com.fluxtion.ext.streaming.api.log.LogControlEvent;
import com.fluxtion.ext.streaming.api.log.WriterLogProvider;
import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.ext.text.api.event.RegisterEventHandler;
import com.fluxtion.ext.text.api.util.CharStreamer;
import com.fluxtion.ext.text.api.util.marshaller.CsvRecordMarshaller;
import com.fluxtion.integration.eventflow.EventConsumer;
import com.fluxtion.integration.eventflow.EventSource;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class DelimitedSource<T> implements EventSource<T> {

    private final CsvRecordMarshaller marshaller;
    private Writer errorLog;
    private final String id;
    private final CharStreamer streamer;

    public DelimitedSource(RowProcessor<T> processor, File inputFile, String id) {
        this.id = id;
        this.marshaller = new CsvRecordMarshaller(processor);
        this.streamer = CharStreamer.stream(inputFile, marshaller);
    }

    public DelimitedSource(RowProcessor<T> processor, Reader reader, String id) {
        this.id = id;
        this.marshaller = new CsvRecordMarshaller(processor);
        this.streamer = CharStreamer.stream(reader, marshaller);
    }

    public DelimitedSource(RowProcessor<T> processor, Reader reader, Writer errorLog, String id) {
        this.errorLog = errorLog;
        this.id = id;
        this.marshaller = new CsvRecordMarshaller(processor);
        this.streamer = CharStreamer.stream(reader, marshaller);
    }

    public DelimitedSource<T> pollForever() {
        streamer.pollForever();
        return this;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init() {
        marshaller.init();
    }

    @Override
    public void start(EventConsumer<T> target) {
        try {
            if (errorLog != null) {
                marshaller.handleEvent(LogControlEvent.setLogService(new WriterLogProvider(errorLog).logPrefix(false)));
            }
            marshaller.handleEvent(new RegisterEventHandler(o -> target.processEvent((T) o)));
            streamer.sync().noInit().stream();
        } catch (IOException ex) {
            log.error("problem streaming file", ex);
        }
    }

    @Override
    public void tearDown() {
        try {
            streamer.shutDown();
        } catch (InterruptedException ex) {
            log.info("problem stopping event source input", ex);
        }
        marshaller.tearDown();
    }

}
