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
package com.fluxtion.ext.streaming.builder.factory;

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.Duration;
import com.fluxtion.ext.streaming.api.WrappedCollection;
import com.fluxtion.ext.streaming.api.WrappedList;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.window.WindowBuildOperations;
import com.google.auto.service.AutoService;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */

@AutoService(WindowBuildOperations.class)
public class WindowBuildOperationsService implements WindowBuildOperations {

    //sliding collections
    @Override
    public <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> sliding(T source, int itemsPerBucket, int numberOfBuckets) {
        return WindowBuilder.sliding(source, itemsPerBucket, numberOfBuckets); 
    }
    
    @Override
    public <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> sliding(T source, Duration time, int numberOfBuckets) {
        return WindowBuilder.sliding(source, time, numberOfBuckets); 
    }

    @Override
    public <S, T extends GroupBy<S>> GroupBy<S> sliding(T source, int itemsPerBucket, int numberOfBuckets) {
        return WindowBuilder.sliding(source, itemsPerBucket, numberOfBuckets); 
    }
    
    @Override
    public <S, T extends GroupBy<S>> GroupBy<S> sliding(T source, Duration time, int numberOfBuckets) {
        return WindowBuilder.sliding(source, time, numberOfBuckets); 
    }

    //tumble collections
    @Override
    public <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> tumbling(T source, Duration time) {
        return WindowBuilder.tumble(source, time); 
    }

    @Override
    public <S, T extends GroupBy<S>> GroupBy<S> tumbling(T source, Duration time) {
        return WindowBuilder.tumble(source, time); 
    }

    @Override
    public <S, T extends GroupBy<S>> GroupBy<S> tumbling(T source, int itemsPerBucket) {
        return WindowBuilder.tumble(source, itemsPerBucket); 
    }

    @Override
    public <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> tumbling(T source, int itemsPerBucket) {
        return WindowBuilder.tumble(source, itemsPerBucket); 
    }

    //sliding scalar
    @Override
    public <R, S, T extends Wrapper<S>> Wrapper<R> tumbling(T source, SerializableFunction<? extends S, R> mapper, Duration time) {
        return WindowBuilder.tumble(source, mapper, time); 
    }

    @Override
    public <R, S, T extends Wrapper<S>> Wrapper<R> tumbling(T source, SerializableFunction<? extends S, R> mapper, int itemsPerBucket) {
        return WindowBuilder.tumble(source, mapper, itemsPerBucket); 
    }

    @Override
    public <R, S, T extends Wrapper<S>> Wrapper<R> sliding(T source, SerializableFunction<? extends S, R> mapper, Duration time, int numberOfBuckets) {
        return WindowBuilder.sliding(source, mapper, time, numberOfBuckets); 
    }

    @Override
    public <R, S, T extends Wrapper<S>> Wrapper<R> sliding(T source, SerializableFunction<? extends S, R> mapper, int itemsPerBucket, int numberOfBuckets) {
        return WindowBuilder.sliding(source, mapper, itemsPerBucket, numberOfBuckets); 
    }
    
    
    
}
