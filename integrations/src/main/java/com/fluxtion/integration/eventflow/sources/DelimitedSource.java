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

import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.ext.text.api.event.RegisterEventHandler;
import com.fluxtion.ext.text.api.util.CharStreamer;
import com.fluxtion.ext.text.api.util.marshaller.CsvRecordMarshaller;
import com.fluxtion.integration.eventflow.EventConsumer;
import com.fluxtion.integration.eventflow.EventSource;
import java.io.File;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class DelimitedSource implements EventSource {

    private final CsvRecordMarshaller marshaller;
    private final String id;
    private final CharStreamer streamer;

    public DelimitedSource(RowProcessor processor, File inputFile, String id) {
        this.id = id;
        this.marshaller = new CsvRecordMarshaller(processor);
        this.streamer = CharStreamer.stream(inputFile, marshaller);
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
    public void start(EventConsumer target) {
        try {
            marshaller.handleEvent(new RegisterEventHandler(target::processEvent));
            streamer.sync().stream();
        } catch (IOException ex) {
            log.error("problem streaming file", ex);
        }
    }

    @Override
    public void tearDown() {
        marshaller.tearDown();
    }

}
