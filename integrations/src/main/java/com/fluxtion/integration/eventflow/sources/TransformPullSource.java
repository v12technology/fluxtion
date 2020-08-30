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
import com.fluxtion.integration.eventflow.EventQueueSource;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T>
 * @param <R>
 */
@Log4j2
public class TransformPullSource<T,R> implements EventConsumer<T>, EventQueueSource<R> {

    protected final EventQueueSource<T> source;
    protected final Function<T,R> f;
    private String id;
    private static int count;
    protected EventConsumer<R> target;
    
    public static <T,R> TransformPullSource<T,R> transform(String id, EventQueueSource<T> source, Function<T,R> transformFunction){
        return new TransformPullSource(id, source, transformFunction);
    }
    
    public static <T,R> TransformPullSource<T,R> transform(EventQueueSource<T> source, Function<T,R> transformFunction){
        return new TransformPullSource(source, transformFunction);
    }
    
    public static <R> FilterSource<R> filter(EventQueueSource<R> source, Predicate<R> predicate){
        return new FilterSource(source, predicate);
    }

    public static <R> FilterSource<R> filter(String id, EventQueueSource<R> source, Predicate<R> predicate){
        return new FilterSource(id, source, predicate);
    }
    

    public TransformPullSource(EventQueueSource source, Function f) {
        this.source = source;
        this.f = f;
    }

    public TransformPullSource(String id, EventQueueSource source, Function f) {
        this.source = source;
        this.f = f;
        this.id = id;
    }
    
    public <P> TransformPullSource<R, P> transform(Function<R, P> transformFunction){
        return transform(this, transformFunction);
    }
    
    public <P> TransformPullSource<R, P> transform(String id, Function<R, P> transformFunction){
        return transform(id, this, transformFunction);
    }
   
    public FilterSource<R> filter(Predicate<R> predicate){
        return filter(this, predicate);
    }
    
    public FilterSource<R> filter(String id, Predicate<R> predicate){
        return filter(id, this, predicate);
    }
    
    @Override
    public String id() {
        return id;
    }

    @Override
    public void init() {
        if (id == null) {
            id = "TransformPullSource-" + count++;
        }
        source.init();
    }

    @Override
    public void poll() {
        source.poll();
    }

    @Override
    public void processEvent(T o) {
        target.processEvent(f.apply(o));
    }
    
    @Override
    public void start(EventConsumer<R> target) {
        this.target = target;
        source.start(this);
    }

    @Override
    public void tearDown() {
        source.tearDown();
    }
    
    public static class FilterSource<T> extends TransformPullSource<T, T>{

        private final Predicate<T> predicate;
        
        public FilterSource(EventQueueSource<T> source, Predicate<T> predicate) {
            super(source, null);
            this.predicate = predicate;
        }
        
        public FilterSource(String id, EventQueueSource<T> source, Predicate<T> predicate) {
            super(id, source, null);
            this.predicate = predicate;
        }

        @Override
        public void processEvent(T o) {
            if(predicate.test(o)){
                target.processEvent(o);
            }
        }
        
    }

}
