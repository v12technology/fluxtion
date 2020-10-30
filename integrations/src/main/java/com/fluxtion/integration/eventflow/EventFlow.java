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
package com.fluxtion.integration.eventflow;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.integration.eventflow.filters.SepEventPublisher;
import com.fluxtion.integration.eventflow.filters.SynchronizedFilter;
import com.fluxtion.integration.eventflow.sources.AsynchEventSource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.extern.log4j.Log4j2;

/**
 * Joins a {@link Pipeline} to {@link EventSource}'s and {@link EventSink}.
 *
 * An event flow can have multiple EventSources feeding into single pipeline.
 * The
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class EventFlow {

    private final Pipeline.PipelineStatge<SynchronizedFilter> dispatcherFilter;
    private Pipeline.PipelineStatge<PipelineFilter> lastStage;
    private final Pipeline pipeline;
    private final PipelineBuilder pipelineBuilder;
    private final LinkedHashMap<String, EventSource> sourceMap;
    private final LinkedHashMap<String, EventQueueSource> queueSourceMap;
    private final List startedInstances;
    private SynchronizedFilter defaultDispatcher;
    private final AtomicBoolean runReaderThread;
    private ReaderThread readerThread;
    private static final LongAdder count = new LongAdder();

    private enum State {
        STARTED, STOPPED, INIT
    };
    private State currentState;

    public EventFlow() {
        sourceMap = new LinkedHashMap<>();
        queueSourceMap = new LinkedHashMap<>();
        startedInstances = new ArrayList();
        defaultDispatcher = new SynchronizedFilter();
        currentState = State.INIT;
        pipelineBuilder = new PipelineBuilder();
        pipeline = new Pipeline();
        dispatcherFilter = pipeline.entry(defaultDispatcher);
        runReaderThread = new AtomicBoolean(false);
    }

    public static EventFlow flow(EventSource source) {
        return new EventFlow().source(source);
    }

    public EventFlow start() {
        switch (currentState) {
            case STARTED:
                log.error("Cannot start already in started state");
                break;
            default: {
                currentState = State.STARTED;
                startedInstances.clear();
                startPipeline();
                initSources();
                startSources();
            }
        }
        return this;
    }

    public EventFlow stop() {

        switch (currentState) {
            case STOPPED:
                log.error("Cannot stop already in stopped state");
                break;
            case INIT:
                log.error("Cannot stop not yet started");
                break;
            default: {
                teardownSources();
                teardownPipeline();
                currentState = State.STOPPED;
            }
        }
        return this;
    }

    /**
     * Set the thread dispatch strategy that routes events from sources to
     * pipeline
     */
    public void setThreadDispatcher() {
        throw new UnsupportedOperationException("configurable dispatch strategy not supported");
    }

    public PipelineBuilder first(PipelineFilter firstFilter) {
        lastStage = dispatcherFilter.next(firstFilter);
        return pipelineBuilder;
    }

    public <S extends StaticEventProcessor> PipelineBuilder first(S filter) {
        return first(SepEventPublisher.of(filter));
    }

    public <S extends EventConsumer> PipelineBuilder peek(S filter) {
        return first(new PipelineFilter.Consumer(filter));
    }

    public <S extends Function> PipelineBuilder map(S filter) {
        return first(new PipelineFilter.MapFunction(filter));
    }

    public <S extends Predicate> PipelineBuilder filter(S filter) {
        return first(new PipelineFilter.PredicateFunction(filter));
    }

    public PipelineBuilder sink(EventSink publisher) {
        return first(new PipelineFilter.SinkStage(publisher));
    }

    public EventFlow source(EventSource source, String identifier) {
        String id = identifier == null ? source.id() : identifier;
        sourceMap.put(id, source);
        return this;
    }

    public EventFlow source(EventSource source) {
        return source(source, null);
    }

    public EventFlow sourceAsync(EventSource source) {
        return source(new AsynchEventSource(source), null);
    }

    public EventFlow sourceAsync(EventSource source, String identifier) {
        return source(new AsynchEventSource(source), identifier);
    }

    public EventFlow source(EventQueueSource source, String identifier) {
        String id = identifier == null ? source.id() : identifier;
        queueSourceMap.put(id, source);
        return this;
    }

    public EventFlow source(EventQueueSource source) {
        return source(source, null);
    }

    public class PipelineBuilder {

        public <S extends PipelineFilter> PipelineBuilder id(String id) {
            lastStage.id(id);
            return this;
        }

        public <S extends PipelineFilter> PipelineBuilder next(S filter) {
            lastStage = lastStage.next(filter);
            return this;
        }

        public <S extends StaticEventProcessor> PipelineBuilder next(S filter) {
            next(SepEventPublisher.of(filter));
            return this;
        }

        public <S extends EventConsumer> PipelineBuilder peek(S filter) {
            next(new PipelineFilter.Consumer(filter));
            return this;
        }

        public <S extends Function> PipelineBuilder map(S filter) {
            next(new PipelineFilter.MapFunction(filter));
            return this;
        }

        public <S extends Predicate> PipelineBuilder filter(S filter) {
            next(new PipelineFilter.PredicateFunction(filter));
            return this;
        }

        public PipelineBuilder sink(EventSink publisher) {
            next(new PipelineFilter.SinkStage(publisher));
            return this;
        }

        public EventFlow start() {
            return EventFlow.this.start();
        }

        public EventFlow stop() {
            return EventFlow.this.stop();
        }

    }

    private void startPipeline() {
        //wire in the dispatcher
        log.info("pipeline start begin");
        pipeline.start();
        log.info("pipeline start end");
    }

    private void teardownPipeline() {
        log.info("pipeline stop begin");
        pipeline.stop();
        log.info("pipeline stop end");
    }

    private void initSources() {
        log.info("init sources begin");
        sourceMap.forEach((String id, EventSource src) -> {
            if (startedInstances.contains(src)) {
                log.info("eventSource:'{}' already init", id);
            } else {
                log.info("init eventSource:'{}'", id);
                src.init();
            }
        });
        queueSourceMap.forEach((String id, EventQueueSource src) -> {
            if (startedInstances.contains(src)) {
                log.info("eventQueueSource:'{}' already init", id);
            } else {
                log.info("init eventQueueSource:'{}'", id);
                src.init();
            }
        });
        log.info("init sources end");
    }

    private void startSources() {
        log.info("start sources begin");
        sourceMap.forEach((String id, EventSource src) -> {
            log.info("starting eventSource:'{}'", id);
            //TODO remove this dummy target and replace with the pipeline
            src.start(defaultDispatcher);
        });
        queueSourceMap.forEach((String id, EventQueueSource src) -> {
            log.info("starting eventQueueSource:'{}'", id);
            src.start(defaultDispatcher);
        });
        log.info("start sources end");
        if (!queueSourceMap.isEmpty()) {
            log.info("starting reader thread");
            runReaderThread.set(true);
            readerThread = new ReaderThread();
            readerThread.start();
        }
    }

    private void teardownSources() {
        log.info("teardown sources begin");
        sourceMap.forEach((String id, EventSource src) -> {
            log.info("teardown eventSource:'{}'", id);
            src.tearDown();
        });
        log.info("teardown sources end");
        runReaderThread.set(false);
        if (readerThread != null) {
            try {
                log.debug("waiting for reader thread to stop");
                readerThread.join(5_000);
                log.debug("reader thread stopped");
            } catch (InterruptedException ex) {
                log.info("interrupted while waiting for reader thread to stop", ex);
            }
        }
    }

    private class ReaderThread extends Thread {

        public ReaderThread() {
            super("sourceQueueReader-" + count.intValue());
            count.increment();
        }

        @Override
        public void run() {
            log.info("starting reader thread");
            EventQueueSource[] sources = new EventQueueSource[queueSourceMap.size()];
            sources = queueSourceMap.values().toArray(sources);
            while (runReaderThread.get()) {
                for (int i = 0; i < sources.length; i++) {
                    EventQueueSource source = sources[i];
                    source.poll();
                }
//                LockSupport.parkNanos(1_000);
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(EventFlow.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
            log.info("exiting reader thread");
        }

    }

}
