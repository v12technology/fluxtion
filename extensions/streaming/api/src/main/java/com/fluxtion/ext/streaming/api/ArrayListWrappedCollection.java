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
import com.fluxtion.api.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T>
 */
public class ArrayListWrappedCollection<T> implements WrappedList<T> {


    private List<T> unmodifiableCollection;
    private final Wrapper<T> wrappedSource;
    @NoEventReference
    private Comparator comparator;
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
    public WrappedList<T> resetNotifier(Object resetNotifier){
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
        if(reversed){
            comparator = comparator.reversed();
            reversed = false;
        }
    }

    @Override
    public WrappedList<T> reverse() {
        reversed = !reversed;
        return this;
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
        if(!reset && wrappedSource!=null){
            final T newItem = wrappedSource.event();
            addItem(newItem);
        }
        reset = false;
        return true;
    }


    @Override
    public void combine(Stateful<? extends T> other) {
        final WrappedList<T> otherList = (WrappedList<T>)other;
        List<T> collection1 = otherList.collection();
        for (int i = 0; i < collection1.size(); i++) {
            T get = collection1.get(i);
            this.addItem(get);
        }
    }

    @Override
    public void deduct(Stateful<? extends T> other) {
        this.collection.removeAll(((WrappedList<T>)other).collection());
    }

    
    public ArrayListWrappedCollection<T> addItem(final T newItem) {
        if (collection.contains(newItem)) {
            return this;
        }
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
    public < I extends Integer> void comparing(SerializableBiFunction<T, T, I> func) {
        System.out.println("SETTING COMPARATOR STATIC FUNCTION " + func.method().getParameters()[0].getType());
    }

    @Override
    public <R extends Comparable> void comparing(SerializableFunction<T, R> in) {
        System.out.println("SETTING COMPARATOR USING PROPERTY");
    }

    @Override
    public List<T> collection() {
        return unmodifiableCollection;
    }

    public List<T> subList(int fromIndex, int toIndex) {
        return collection().subList(fromIndex, toIndex);
    }

}
