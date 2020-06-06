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
package com.fluxtion.integrations.dispatch;

import com.fluxtion.integration.eventflow.EventConsumer;
import com.fluxtion.integration.eventflow.EventFlow;
import com.fluxtion.integration.eventflow.EventSource;
import com.fluxtion.integration.eventflow.filters.ConsoleFilter;
import com.fluxtion.integration.eventflow.sources.DelimitedSource;
import java.io.File;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class EventFlowManagerTest {

    @Test
    public void testSimple() {
        new EventFlow()
                .source(new DelimitedSource(new DataEventCsvDecoder0(), new File("src/test/data/data1.csv"), "data-1"))
//                .source(new EventSourceImpl("src-1"))
//                .source(new EventSourceImpl("src-2"))
                .pipeline(new ConsoleFilter())
                .start();
    }

    private static class EventSourceImpl implements EventSource {

        private final String id;

        public EventSourceImpl(String id) {
            this.id = id;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public void init() {
            log.info("init id:{}", id);
        }

        @Override
        public void start(EventConsumer target) {
            log.info("setConsumer id:{}", id);
            target.processEvent("hello from - " + id);
        }

        @Override
        public void tearDown() {
            log.info("tearDown id:{}", id);
        }
    }
}
