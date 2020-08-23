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
import com.fluxtion.integration.eventflow.PipelineFilter;
import com.fluxtion.integration.eventflow.filters.ConsoleFilter;
import com.fluxtion.integration.eventflow.filters.Log4j2Filter;
import com.fluxtion.integration.eventflow.sources.DelimitedPullSource;
import com.fluxtion.integration.eventflow.sources.DelimitedSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.PipedReader;
//import java.io.PipedWriter;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.extern.log4j.Log4j2;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class EventFlowManagerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

//    @Test
    public void testSimple() {
        new EventFlow()
                .source(new DelimitedSource(new DataEventCsvDecoder0(), new File("src/test/data/data1.csv"), "data-1"))
                //                .source(new EventSourceImpl("src-1"))
                //                .source(new EventSourceImpl("src-2"))
                .first(new ConsoleFilter())
                .start();
    }

//    @Test
    public void testReader() throws FileNotFoundException, IOException, InterruptedException {
        File testFile = folder.newFile("EventFlowManagerTest_testReader.csv");
        EventFlow flow = new EventFlow()
                .source(new DelimitedPullSource(new DataEventCsvDecoder0(), new FileReader(testFile), "data-1"))
                .first(new Log4j2Filter())
                .start();
//        Thread.sleep(100);
        String part1 = "id,name\n"
                + "1,greg\n"
                + "2,jo";

        String part2 = "sie\n";
        FileWriter writer = new FileWriter(testFile);
        writer.write(part1);
        writer.flush();
        writer.write(part2);
        writer.flush();

        Thread.sleep(1);
        for (int i = 0; i < 10; i++) {
            writer.write(i + ",freddie\n");
            writer.flush();
            Thread.sleep(1);

        }
        flow.stop();
    }

//    @Test
    public void testPipedReader() throws FileNotFoundException, IOException, InterruptedException {
        PipedReader reader = new PipedReader();
        PipedWriter writer = new PipedWriter(reader);
        LongAdder count = new LongAdder();
        EventFlow.PipelineBuilder builder = new EventFlow()
                .source(new DelimitedPullSource(new DataEventCsvDecoder0(), reader, "data-1"))
                .first(new PipelineFilter() {
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
        for (int i = 0; i < 100; i++) {
            writer.write(i + ",freddie\n");
//            log.info("writing id:{}", i);
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

//    @Test
    public void testPipeeComms() throws IOException, InterruptedException {
        PipedReader reader = new PipedReader();
        PipedWriter writer = new PipedWriter(reader);
        LongAdder count = new LongAdder();

        Thread writerThread = new Thread(() -> {
            try {
                for (int i = 0; i < 100_000; i++) {
                    writer.write(i + ",freddie\n");
//            log.info("writing");
//            writer.flush();
//            Thread.yield();
//            Thread.sleep(1);

                }
                writer.close();
                System.out.println("closing writer");
            } catch (IOException ex) {
                Logger.getLogger(EventFlowManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        Thread readerThread = new Thread(() -> {
            try {
                int i = 0;
                while (i > -1) {
                    i = reader.read();
                    count.increment();
                }
                System.out.println("exiting reader");
            } catch (IOException ex) {
                Logger.getLogger(EventFlowManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        long now = System.currentTimeMillis();
        readerThread.start();
        writerThread.start();        
        readerThread.join(10_000);
        writerThread.join(10_000);
        long end = System.currentTimeMillis();
        System.out.println("Time:" + (end-now));
        System.out.println("count:" + count.intValue());
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
