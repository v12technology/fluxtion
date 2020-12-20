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

import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.PushReference;
import com.fluxtion.api.annotations.SepNode;
import com.fluxtion.ext.streaming.api.ArrayListWrappedCollection;
import com.fluxtion.ext.streaming.api.WrappedList;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import lombok.Data;

/**
 * Applies a sliding window to a {@link GroupBy}
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T>
 */
@Data
public class SlidingGroupByAggregator<S, T extends GroupBy<S>> implements GroupBy<S> {

    @SepNode
    @PushReference
    private ArrayListWrappedCollection<S> collection;
    @SepNode
    private final Object notifier;
    @NoEventReference
    @SepNode
    private final T source;
    @NoEventReference
    @SepNode
    private TimeReset timeReset;
    private T aggregator;
    private transient T primingAggregator;
    private final int numberOfBuckets;
    private ArrayDeque<T> deque;
    private int expiredCount;
    private boolean firstExpiry;

    public SlidingGroupByAggregator(Object notifier, T source, int numberOfBuckets) {
        try {
            this.notifier = notifier;
            this.source = source;
            this.numberOfBuckets = numberOfBuckets;
            collection = new ArrayListWrappedCollection<>();
            //TODO pass in a factory Supplier
            aggregator = (T) source.getClass().getDeclaredConstructor().newInstance();
            aggregator.setTargetCollecion(collection);
        } catch (Exception ex) {
            throw new RuntimeException("Cannot instantiate SlidingGroupByAggregator", ex);
        }
    }
    
    @OnEvent
    public boolean aggregate() {
        int expiredBuckets = timeReset == null ? 1 : timeReset.getWindowsExpired();
        if (expiredBuckets == 0) {
            return false;
        }
        T popped1 = deque.poll();
        aggregator.deduct(popped1);
        expiredCount += expiredBuckets;
        firstExpiry = firstExpiry | expiredCount >= numberOfBuckets | expiredBuckets >= numberOfBuckets;
        for (int i = 1; i < expiredBuckets; i++) {
            T popped2 = deque.poll();
            aggregator.deduct(popped2);
            popped2.reset();
            deque.add(popped2);
        }
        popped1.reset();
        popped1.combine(source);
        deque.add(popped1);
        //add
        aggregator.combine(source);
        source.reset();
        return firstExpiry;
    }

    public void init() {
        firstExpiry = false;
        try {
            deque = new ArrayDeque<>(numberOfBuckets);
            aggregator.setTargetCollecion(collection);
            aggregator.reset();
            for (int i = 0; i < numberOfBuckets; i++) {
                final T function = (T) source.getClass().getDeclaredConstructor().newInstance();
                function.reset();
                deque.add(function);
            }
        } catch (Exception ex) {
            throw new RuntimeException("missing default constructor for:" + source.getClass());
        }
    }

    //Overriden methods for the encapsulated aggregating groupBy
    @Override
    public <K> S value(K key) {
        return aggregator.value(key);
    }

    @Override
    public <V extends Wrapper<S>> Map<?, V> getMap() {
        return aggregator.getMap();
    }

    @Override
    public S record() {
        return aggregator.record();
    }

    @Override
    public Class<S> recordClass() {
        return aggregator.recordClass();
    }

    @Override
    public Collection<S> collection() {
        //TODO add logic to return empty set until primed
        return aggregator.collection();
    }

    @Override
    public WrappedList<S> comparator(Comparator comparator) {
        return aggregator.comparator(comparator);
    }

    @Override
    public void reset() {
        init();
    }

    //TODO for chaining 
    @Override
    public void deduct(GroupBy<S> other) {
        GroupBy.super.deduct(other); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void combine(GroupBy<S> other) {
        GroupBy.super.combine(other); //To change body of generated methods, choose Tools | Templates.
    }

}
