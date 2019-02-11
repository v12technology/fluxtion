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
package com.fluxtion.ext.declarative.api;

import com.fluxtion.ext.declarative.api.stream.StreamOperator;
import com.fluxtion.api.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;

/**
 * A wrapper class that holds a reference to a node in the SEP. Any node in SEP
 * can be a source of a stream of values.<p>
 * Stream operations are provided to filter and map the underlying wrapped type.
 *
 * @author Greg Higgins
 * @param <T>
 */
public interface Wrapper<T> {

    /**
     * The wrapped node
     *
     * @return the wrapped node
     */
    T event();

    /**
     * The type of the wrapped node
     *
     * @return wrapped node class
     */
    Class<T> eventClass();

    default Wrapper<T> filter(SerializableFunction<T, Boolean> filter) {
        return (Wrapper<T>) StreamOperator.service().filter(filter, this, true);
    }

    default <S> Wrapper<T> filter(SerializableFunction<T, S> supplier, SerializableFunction<S, Boolean> filter) {
        return (Wrapper<T>) StreamOperator.service().filter(filter, this, supplier.method(), true);
    }

    default <R> Wrapper<R> map(SerializableFunction<T, R> mapper) {
        return (Wrapper<R>) StreamOperator.service().map((SerializableFunction) mapper, this, true);
    }

    default <R, S> Wrapper<R> map(SerializableFunction<S, R> mapper, SerializableFunction<T, S> supplier) {
        return (Wrapper<R>) StreamOperator.service().map((SerializableFunction) mapper, this, supplier.method(), true);
    }

    default Wrapper<T> forEach(SerializableConsumer<T> consumer) {
        return (Wrapper<T>) StreamOperator.service().forEach(consumer, this);
    }

    default Wrapper<T> console(String prefix) {
        StreamOperator.PrefixToConsole console = new StreamOperator.PrefixToConsole(prefix);
        return StreamOperator.service().forEach(console::standardOut, this);
    }

    default Wrapper<T> resetNotifier(Object resetNotifier) {
        return this;
    }

}
