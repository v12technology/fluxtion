/*
 * Copyright (C) 2021 V12 Technology Ltd
 *  All rights reserved.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Server Side Public License, version 1,
 *  as published by MongoDB, Inc.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Server Side Public License for more details.
 *
 *  You should have received a copy of the Server Side Public License
 *  along with this program.  If not, see 
 *  <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.streaming.builder.factory;

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.window.WindowBuildOperations;
import static com.fluxtion.ext.streaming.api.window.WindowBuildOperations.service;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.avg;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.cumSum;

/**
 * Utility functions to construct windowed functions using pre-existing functions
 * supplied by Fluxtion.
 * @author gregp
 */
public interface WindowFunctionsBuilder {

    /**
     * Calculates a moving average using a count based window.
     * 
     * Window size = itemsPerBucket * numberOfBuckets
     * Publish rate = itemsPerBucket
     * @param <T>
     * @param supplier 
     * @param itemsPerBucket
     * @param numberOfBuckets
     * @return 
     */
    public static <T> Wrapper<Number> movingAvg(SerializableFunction<T, Number> supplier, int itemsPerBucket, int numberOfBuckets) {
        return movingAvg(select(supplier), itemsPerBucket, numberOfBuckets);
    }
    
    public static Wrapper<Number> movingAvg(Wrapper<? extends Number> source, int itemsPerBucket, int numberOfBuckets){
        return service().sliding(source, avg(), itemsPerBucket, numberOfBuckets);
    }
 
    public static <T extends Number> Wrapper<Number> movingAvg(Class<T> supplier, int itemsPerBucket, int numberOfBuckets) {
        return movingAvg(EventSelect.select(supplier), itemsPerBucket, numberOfBuckets);
    }
    
    //
    public static <T> Wrapper<Number> movingCumSum(SerializableFunction<T, Number> supplier, int itemsPerBucket, int numberOfBuckets) {
        return WindowBuildOperations.service().sliding(select(supplier), cumSum(), itemsPerBucket, numberOfBuckets);
    }
    
    public static Wrapper<Number> movingCumSum(Wrapper<? extends Number> source, int itemsPerBucket, int numberOfBuckets){
        return service().sliding(source, cumSum(), itemsPerBucket, numberOfBuckets);
    }

    public static <T extends Number> Wrapper<Number> movingCumSum(Class<T> supplier, int itemsPerBucket, int numberOfBuckets) {
        return movingCumSum(EventSelect.select(supplier), itemsPerBucket, numberOfBuckets);
    }

}
