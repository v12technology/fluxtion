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
import com.fluxtion.integration.eventflow.EventFlow.PipelineBuilder;
import static com.fluxtion.integration.eventflow.EventFlow.flow;
import com.fluxtion.integration.eventflow.EventSource;
import com.fluxtion.integration.eventflow.PipelineFilter;
import com.fluxtion.integration.eventflow.filters.ConsoleFilter;
import com.fluxtion.integration.eventflow.filters.Log4j2Filter;
import com.fluxtion.integration.eventflow.sources.DelimitedPullSource;
import com.fluxtion.integration.eventflow.sources.DelimitedSource;
import com.fluxtion.integration.eventflow.sources.ManualEventSource;
import com.fluxtion.integration.eventflow.sources.TransformPullSource;
import com.fluxtion.integration.eventflow.sources.TransformSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
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

    @Test
    public void testStartStopSources() {
        ArrayList started = new ArrayList();
        List<String> audit = (List<String>) started;
//        val src1 = new EventSourceImpl("src1");
        PipelineBuilder flow = flow(new EventSourceImpl("src1"))
                .source(new EventSourceImpl("src2"))
                .peek(started::add);
        assertTrue(started.isEmpty());
        flow.start();
        assertThat(audit, contains("src1", "src2"));
        assertThat(audit, hasSize(2));
        flow.stop();
        assertThat(audit, hasSize(4));
        assertThat(audit, contains("src1", "src2", "src1", "src2"));
    }

    @Test
    public void transformPushSource() {
        ManualEventSource injector = new ManualEventSource("manSrc1");
        ArrayList<String> audit = new ArrayList();
        EventFlow flow = flow(TransformSource.transform(injector, i -> "transform"))
                .first(new TestFilter("f1", audit))
                .start();
        audit.clear();
        injector.publishToFlow(1);
        assertThat(audit, contains("f1", "transform"));
        audit.clear();
        injector.publishToFlow("e1");
        assertThat(audit, contains("f1", "transform"));
        flow.stop();
    }

    @Test
    public void testPipeline() {
        ManualEventSource injector = new ManualEventSource("manSrc1");
        ArrayList<String> audit = new ArrayList();
        EventFlow flow = flow(injector)
                .first(new TestFilter("f1", audit))
                .next(new TestFilter("f2", audit))
                .start();
        assertThat(audit, contains("f2", "f1", "f2", "f1"));
        //send event
        audit.clear();
        injector.publishToFlow("e1");
        assertThat(audit, contains("f1", "e1", "f2", "e1"));
        //stop
        audit.clear();
        flow.stop();
        assertThat(audit, contains("f1", "f2"));
    }

    @Test
    public void filterPipeline() {
        ManualEventSource injector = new ManualEventSource("manSrc1");
        ArrayList<String> audit = new ArrayList();
        EventFlow flow = flow(injector)
                .first(new TestFilter("f1", audit))
                .filter(String.class::isInstance)
                .next(new TestFilter("f2", audit))
                .start();
        audit.clear();
        injector.publishToFlow(1);
        assertThat(audit, contains("f1", "1"));
        audit.clear();
        injector.publishToFlow("e1");
        assertThat(audit, contains("f1", "e1", "f2", "e1"));
        flow.stop();
    }

    @Test
    public void mapPipeline() {
        ManualEventSource injector = new ManualEventSource("manSrc1");
        ArrayList<String> audit = new ArrayList();
        EventFlow flow = flow(injector)
                .first(new TestFilter("f1", audit))
                .map(i -> "transformed")
                .next(new TestFilter("f2", audit))
                .start();
        audit.clear();
        injector.publishToFlow(1);
        assertThat(audit, contains("f1", "1", "f2", "transformed"));
        audit.clear();
        injector.publishToFlow("e1");
        assertThat(audit, contains("f1", "e1", "f2", "transformed"));
        flow.stop();
    }

    @Test
    public void testAsyncPushReader() throws FileNotFoundException, IOException, InterruptedException {
        PipedReader reader = new PipedReader();
        PipedWriter writer = new PipedWriter(reader);
        ArrayList audit = new ArrayList();
        CountDownLatch latch = new CountDownLatch(2);
        EventFlow flow = new EventFlow()
                .sourceAsync(new DelimitedSource(new DataEventCsvDecoder0(), reader, "data-1").pollForever())
                .peek(audit::add).id("audit")
                .peek(i -> latch.countDown()).id("countdown")
                .start();
        String part1 = "id,name\n"
                + "1,greg\n"
                + "2,josie\n";
        writer.write(part1);
        writer.flush();
        if (!latch.await(1, TimeUnit.SECONDS)) {
            Assert.fail("records not received within time window of 1 second");
        } else {
            assertThat((List<?>)audit, hasSize(2));
        }
        reader.close();
        writer.close();
        flow.stop();
    }
    @Test
    public void testPullReader() throws FileNotFoundException, IOException, InterruptedException {
        PipedReader reader = new PipedReader();
        PipedWriter writer = new PipedWriter(reader);
        ArrayList audit = new ArrayList();
        CountDownLatch latch = new CountDownLatch(2);
        EventFlow flow = new EventFlow()
                .source(new DelimitedPullSource(new DataEventCsvDecoder0(), reader, "data-1"))
                .peek(audit::add)
                .peek(i -> latch.countDown())
                .start();
        String part1 = "id,name\n"
                + "1,greg\n"
                + "2,josie\n";
        writer.write(part1);
        writer.flush();
        if (!latch.await(1, TimeUnit.SECONDS)) {
            Assert.fail("records not received within time window of 1 second");
        } else {
            assertThat((List<?>)audit, hasSize(2));
        }
        flow.stop();
    }

    @Test
    public void testTransformPullReader() throws FileNotFoundException, IOException, InterruptedException {
        PipedReader reader = new PipedReader();
        PipedWriter writer = new PipedWriter(reader);
        ArrayList audit = new ArrayList();
        CountDownLatch latch = new CountDownLatch(2);
        EventFlow flow = new EventFlow()
                .source(TransformPullSource.transform(new DelimitedPullSource(new DataEventCsvDecoder0(), reader, "data-1"), i -> "transformed"))
                .peek(audit::add)
                .peek(i -> latch.countDown())
                .start();
        String part1 = "id,name\n"
                + "1,greg\n"
                + "2,josie\n";
        writer.write(part1);
        writer.flush();
        if (!latch.await(1, TimeUnit.SECONDS)) {
            Assert.fail("records not received within time window of 1 second");
        } else {
            assertThat((List<?>)audit, hasSize(2));
            assertThat((List<?>)audit, contains( "transformed", "transformed"));
        }
        flow.stop();
    }

    private static class EventSourceImpl implements EventSource {

        private final String id;
        private EventConsumer target;

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
            this.target = target;
            target.processEvent(id);
        }

        @Override
        public void tearDown() {
            log.info("tearDown id:{}", id);
            target.processEvent(id);
        }
    }

    @Data
    private static class TestFilter extends PipelineFilter {

        private final String id;
        private final List audit;

        @Override
        public void processEvent(Object o) {
            log.info("process id:{} event:{}", id, o);
            audit.add(id);
            audit.add(o.toString());
            propagate(o);
        }

        @Override
        protected void stopHandler() {
            log.info("stop id:{}", id);
            audit.add(id);
        }

        @Override
        protected void startHandler() {
            log.info("start id:{}", id);
            audit.add(id);
        }

        @Override
        protected void initHandler() {
            log.info("init id:{}", id);
            audit.add(id);
        }

    }
}
