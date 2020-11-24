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
package com.fluxtion.integration.eventflow.sinks;

import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.integration.eventflow.MarshallerRegistry;
import com.fluxtion.integration.eventflow.EventSink;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;

/**
 * Journals events as CSV records using log4j2. Acts as a pipelinefilter:
 *
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class CsvSink implements EventSink {

    StringBuilder sb = new StringBuilder();
    private EventSink sink;
    private boolean firstPublish;
    private final MarshallerRegistry marshallerRegistry;

    public CsvSink(MarshallerRegistry marshallerRegistry) {
        this.marshallerRegistry = marshallerRegistry;
    }

    public CsvSink(EventSink sink, MarshallerRegistry marshallerRegistry) {
        this.sink = sink;
        this.marshallerRegistry = marshallerRegistry;
    }

    @Override
    public void publish(Object o) {
        final Class<? extends Object> clazz = o.getClass();
        RowProcessor processor = marshallerRegistry.getRowProcessor(o);
        sb.setLength(0);
        if (processor != null) {
            try {
                processor.toCsv(o, sb);
                if (sink != null) {
                    if (firstPublish) {
                        sink.publish(processor.csvHeaders());
                    }
                    sink.publish(sb);
                } else {
                    if (firstPublish) {
                        System.out.println(processor.csvHeaders());
                    }
                    System.out.print(sb);
                }
                firstPublish = false;
            } catch (IOException ex) {
                log.warn("could not marshall instabce to csv:", o);
            }
        } else {
            log.debug("no marshaller registred for:", clazz);
        }
    }

    @Override
    public void init() {
        firstPublish = true;
    }

}
