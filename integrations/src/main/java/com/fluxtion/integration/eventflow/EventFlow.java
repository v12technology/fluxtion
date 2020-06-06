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

import com.fluxtion.api.event.RegisterEventHandler;
import com.fluxtion.integration.eventflow.filters.SynchronizedFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class EventFlow {
    private final Pipeline.PipelineStatge<SynchronizedFilter> dispatcherFilter;
    private  Pipeline.PipelineStatge<PipelineFilter> lastStage;
    private final Pipeline pipeline;

    private PipelineBuilder pipelineBuilder;
    private final LinkedHashMap<String, EventSource> sourceMap;
    private final LinkedHashMap<String, EventPublisher> publisherMap;
    private final List startedInstances;
    private SynchronizedFilter defaultDispatcher;

    private enum State {
        STARTED, STOPPED, INIT
    };
    private State currentState;

    public EventFlow() {
        sourceMap = new LinkedHashMap<>();
        publisherMap = new LinkedHashMap<>();
        startedInstances = new ArrayList();
        defaultDispatcher = new SynchronizedFilter();
        currentState = State.INIT;
        pipelineBuilder = new PipelineBuilder();
        pipeline = new Pipeline();
        dispatcherFilter = pipeline.entry(defaultDispatcher);
    }

    public EventFlow start() {
        switch (currentState) {
            case STARTED:
                log.error("Cannot start already in started state");
                break;
            default: {
                startedInstances.clear();
                startPipeline();
                initSources();
                startPublishers();
                startSources();
                currentState = State.STARTED;
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
                log.error("Cannot stop not yet state");
                break;
            default: {
                teardownSources();
                teardownPublishers();
                teardownPipeline();
                currentState = State.STOPPED;
            }
        }
        return this;
    }

    /**
     * Set the thread dispatch strategy that routes events from
     */
    public void setThreadDispatcher() {
    }

    public PipelineBuilder pipeline(PipelineFilter firstFilter) {
        lastStage = dispatcherFilter.next(firstFilter);
        return pipelineBuilder;
    }

    /**
     * Register a publisher as an output for use in the pipeline
     *
     * @param publisher
     * @param identifier
     * @return
     */
    public EventFlow publisher(EventPublisher publisher, String identifier) {
        String id = identifier == null ? publisher.id() : identifier;
        publisherMap.put(id, publisher);
        return this;
    }

    public EventFlow publisher(EventPublisher publisher) {
        return publisher(publisher, null);
    }

    /**
     *
     * @param source
     * @param identifier
     * @return
     */
    public EventFlow source(EventSource source, String identifier) {
        String id = identifier == null ? source.id() : identifier;
        sourceMap.put(id, source);
        return this;
    }

    public EventFlow source(EventSource source) {
        return source(source, null);
    }

    public class PipelineBuilder {

        public <S extends PipelineFilter> PipelineBuilder next(S filter) {
            lastStage = lastStage.next(filter);
            return this;
        }
        
        public EventFlow start(){
            return EventFlow.this.start();
        }

    }

    private void startPipeline() {
        //wire in the dispatcher
        log.info("pipeline start begin");
        pipeline.start();
        log.info("pipeline start end");
    }

    private void teardownPipeline() {
        log.info("pipeline start begin");
        pipeline.stop();
        log.info("pipeline start end");
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
        log.info("init sources end");
    }

    private void startSources() {
        log.info("start sources begin");
        sourceMap.forEach((String id, EventSource src) -> {
            log.info("starting eventSource:'{}'", id);
            //TODO remove this dummy target and replace with the pipeline
            src.start(defaultDispatcher);
        });
        log.info("start sources end");
    }

    private void teardownSources() {
        log.info("teardown sources begin");
        sourceMap.forEach((String id, EventSource src) -> {
            log.info("teardown eventSource:'{}'", id);
            src.tearDown();
        });
        log.info("teardown sources end");
    }

    private void startPublishers() {
        log.info("start publishers begin");
        publisherMap.forEach((String id, EventPublisher con) -> {
            if (startedInstances.contains(con)) {
                log.info("eventPublisher:'{}' already started", id);
            } else {
                log.info("starting eventPublisher:'{}'", id);
                con.init();
                defaultDispatcher.processEvent(new RegisterEventHandler(con::publish));
            }
        });
        log.info("start publishers end");
    }

    private void teardownPublishers() {
        log.info("teardown publishers begin");
        publisherMap.forEach((String id, EventPublisher con) -> {
            log.info("teardown eventPublisher:'{}'", id);
            con.tearDown();
        });
        log.info("teardown publishers end");
    }

}
