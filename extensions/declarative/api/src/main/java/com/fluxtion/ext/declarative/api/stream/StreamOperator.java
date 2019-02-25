/*
 * Copyright (C) 2019 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.declarative.api.stream;

import com.fluxtion.api.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.declarative.api.Wrapper;
import java.lang.reflect.Method;
import java.util.ServiceLoader;

/**
 * An interface defining stream operations on a node in a SEP graph. The
 * {@link ServiceLoader} mechanism is used to load an implementation at runtime
 * for the StreamOperator interface.
 *
 * @author V12 Technology Ltd.
 */
public interface StreamOperator {

    default <S, T> Wrapper<T> filter(SerializableFunction<S, Boolean> filter,
            Wrapper<T> source, Method accessor, boolean cast) {
        return source;
    }

    default <T> Wrapper<T> filter(SerializableFunction<T, Boolean> filter,
            Wrapper<T> source, boolean cast) {
        return source;
    }

    default <T, R> Wrapper<R> map(SerializableFunction<T, R> mapper, Wrapper<T> source, boolean cast) {
        return null;
    }

    default <T, R> Wrapper<R> map(SerializableFunction<T, R> mapper, Wrapper<T> source, Method accessor, boolean cast) {
        return null;
    }

    /**
     * push data from the wrapper to the consumer
     *
     * @param <T>
     * @param <R>
     * @param source
     * @param accessor
     * @param consumer
     */
    default <T, R> void push(Wrapper<T> source, Method accessor, SerializableConsumer<R> consumer) {
    }

    /**
     * Wrap a node with a {@link Wrapper} allowing stream operations to be
     * applied to the node.
     *
     * @param <T>
     * @param node
     * @return
     */
    default <T> Wrapper<T> stream(T node) {
        return null;
    }
    
    public static <T> Wrapper<T> wrap(T node){
        return service().stream(node);
    }

    /**
     * Supply the wrapper to a consumer when the wrapper is on the execution
     * path
     *
     * @param <T>
     * @param <S>
     * @param consumer
     * @param source
     * @param consumerId - id for node in SEP, can be null for autonaming
     * @return
     */
    default <T, S extends T> Wrapper<T> forEach(SerializableConsumer<S> consumer, Wrapper<T> source, String consumerId) {
        return source;
    }

    /**
     * adds the wrapper to an execution path
     *
     * @param <T>
     * @param source
     * @param notifier
     * @return
     */
    default <T> Wrapper<T> eventNotifer(Wrapper<T> source, Object notifier) {
        return source;
    }
    
    default <T> Wrapper<T> eventFilter(Wrapper<T> source, Object notifier) {
        return source;
    }

    /**
     * name a StreamOperator node in the generated SEP.
     *
     * @param <T>
     * @param node
     * @param name
     * @return
     */
    default <T> T nodeId(T node, String name) {
        return node;
    }

    public static StreamOperator service() {
        ServiceLoader<StreamOperator> load = ServiceLoader.load(StreamOperator.class);
        if (load.iterator().hasNext()) {
            return load.iterator().next();
        } else {
            load = ServiceLoader.load(StreamOperator.class, StreamOperator.class.getClassLoader());
            if (load.iterator().hasNext()) {
                return load.iterator().next();
            } else {
                return new StreamOperator() {
                };
            }
        }
    }

    public static <I> void standardOut(I out) {
        System.out.println(out);
    }

    public static class PrefixToConsole {

        private final String prefix;

        public PrefixToConsole(String prefix) {
            this.prefix = prefix + " ";
        }

        public <I> void standardOut(I out) {
            System.out.println(prefix + out);
        }
    }
}
