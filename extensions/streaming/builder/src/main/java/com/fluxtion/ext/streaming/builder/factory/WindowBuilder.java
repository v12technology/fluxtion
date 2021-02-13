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

import com.fluxtion.ext.streaming.api.Duration;
import com.fluxtion.api.SepContext;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.WrappedCollection;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.window.CountReset;
import com.fluxtion.ext.streaming.api.window.SlidingNumberAggregator;
import com.fluxtion.ext.streaming.api.window.TimeReset;
import com.fluxtion.ext.streaming.api.Stateful.StatefulNumber;
import com.fluxtion.ext.streaming.api.WrappedList;
import com.fluxtion.ext.streaming.api.window.SlidingAggregator;
import com.fluxtion.ext.streaming.api.window.SlidingCollectionAggregator;
import com.fluxtion.ext.streaming.api.window.SlidingGroupByAggregator;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class WindowBuilder{

    // ==================================================
    // SLIDING FUNCTIONS - REDUCING
    // ==================================================
    /**
     * Creates a count based sliding window aggregating data into a single value using the supplied mapping function.
     * The user specifies the number of counts in a bucket and the total number of buckets. The data is published at the
     * bucket intervals.
     *
     * @param <R> The output type of the mapping function
     * @param <S> The input type of the mapping function
     * @param <T> The wrapper type of the source data
     * @param source The source instance for the window
     * @param mapper Mapping function
     * @param itemsPerBucket Number of elements in a bucket, also the publish rate of the aggregated data
     * @param numberOfBuckets The total number of buckets to be aggregated
     * @return The combination of all bucket values that are within scope
     */
    public static <R, S, T extends Wrapper<S>> Wrapper<R> sliding(T source, SerializableFunction<? extends S, R> mapper, int itemsPerBucket, int numberOfBuckets) {
        Object mappingInstance = null;
        if (mapper.captured().length == 0) {
            throw new RuntimeException("mapping function must be an instance method");
        } else {
            mappingInstance = mapper.captured()[0];
        }
        Class clazz = mappingInstance.getClass();
        CountReset tumble = new CountReset(source.map(mapper), itemsPerBucket);
        if (StatefulNumber.class.isAssignableFrom(clazz)) {
            return (Wrapper<R>) SepContext.service().addOrReuse(new SlidingNumberAggregator(tumble, (StatefulNumber) mappingInstance, numberOfBuckets));
        } else if (Stateful.class.isAssignableFrom(clazz)) {
            Class<R> returnType = (Class<R>) mapper.method().getReturnType();
            return SepContext.service().addOrReuse(new SlidingAggregator(tumble, returnType, (Stateful) mappingInstance, numberOfBuckets));
        } else {
            throw new RuntimeException("unsupported type for aggregation function must implement Stateful or StatefulNumber, class:" + clazz);
        }
    }

    public static <R, S> Wrapper<R> sliding(Class<S> sourceClass, SerializableFunction<? extends S, R> mapper, int itemsPerBucket, int numberOfBuckets) {
        return sliding(EventSelect.select(sourceClass), mapper, itemsPerBucket, numberOfBuckets);
    }
    
    /**
     * Creates a time based sliding window aggregating data into a single value using the supplied mapping function. The
     * user specifies the time in a bucket and the total number of buckets. The data is published at the bucket
     * intervals. The expiry time of data is equivalent to duration of a bucket * number of buckets. As data os received
     * it is pushed into the current active bucket
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
    public static <R, S, T extends Wrapper<S>> Wrapper<R> sliding(T source, SerializableFunction<? extends S, R> mapper, Duration time, int numberOfBuckets) {
        Object mappingInstance = null;
        if (mapper.captured().length == 0) {
            throw new RuntimeException("mapping function must be an instance method");
        } else {
            mappingInstance = mapper.captured()[0];
        }
        Class clazz = mappingInstance.getClass();
        final TimeReset timeReset = new TimeReset(source, time.getMillis(), null);
        Wrapper<R> tumble = source.map(mapper);
//        Wrapper<T> tumbleIncremental = notifierOverride((Wrapper<T>) source.map(mapper), timeReset);
//        Wrapper<R> tumbleIncremental = tumbleIncremental(source.map(mapper), time);
        if (StatefulNumber.class.isAssignableFrom(clazz)) {
            final SlidingNumberAggregator slidingNumberAggregator = new SlidingNumberAggregator(tumble, (StatefulNumber) mappingInstance, numberOfBuckets);
            slidingNumberAggregator.setTimeReset(timeReset);
            return (Wrapper<R>) SepContext.service().addOrReuse(slidingNumberAggregator);
        } else if (Stateful.class.isAssignableFrom(clazz)) {
            Class<R> returnType = (Class<R>) mapper.method().getReturnType();
            SlidingAggregator aggregator = new SlidingAggregator(tumble, returnType, (Stateful) mappingInstance, numberOfBuckets);
            SlidingAggregator slidingAggregator = SepContext.service().addOrReuse(aggregator);
            slidingAggregator.setTimeReset(timeReset);
            return slidingAggregator;
        } else {
            throw new RuntimeException("unsupported type for aggregation function must implement Stateful or StatefulNumber, class:" + clazz);
        }
    }

    public static <R, S> Wrapper<R> sliding(Class<S> sourceClass, SerializableFunction<? extends S, R> mapper, Duration time, int numberOfBuckets) {
        return sliding(EventSelect.select(sourceClass), mapper, time, numberOfBuckets);
    }

    // ==================================================
    // SLIDING FUNCTIONS - COLLECTING
    // ================================================== 
    /**
     * Creates an aggregated collection of data points as a WrappedList. The user specifies the number of counts in a
     * bucket and the total number of buckets. The WrappedList is published at the bucket intervals.
     *
     * @param <S> The type of each data point
     * @param <T> The type of collection to be aggregated
     * @param source The source collection
     * @param itemsPerBucket Number of elements in a bucket, also the publish rate of the aggregated data
     * @param numberOfBuckets The total number of buckets to be aggregated
     * @return A WrappedList containing the union of all buckets within scope
     */
    public static <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> sliding(T source, int itemsPerBucket, int numberOfBuckets) {
        CountReset tumble = new CountReset(source, itemsPerBucket);
        final SlidingCollectionAggregator slidingAggregator = SepContext.service().addOrReuse(new SlidingCollectionAggregator<>(tumble, (WrappedCollection) source, numberOfBuckets));
        return slidingAggregator.getTargetCollection();
    }

    public static <S> WrappedList<S> sliding(Class<S> sourceClass, int itemsPerBucket, int numberOfBuckets) {
        return sliding(EventSelect.select(sourceClass).collect(), itemsPerBucket, numberOfBuckets);
    }

    public static <S, T extends GroupBy<S>> GroupBy<S> sliding(T source, int itemsPerBucket, int numberOfBuckets) {
        CountReset tumble = new CountReset(source, itemsPerBucket);
        final SlidingGroupByAggregator<S, T> groupByAggregator = SepContext.service().addOrReuse(new SlidingGroupByAggregator<>(tumble,  source, numberOfBuckets));
        return groupByAggregator;
    }
    /**
     * Creates an aggregated collection of data points as a WrappedList. The user specifies the time duration of a
     * bucket and the total number of buckets. The WrappedList is published at the bucket intervals.
     *
     * @param <S> The type of each data point
     * @param <T> The type of collection to be aggregated
     * @param source The source collection
     * @param time Duration of a bucket
     * @param numberOfBuckets The total number of buckets to be aggregated
     * @return A WrappedList containing the union of all buckets within scope
     */
    public static <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> sliding(T source, Duration time, int numberOfBuckets) {
        final TimeReset timeReset = new TimeReset(source, time.getMillis(), null);
        final SlidingCollectionAggregator slidingAggregator = SepContext.service().addOrReuse(new SlidingCollectionAggregator(source, source, numberOfBuckets));
        slidingAggregator.setTimeReset(timeReset);
        return slidingAggregator.getTargetCollection();
    }

    public static <S> WrappedList<S> sliding(Class<S> sourceClass, Duration time, int numberOfBuckets) {
        return sliding(EventSelect.select(sourceClass).collect(), time, numberOfBuckets);
    }

    public static <S, T extends GroupBy<S>> GroupBy<S> sliding(T source,  Duration time, int numberOfBuckets) {
        final TimeReset timeReset = new TimeReset(source, time.getMillis(), null);
        final SlidingGroupByAggregator<S, T> groupByAggregator = SepContext.service().addOrReuse(new SlidingGroupByAggregator<>(source,  source, numberOfBuckets));
        groupByAggregator.setTimeReset(timeReset);
        return groupByAggregator;
    }
    
    // ==================================================
    // TUMBLE FUNCTIONS - REDUCING
    // ==================================================
    /**
     * Tumble to a single value applying a function to a Wrapper. Window size is based on count
     *
     * @param <R>
     * @param <S>
     * @param <T>
     * @param source
     * @param mapper
     * @param itemsPerBucket
     * @return
     */
    public static <R, S, T extends Wrapper<S>> Wrapper<R> tumble(T source, SerializableFunction<? extends S, R> mapper, int itemsPerBucket) {
        int numberOfBuckets = 1;
        return sliding(source, mapper, itemsPerBucket, numberOfBuckets);
    }

    /**
     * Tumble to a single value applying a function to a stream of events select from Class type. Window size is based
     * on count
     *
     * @param <R>
     * @param <S>
     * @param sourceClass
     * @param mapper
     * @param itemsPerBucket
     * @return
     */
    public static <R, S> Wrapper<R> tumble(Class<S> sourceClass, SerializableFunction<? extends S, R> mapper, int itemsPerBucket) {
        return tumble(EventSelect.select(sourceClass), mapper, itemsPerBucket);
    }

    /**
     * Tumble to a single value applying a function to a Wrapper. Window size is based on time period
     *
     * @param <R>
     * @param <S>
     * @param <T>
     * @param source
     * @param mapper
     * @param time
     * @return
     */
    public static <R, S, T extends Wrapper<S>> Wrapper<R> tumble(T source, SerializableFunction<? extends S, R> mapper, Duration time) {
        return sliding(source, mapper, time, 1);
    }

    /**
     * Tumble to a single value applying a function to a stream of events select from Class type. Window size is based
     * on time period
     *
     * @param <R>
     * @param <S>
     * @param sourceClass
     * @param mapper
     * @param time
     * @return
     */
    public static <R, S> Wrapper<R> tumble(Class<S> sourceClass, SerializableFunction<? extends S, R> mapper, Duration time) {
        return tumble(EventSelect.select(sourceClass), mapper, time);
    }

    // ==================================================
    // TUMBLE FUNCTIONS - COLLECTING
    // ================================================== 
    /**
     * A tumble window applied to a Wrapped collection stream and aggregated into a collection. The window size is based on count
     * of received collections. 
     * 
     * @param <S>
     * @param <T>
     * @param source
     * @param itemsPerBucket
     * @return
     */
    public static <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> tumble(T source, int itemsPerBucket) {
        return sliding(source, itemsPerBucket, 1);
    }

    
    public static <S, T extends GroupBy<S>> GroupBy<S> tumble(T source, int itemsPerBucket) {
        return sliding(source, itemsPerBucket, 1);
    } 
    
    /**
     * A tumble window applied to a stream of events selected from Class type and aggregated into a collection. The
     * window size is based on count
     *
     * @param <S>
     * @param sourceClass
     * @param itemsPerBucket
     * @return
     */
    public static <S> WrappedList<S> tumble(Class<S> sourceClass, int itemsPerBucket) {
        return tumble(EventSelect.select(sourceClass).collect(), itemsPerBucket);
    }

    /**
     * A tumble window applied to a Wrapper stream and aggregated into a collection. The window size is based on time
     * @param <S>
     * @param <T>
     * @param source
     * @param time
     * @return 
     */
    public static <S, T extends WrappedCollection<S, ?, ?>> WrappedList<S> tumble(T source, Duration time){
        return sliding(source, time, 1);
    }
    
    public static <S, T extends GroupBy<S>> GroupBy<S> tumble(T source, Duration time) {
        return sliding(source, time, 1);
    } 
    
    /**
     * A tumble window applied to a stream of events selected from Class type and aggregated into a collection. The window size is based on time
     * @param <S>
     * @param sourceClass
     * @param time
     * @return 
     */
    public static <S> WrappedList<S> tumble(Class<S> sourceClass, Duration time) {
        return tumble( EventSelect.select(sourceClass).collect(), time);
    }  
    
    // ==================================================
    // TUMBLE FUNCTIONS - INCREMENTAL
    // ================================================== 
    public static <S extends WrappedCollection> Wrapper<S> tumbleIncremental(S source, int count) {
        return notifierOverride(source, new CountReset(source, count));
    }

    public static <S, T extends Wrapper<S>> Wrapper<S> tumbleIncremental(T source, int count) {
        return notifierOverride(source, new CountReset(source, count));
    }

    public static <S, T extends GroupBy<S>> Wrapper<T> tumbleIncremental(T source, int count) {
        CountReset countBucketNotifier = new CountReset(source, count);
        return notifierOverride(source, countBucketNotifier);
    }

    public static <S extends WrappedCollection> Wrapper<S> tumbleIncremental(S source, Duration time) {
        return notifierOverride(source, new TimeReset(source, time.getMillis(), null));
    }

    public static <S, T extends Wrapper<S>> Wrapper<S> tumbleIncremental(T source, Duration time) {
        return notifierOverride(source, new TimeReset(source, time.getMillis(), null));
    }

    public static <S, T extends GroupBy<S>> Wrapper<T> tumbleIncremental(T source, Duration time) {
        return notifierOverride(source, new TimeReset(source, time.getMillis(), null));
    }

}
