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

import com.fluxtion.integration.eventflow.EventConsumer;
import com.fluxtion.integration.eventflow.EventSource;
import java.util.concurrent.Executors;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T>
 * @param <R>
 */
public class AsynchEventSource<T, R> implements EventConsumer<T>, EventSource<R> {

    private final EventSource source;
    private String id;
    private static int count;
    private EventConsumer target;

    public AsynchEventSource(EventSource source) {
        this.source = source;
        id = "AsynchEventSource-" + count++;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init() {
        source.init();
    }

    @Override
    public void processEvent(T o) {
        target.processEvent(o);
    }

    @Override
    public void start(EventConsumer<R> target) {
        this.target = target;
        Executors.newSingleThreadExecutor().submit(() ->  source.start(this));
    }

    @Override
    public void tearDown() {
        source.tearDown();
    }
}
