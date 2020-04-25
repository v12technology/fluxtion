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

    public static <R, S, T extends Wrapper<S>> Wrapper<R> sliding(T source, SerializableFunction<S, R> supplier, int bucketSize, int bucketCount) {
        Object mappingInstance = null;
        if (supplier.captured().length == 0) {
            throw new RuntimeException("mapping function must be an instance method");
        } else {
            mappingInstance = supplier.captured()[0];
        }
        Class clazz = mappingInstance.getClass();
        Wrapper<R> tumble = tumble(source.map(supplier), bucketSize);
        if (StatefulNumber.class.isAssignableFrom(clazz)) {
            return (Wrapper<R>) SepContext.service().addOrReuse(new SlidingNumberAggregator(tumble, (StatefulNumber) mappingInstance, bucketCount));
        } else if (Stateful.class.isAssignableFrom(clazz)) {
            Class<R> returnType = (Class<R>) supplier.method().getReturnType();
            return SepContext.service().addOrReuse(new SlidingAggregator<>(tumble, returnType, (Stateful) mappingInstance, bucketCount));
        } else {
            throw new RuntimeException("unsupported type for aggregation class:" + clazz);
        }
    }

    public static <S, T extends WrappedCollection<S, ?, ?>>  WrappedList<S> sliding(T source, int bucketSize, int bucketCount) {
        Wrapper<T> tumble = tumble(source, bucketSize);
        Class<WrappedCollection> returnType = WrappedCollection.class;
        final SlidingCollectionAggregator slidingAggregator = SepContext.service().addOrReuse(new SlidingCollectionAggregator<>(tumble, returnType, (WrappedCollection)source, bucketCount));
        return slidingAggregator.getTargetCollection();
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
