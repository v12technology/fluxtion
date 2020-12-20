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
package com.fluxtion.ext.streaming.api;

import com.fluxtion.api.SepContext;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.window.WindowBuildOperations;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T>
 */
@Slf4j
public class ArrayListWrappedCollection<T> implements WrappedList<T> {

    private List<T> unmodifiableCollection;
    private final Wrapper<T> wrappedSource;
    @NoEventReference
    private Comparator comparator;
    private SerializableFunction<T, ? extends Comparable> comparingFunction;
    private List<T> collection;
    private Object resetNotifier;
    private boolean reset;
    private boolean reversed;

    public ArrayListWrappedCollection() {
        this(null);
    }

    public ArrayListWrappedCollection(Wrapper<T> wrappedSource) {
        this.wrappedSource = wrappedSource;
        reversed = false;
        init();
    }

    @Override
    public void reset() {
        init();
    }

    @OnParentUpdate("resetNotifier")
    public void resetNotification(Object resetNotifier) {
        collection.clear();
        reset = true;
    }

    @Override
    public WrappedList<T> resetNotifier(Object resetNotifier) {
        this.resetNotifier = resetNotifier;
        return this;
    }

    public Object getResetNotifier() {
        return resetNotifier;
    }

    public void setResetNotifier(Object resetNotifier) {
        this.resetNotifier = resetNotifier;
    }

    @Initialise
    public final void init() {
        this.collection = new ArrayList<>();
        this.unmodifiableCollection = Collections.unmodifiableList(collection);
        if(comparingFunction!=null){
            comparator = new FunctionComparator();
            Comparator.comparing(comparingFunction);
        }
        if (reversed) {
            comparator = comparator.reversed();
            reversed = false;
        }
    }

    @Override
    public WrappedList<T> reverse() {
        reversed = !reversed;
        return this;
    }

    public void sort() {
        if (comparator != null) {
            log.debug("sorting");
            this.collection.sort(comparator);
        }else{
            log.debug("no sorting - comparator is null");
        }
    }

    @Override
    public WrappedList<T> top(int n) {
        return SepContext.service().addOrReuse(new SubList<>(this, 0, n));
    }

    @Override
    public WrappedList<T> last(int n) {
        return SepContext.service().addOrReuse(new SubList<>(this, -n, 0));
    }

    @Override
    public WrappedList<T> skip(int n) {
        return SepContext.service().addOrReuse(new SubList<>(this, n, -n));
    }

    @OnEvent
    public boolean updated() {
        if (!reset && wrappedSource != null) {
            final T newItem = wrappedSource.event();
            addItem(newItem);
        }
        reset = false;
        return true;
    }

    @Override
    public WrappedList<T> sliding(int itemsPerBucket, int numberOfBuckets){
        WrappedList<T> sliding = WindowBuildOperations.service().sliding(self(), itemsPerBucket, numberOfBuckets);
        return sliding;
    }
    
    @Override
    public WrappedList<T> sliding(Duration timePerBucket, int numberOfBuckets) {
        WrappedList<T> sliding = WindowBuildOperations.service().sliding(self(), timePerBucket, numberOfBuckets);
        return sliding;
    }

    @Override
    public WrappedList<T> tumbling(Duration time) {
        WrappedList<T> sliding = WindowBuildOperations.service().tumbling(self(), time);
        return sliding;
    }

    @Override
    public WrappedList<T> tumbling(int itemCount) {
        WrappedList<T> sliding = WindowBuildOperations.service().tumbling(self(), itemCount);
        return sliding;
    }
    
    @Override
    public void combine(WrappedList<T> other) {
        List<T> otherCollection = other.collection();
        if (otherCollection == null) {
            return;
        }
        if (otherCollection instanceof List) {
            List<T> list = (List) otherCollection;
            for (int i = 0; i < list.size(); i++) {
                this.addItem(list.get(i));
            }
        } else {
            otherCollection.forEach(this::addItem);
        }
    }

    @Override
    public void deduct(WrappedList<T> other) {
        List<T> collection1 = other.collection();
        for (int i = 0; i < collection1.size(); i++) {
            T get = collection1.get(i);
            this.collection.remove(get);
        }
    }

    public ArrayListWrappedCollection<T> addItem(final T newItem) {
        if (comparator != null) {
            int index = Collections.binarySearch(collection, newItem, comparator);
            if (index < 0) {
                index = ~index;
            }
            collection.add(index, newItem);
        } else {
            collection.add(newItem);
        }
        return this;
    }

    public ArrayListWrappedCollection<T> removeItem(final T newItem) {
        this.collection.remove(newItem);
        return this;
    }

    @Override
    public WrappedList<T> comparator(Comparator comparator) {
        setComparator(SepContext.service().addOrReuse(comparator));
        return this;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public Comparator getComparator() {
        return comparator;
    }

    public boolean isReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public WrappedList<T> comparing(SerializableFunction comparingFunction) {
        this.comparingFunction = comparingFunction;
        return this;
    }

    public SerializableFunction<T, ?> getComparingFunction() {
        return comparingFunction;
    }

    @Override
    public List<T> collection() {
        return unmodifiableCollection;
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return collection().subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return "ArrayListWrappedCollection{" + "collection=" + collection + '}';
    }

    
    private class FunctionComparator implements Comparator<T>{

        @Override
        public int compare(T o1, T o2) {
            return ((Comparable)comparingFunction.apply(o1)).compareTo(comparingFunction.apply(o2));
        }

    }
}
