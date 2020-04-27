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

import com.fluxtion.api.SepContext;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.WrappedCollection;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.window.CountReset;
import com.fluxtion.ext.streaming.api.window.SlidingNumberAggregator;
import com.fluxtion.ext.streaming.api.window.TimeReset;
import static com.fluxtion.ext.streaming.builder.factory.FilterByNotificationBuilder.filter;
import com.fluxtion.ext.streaming.api.Stateful.StatefulNumber;
import com.fluxtion.ext.streaming.api.WrappedList;
import com.fluxtion.ext.streaming.api.window.SlidingAggregator;
import com.fluxtion.ext.streaming.api.window.SlidingCollectionAggregator;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class WindowBuilder {

    /**
     * Creates a count based sliding window aggregating data using the supplied
     * mapping function. The user specifies the number of counts in a bucket and
     * the total number of buckets. The data is published at the bucket
     * intervals.
     *
     * @param <R> The output type of the mapping function
     * @param <S> The input type of the mapping function
     * @param <T> The wrapper type of the source data
     * @param source The source instance for the window
     * @param mapper Mapping function
     * @param itemsPerBucket Number of elements in a bucket, also the publish rate
     * of the aggregated data
     * @param numberOfBuckets The total number of buckets to be aggregated
     * @return The combination of all bucket values that are within scope
     */
    public static <R, S, T extends Wrapper<S>> Wrapper<R> sliding(T source, SerializableFunction<S, R> mapper, int itemsPerBucket, int numberOfBuckets) {
        Object mappingInstance = null;
        if (mapper.captured().length == 0) {
            throw new RuntimeException("mapping function must be an instance method");
        } else {
            mappingInstance = mapper.captured()[0];
        }
        Class clazz = mappingInstance.getClass();
        Wrapper<R> tumble = tumble(source.map(mapper), itemsPerBucket);
        if (StatefulNumber.class.isAssignableFrom(clazz)) {
            return (Wrapper<R>) SepContext.service().addOrReuse(new SlidingNumberAggregator(tumble, (StatefulNumber) mappingInstance, numberOfBuckets));
        } else if (Stateful.class.isAssignableFrom(clazz)) {
            Class<R> returnType = (Class<R>) mapper.method().getReturnType();
            return SepContext.service().addOrReuse(new SlidingAggregator<>(tumble, returnType, (Stateful) mappingInstance, numberOfBuckets));
        } else {
            throw new RuntimeException("unsupported type for aggregation function must implement Stateful or StatefulNumber, class:" + clazz);
        }
    }

    public static <R, S, T extends Wrapper<S>> Wrapper<R> sliding(Class<S> sourceClass, SerializableFunction<S, R> mapper, int itemsPerBucket, int numberOfBuckets) {
        return sliding((T)EventSelect.select(sourceClass).collect().asWrapper(), mapper, itemsPerBucket, numberOfBuckets);
    }
    
    
    /**
     * Creates a time based sliding window aggregating data using the supplied
     * mapping function. The user specifies the time in a bucket and
     * the total number of buckets. The data is published at the bucket
     * intervals.
     * 
     * @param <R> The output type of the mapping function
     * @param <S> The input type of the mapping function
     * @param <T> The wrapper type of the source data
     * @param source The source instance for the window
     * @param mapper Mapping function
     * @param time Duration of each bucket 
     * @param numberOfBuckets The total number of buckets to be aggregated
     * @return The combination of all bucket values that are within scope
     */
    public static <R, S, T extends Wrapper<S>> Wrapper<R> sliding(T source, SerializableFunction<S, R> mapper,  Duration time, int numberOfBuckets) {
        Object mappingInstance = null;
        if (mapper.captured().length == 0) {
            throw new RuntimeException("mapping function must be an instance method");
        } else {
            mappingInstance = mapper.captured()[0];
        }
        Class clazz = mappingInstance.getClass();
        final TimeReset timeReset = new TimeReset(source, time.getMillis(), null);
        Wrapper<T> tumble = filter((Wrapper<T>) source.map(mapper), timeReset);
//        Wrapper<R> tumble = tumble(source.map(mapper), time);
        if (StatefulNumber.class.isAssignableFrom(clazz)) {
            final SlidingNumberAggregator slidingNumberAggregator = new SlidingNumberAggregator(tumble, (StatefulNumber) mappingInstance, numberOfBuckets);
            slidingNumberAggregator.setTimeReset(timeReset);
            return (Wrapper<R>) SepContext.service().addOrReuse(slidingNumberAggregator);
        } else if (Stateful.class.isAssignableFrom(clazz)) {
            Class<R> returnType = (Class<R>) mapper.method().getReturnType();
            SlidingAggregator slidingAggregator = SepContext.service().addOrReuse(new SlidingAggregator<>(tumble, returnType, (Stateful) mappingInstance, numberOfBuckets));
            slidingAggregator.setTimeReset(timeReset);
            return slidingAggregator;
        } else {
            throw new RuntimeException("unsupported type for aggregation function must implement Stateful or StatefulNumber, class:" + clazz);
        }
    }
    
        
    public static <R, S, T extends Wrapper<S>> Wrapper<R> sliding(Class<S> sourceClass, SerializableFunction<S, R> mapper, Duration time, int numberOfBuckets) {
        return sliding((T)EventSelect.select(sourceClass), mapper, time, numberOfBuckets);
    }
    
    /**
     * Creates an aggregated collection of data points as a WrappedList. The
     * user specifies the number of counts in a bucket and the total number of
     * buckets. The WrappedList is published at the bucket intervals.
     *
     * @param <S> The type of each data point
     * @param <T> The type of collection to be aggregated
     * @param source The source collection
     * @param itemsPerBucket Number of elements in a bucket, also the publish rate
     * of the aggregated data
     * @param numberOfBuckets The total number of buckets to be aggregated
     * @return A WrappedList containing the union of all buckets within scope
     */
    public static <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> sliding(T source, int itemsPerBucket, int numberOfBuckets) {
        Wrapper<T> tumble = tumble(source, itemsPerBucket);
        final SlidingCollectionAggregator slidingAggregator = SepContext.service().addOrReuse(new SlidingCollectionAggregator<>(tumble, (WrappedCollection) source, numberOfBuckets));
        return slidingAggregator.getTargetCollection();
    }
    
    public static <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> sliding(Class<S> sourceClass, int itemsPerBucket, int numberOfBuckets) {
        return  sliding((T)EventSelect.select(sourceClass).collect(), itemsPerBucket, numberOfBuckets);
    }

    
    /**
     * Creates an aggregated collection of data points as a WrappedList. The
     * user specifies the time duration of a bucket and the total number of
     * buckets. The WrappedList is published at the bucket intervals.
     *
     * @param <S> The type of each data point
     * @param <T> The type of collection to be aggregated
     * @param source The source collection
     * @param time Duration of a bucket
     * @param numberOfBuckets The total number of buckets to be aggregated
     * @return A WrappedList containg the union of all buckets within scope
     */
    public static <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> sliding(T source, Duration time, int numberOfBuckets) {
        final TimeReset timeReset = new TimeReset(source, time.getMillis(), null);
//        Wrapper<T> tumble = tumble(source, time);
        Wrapper<T> tumble = filter(source, timeReset);
        final SlidingCollectionAggregator slidingAggregator = SepContext.service().addOrReuse(new SlidingCollectionAggregator<>(tumble, (WrappedCollection) source, numberOfBuckets));
        slidingAggregator.setTimeReset(timeReset);
        return slidingAggregator.getTargetCollection();
    }
    
    public static <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> sliding(Class<S> sourceClass, Duration time, int numberOfBuckets) {
        return  sliding((T)EventSelect.select(sourceClass).collect(), time, numberOfBuckets);
    }

    
    public static <S extends WrappedCollection> Wrapper<S> tumble(S source, int count) {
        return filter(source, new CountReset(source, count));
    }

    public static <S, T extends Wrapper<S>> Wrapper<S> tumble(T source, int count) {
        return filter(source, new CountReset(source, count));
    }

    public static <S, T extends GroupBy<S>> Wrapper<T> tumble(T source, int count) {
        CountReset countBucketNotifier = new CountReset(source, count);
        return filter(source, countBucketNotifier);
    }

    public static <S extends WrappedCollection> Wrapper<S> tumble(S source, Duration time) {
        return filter(source, new TimeReset(source, time.getMillis(), null));
    }

    public static <S, T extends Wrapper<S>> Wrapper<S> tumble(T source, Duration time) {
        return filter(source, new TimeReset(source, time.getMillis(), null));
    }

    public static <S, T extends GroupBy<S>> Wrapper<T> tumble(T source, Duration time) {
        return filter(source, new TimeReset(source, time.getMillis(), null));
    }

}
