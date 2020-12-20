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
 * <http://www.mongodb.com/licensing/server-side-default-license>.
 */
package com.fluxtion.ext.streaming.api;

import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.stream.StreamOperator;
import com.fluxtion.ext.streaming.api.window.WindowBuildOperations;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T> The type held in this collection
 * @param <U> The underlying collection type
 * @param <C> The type of the subclass of WrappedCollection
 */
public interface WrappedCollection<T, U extends Collection<T>, C extends WrappedCollection<T, U, C>>
    extends Stateful<C>, WrapperBase<Collection<T>, C> {

    default <R extends Comparable> WrappedList<T> comparing(SerializableFunction<T, R> in) {
        throw new UnsupportedOperationException("comparing not implemented in WrappedCollection");
    }

    default WrappedList<T> comparator(Comparator comparator) {
        return null;
    }

    U collection();
    
    default WrappedCollection<T, U, C>  sliding(int itemsPerBucket, int numberOfBuckets){
        return null;
    }
    
    default WrappedCollection<T, U, C>  sliding(Duration timePerBucket, int numberOfBuckets){
        return null;
    }

    /**
     * Collects the events into a WrappedList using a time based tumbling window strategy. 
     * @param time duration of the tumbling window
     * @return The collection of events in sliding window
     */
    default WrappedCollection<T, U, C> tumbling(Duration time){
        return null; 
    }    
    
     /**
     * Collects the events into a WrappedList using a time based tumbling window strategy. 
     * @param itemCount number of items in the tumbling window
     * @return The collection of events in sliding window
     */
    default WrappedCollection<T, U, C> tumbling(int itemCount){
        return null; 
    }
    
    default <R> Wrapper<R> map(SerializableFunction<U, R> mapper) {
        return StreamOperator.service().map(mapper, asWrapper(), true);
    }

    default Wrapper<U> asWrapper() {
        return StreamOperator.service().streamInstance(this::collection);
    }

    WrappedCollection<T, U, C> resetNotifier(Object resetNotifier);

    @Override
    default U event() {
        return collection();
    }

    @Override
    default Class<Collection<T>> eventClass() {
        return (Class<Collection<T>>) collection().getClass();
    }

    default int size() {
        return collection().size();
    }

    default boolean isEmpty() {
        return collection().isEmpty();
    }

    default boolean contains(T o) {
        return collection().contains(o);
    }

    default Iterator<T> iterator() {
        return collection().iterator();
    }

    default Stream<T> stream() {
        return collection().stream();
    }

    default void forEach(Consumer<? super T> action) {
        collection().forEach(action);
    }

}
