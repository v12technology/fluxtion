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
import com.fluxtion.ext.streaming.api.stream.StreamOperator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T> The type held in this collection
 */
public interface WrappedCollection<T> extends Stateful{

    default <I extends Integer> void comparing(LambdaReflection.SerializableBiFunction<T, T, I> func) {
        throw new UnsupportedOperationException("comparing not implemented in WrappedCollection");
    }

    default <R extends Comparable> void comparing(LambdaReflection.SerializableFunction<T, R> in) {
        throw new UnsupportedOperationException("comparing not implemented in WrappedCollection");
    }

    default WrappedList<T> comparator(Comparator comparator) {
        return null;
    }

    default WrappedCollection<T> id(String id) {
        return StreamOperator.service().nodeId(this, id);
    }

    Collection<T> collection();

    WrappedCollection<T> resetNotifier(Object resetNotifier);
        
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
