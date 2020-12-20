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
package com.fluxtion.ext.streaming.api.window;

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.Duration;
import com.fluxtion.ext.streaming.api.WrappedCollection;
import com.fluxtion.ext.streaming.api.WrappedList;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import java.util.ServiceLoader;

/**
 * Window building functions for sliding and tumbling windows.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public interface WindowBuildOperations {
    
    //sliding collections
    default <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> sliding(T source, int itemsPerBucket, int numberOfBuckets){
        return service().sliding(source, itemsPerBucket, numberOfBuckets);
    }
    
    default <S, T extends GroupBy<S>> GroupBy<S> sliding(T source, int itemsPerBucket, int numberOfBuckets){
        return service().sliding(source, itemsPerBucket, numberOfBuckets);
    }
    
    default <S, T extends GroupBy<S>> GroupBy<S> sliding(T source, Duration time, int numberOfBuckets){
        return service().sliding(source, time, numberOfBuckets);
    }
    
    default <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> sliding(T source, Duration time, int numberOfBuckets){
        return service().sliding(source, time, numberOfBuckets);
    }
    
    //tumbling collections
    default <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> tumbling(T source, int itemsPerBucket){
        return service().tumbling(source, itemsPerBucket);
    }
    
    default <S, T extends GroupBy<S>> GroupBy<S> tumbling(T source, int itemsPerBucket){
        return service().tumbling(source, itemsPerBucket);
    }
    
    default <S, T extends GroupBy<S>> GroupBy<S> tumbling(T source, Duration time){
        return service().tumbling(source, time);
    }
    
    default <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> tumbling(T source, Duration time){
        return service().tumbling(source, time);
    }
    
    //sliding scalar
    default <R, S, T extends Wrapper<S>> Wrapper<R> sliding(T source, SerializableFunction<? extends S, R> mapper, int itemsPerBucket, int numberOfBuckets){
        return service().sliding(source, mapper, itemsPerBucket, numberOfBuckets);
    }
    
    default <R, S, T extends Wrapper<S>> Wrapper<R> sliding(T source, SerializableFunction<? extends S, R> mapper, Duration time, int numberOfBuckets){
        return service().sliding(source, mapper, time, numberOfBuckets);
    }
    
    //tumbling scalar
    default <R, S, T extends Wrapper<S>> Wrapper<R> tumbling(T source, SerializableFunction<? extends S, R> mapper, int itemsPerBucket){
        return service().tumbling(source, mapper, itemsPerBucket);
    }
    
    default <R, S, T extends Wrapper<S>> Wrapper<R> tumbling(T source, SerializableFunction<? extends S, R> mapper, Duration time){
        return service().tumbling(source, mapper, time);
    }
    
    static WindowBuildOperations service() {
        ServiceLoader<WindowBuildOperations> load = ServiceLoader.load(WindowBuildOperations.class);
        if (load.iterator().hasNext()) {
            return load.iterator().next();
        } else {
            load = ServiceLoader.load(WindowBuildOperations.class, WindowBuildOperations.class.getClassLoader());
            if (load.iterator().hasNext()) {
                return load.iterator().next();
            } else {
                return new WindowBuildOperations() {
                    @Override
                    public <S, T extends GroupBy<S>> GroupBy<S> sliding(T source, int itemsPerBucket, int numberOfBuckets) {
                        return null;
                    }

                    @Override
                    public <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> sliding(T source, int itemsPerBucket, int numberOfBuckets) {
                        return null;
                    }
                    
                };
            }
        }
    }


}
