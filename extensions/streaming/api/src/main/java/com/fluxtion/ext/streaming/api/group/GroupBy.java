/*
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.ext.streaming.api.group;

import static com.fluxtion.api.SepContext.service;
import com.fluxtion.ext.streaming.api.ArrayListWrappedCollection;
import com.fluxtion.ext.streaming.api.Duration;
import com.fluxtion.ext.streaming.api.WrappedCollection;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.stream.StreamOperator;
import com.fluxtion.ext.streaming.api.test.BooleanFilter;
import com.fluxtion.ext.streaming.api.window.WindowBuildOperations;
import java.util.Collection;
import java.util.Map;

/**
 * Runtime interface for GroupBy instances generated by SEP.
 *
 * @param <T> he target type of the group
 * @author greg
 */
public interface GroupBy<T> extends WrappedCollection<T, Collection<T>, GroupBy<T>> {

    <K> T value(K key);

    <V extends Wrapper<T>> Map<?, V> getMap();

    @Override
    default GroupBy<T> id(String id) {
        return StreamOperator.service().nodeId(this, id);
    }

    @Override
    default GroupBy<T> resetNotifier(Object resetNotifier) {
        return this;
    }

    @Override
    default GroupBy<T> sliding(int itemsPerBucket, int numberOfBuckets) {
        GroupBy<T> sliding = WindowBuildOperations.service().sliding(self(), itemsPerBucket, numberOfBuckets);
        return sliding;
    }

    @Override
    default GroupBy<T> sliding(Duration timePerBucket, int numberOfBuckets) {
        GroupBy<T> sliding = WindowBuildOperations.service().sliding(self(), timePerBucket, numberOfBuckets);
        return sliding;
    }

    @Override
    default GroupBy<T> tumbling(Duration time) {
        GroupBy<T> sliding = WindowBuildOperations.service().tumbling(self(), time);
        return sliding;
    }

    @Override
    default GroupBy<T> tumbling(int itemCount) {
        GroupBy<T> sliding = WindowBuildOperations.service().tumbling(self(), itemCount);
        return sliding;
    }

    @Override
    public default void reset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * The last record that was updated as a wrapped node
     *
     * @return the wrapped node
     */
    T record();

    /**
     * The type of the wrapped node
     *
     * @return wrapped node class
     */
    Class<T> recordClass();

    /**
     * Set the target collection for storing results of the groupBy operations. If no target is supplied then an
     * internal
     * list is used.
     *
     * @param wrappedList
     */
    default void setTargetCollecion(ArrayListWrappedCollection<T> wrappedList) {
    }

    GroupBy<T> newInstance();

    /**
     * Attaches an event notification instance to the current stream node,
     * overriding the execution path of the current stream.Only when the
     * notifier updates will the child nodes of this stream node be on the
     * execution path.
     *
     * @param notifier external event notifier
     * @return
     */
    default Wrapper<GroupBy<T>> notifierOverride(Object notifier) {
        BooleanFilter<GroupBy<T>> filter = new BooleanFilter<>(service().addOrReuse(self()), service().addOrReuse(notifier));
        service().addOrReuse(filter);
        return filter;
    }
}
