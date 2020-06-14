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
import com.fluxtion.ext.text.api.event.CharEvent;
import com.fluxtion.ext.text.api.event.RegisterEventHandler;
import com.fluxtion.ext.text.api.util.marshaller.CsvRecordMarshaller;
import com.fluxtion.integration.eventflow.EventConsumer;
import com.fluxtion.integration.eventflow.EventQueueSource;
import java.io.IOException;
import java.io.Reader;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class DelimitedPullSource implements EventQueueSource {

    private final CsvRecordMarshaller marshaller;
    private final String id;
    private char[] readBuffer;
    private final CharEvent charEvent;
    private final Reader reader;

    public DelimitedPullSource(Reader reader, RowProcessor processor, String id) {
        this.marshaller = new CsvRecordMarshaller(processor);
        this.id = id;
        this.reader = reader;
        readBuffer = new char[4096];
        charEvent = new CharEvent(' ');
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init() {
        log.info("init DelimitedPullSource id:'{}'", id);
        marshaller.init();
    }

    @Override
    public void start(EventConsumer target) {
        log.info("start DelimitedPullSource id:'{}'", id);
        marshaller.handleEvent(new RegisterEventHandler(target::processEvent));
    }

    @Override
    public void poll() {
        try {
            //log.debug("poll DelimitedPullSource id:'{}'", id);
            if (reader.ready()) {
                int readSize = reader.read(readBuffer);
                for (int i = 0; i < readSize; i++) {
                    charEvent.setCharacter(readBuffer[i]);
                    marshaller.handleEvent(charEvent);
                }
            }
        } catch (IOException ex) {
            log.error("problem reading from stream id:'{}'", id, ex);
        }
    }

    @Override
    public void tearDown() {
        log.info("tearDown DelimitedPullSource id:'{}'", id);
        marshaller.tearDown();
    }

}
