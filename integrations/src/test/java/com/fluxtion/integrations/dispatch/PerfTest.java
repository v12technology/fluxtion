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

import com.fluxtion.integration.eventflow.EventFlow;
import com.fluxtion.integration.eventflow.PipelineFilter;
import com.fluxtion.integration.eventflow.sources.DelimitedPullSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.LongAdder;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class PerfTest {

//    @Test
    public void testPipedReader() throws FileNotFoundException, IOException, InterruptedException {
        PipedReader reader = new PipedReader();
        PipedWriter writer = new PipedWriter(reader);
        LongAdder count = new LongAdder();
        EventFlow.PipelineBuilder builder = new EventFlow()
                .source(new DelimitedPullSource(reader, new DataEventCsvDecoder0(), "data-1"))
                .pipeline(new PipelineFilter() {
                    @Override
                    public void processEvent(Object o) {
//                        log.info("received:{}", o);
                        count.increment();
                    }
                });
        EventFlow flow = builder.start();
//                .start();
        Thread.sleep(100);
        String part1 = "id,name\n"
                + "1,greg\n"
                + "2,jo";

        String part2 = "sie\n";
        writer.write(part1);
        writer.flush();
        writer.write(part2);
        writer.flush();

        Thread.sleep(1);
        log.info("starting writing");
        for (int i = 0; i < 100_000; i++) {
            writer.write(i + ",freddie\n");
            log.info("writing id:{}", i);
//            writer.flush();
//            LockSupport.parkNanos(1_000);
//            Thread.yield();
//            Thread.sleep(1);

        }
        log.info("finished writing");
//        Thread.sleep(10);
        flow.stop();
        log.info("stopped reader");
        System.out.println("processed:" + count.intValue());
    }
}
