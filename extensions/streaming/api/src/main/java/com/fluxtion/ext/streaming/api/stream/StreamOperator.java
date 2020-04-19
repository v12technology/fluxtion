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
package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.FilterWrapper;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.numeric.NumericFunctionStateless;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.ServiceLoader;

/**
 * An interface defining stream operations on a node in a SEP graph. The
 * {@link ServiceLoader} mechanism is used to load an implementation at runtime
 * for the StreamOperator interface.
 *
 * @author V12 Technology Ltd.
 */
public interface StreamOperator {

    default <T> Wrapper<T> select(Class<T> eventClazz){
        return null;
    }

    default <S, T> FilterWrapper<T> filter(SerializableFunction<S, Boolean> filter,
            Wrapper<T> source, Method accessor, boolean cast) {
        return (FilterWrapper<T>) source;
    }

    default <T> FilterWrapper<T> filter(SerializableFunction<? extends T, Boolean> filter,
            Wrapper<T> source, boolean cast) {
        return (FilterWrapper<T>) source;
    }

    default <T, S extends Number, F extends NumericFunctionStateless, R extends Number> GroupBy<R> group(Wrapper<T> source,
            SerializableFunction<T, S> key, Class<F> functionClass){
        return null;
    }

    default <K, T, S extends Number, F extends NumericFunctionStateless, R extends Number> GroupBy<R> group(Wrapper<T> source,
            SerializableFunction<T, K> key,
            SerializableFunction<T, S> supplier,
            Class<F> functionClass){
        return null;
    }

    /**
     * Streams the return of a method as a wrapped instance.
     * @param <T>
     * @param <R>
     * @param mapper
     * @param source
     * @return 
     */
    default <T, R> Wrapper<R> get(SerializableFunction<T, R> mapper, Wrapper<T> source) {
        return null;
    }
    
    default <T, R> Wrapper<R> map(SerializableFunction<T, R> mapper, Wrapper<T> source, boolean cast) {
        return null;
    }

    default <T, R> Wrapper<R> map(SerializableFunction<T, R> mapper, Wrapper<T> source, Method accessor, boolean cast) {
        return null;
    }

    default  <R, S, U> Wrapper<R> map(SerializableBiFunction<? extends U, ? extends S, R> mapper,
            Argument<? extends U> arg1,
            Argument<? extends S> arg2){
        return null;
    }

    default <F, R> Wrapper<R> map(F mapper, Method mappingMethod, Argument... args){
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
     * Attaches an event notification instance to the current stream node and
     * merges notifications from stream and the added notifier.When the notifier
     * updates all the child nodes of this stream node will be on the execution
     * path and invoked following normal SEP rules.The existing execution path
     * will be unaltered if either the parent wrapped node or the eventNotifier
     * updates then the execution path will progress.
     *
     * @param <T>
     * @param source
     * @param notifier
     * @return
     */
    default <T> Wrapper<T> notiferMerge(Wrapper<T> source, Object notifier) {
        return source;
    }

    /**
     * Attaches an event notification instance to the current stream node,
     * overriding the execution path of the current stream.Only when the
     * notifier updates will the child nodes of this stream node be on the
     * execution path.
     *
     * @param <T>
     * @param source
     * @param notifier external event notifier
     * @return
     */
    default <T> Wrapper<T> notifierOverride(Wrapper<T> source, Object notifier) {
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

    
     default <T, I extends Integer> Comparator<T> comparing(SerializableBiFunction<T, T, I> func){
         return null;
     }
            
//    default <T, S extends T, R extends T, I extends Integer> Comparator<T> comparing(Class<T> clazz, SerializableBiFunction<S, R, I> func){
//        return null;
//    }

    static StreamOperator service() {
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

    static <I> void standardOut(I out) {
        System.out.println(out);
    }

    class ConsoleLog {

        private final Object source;
        private Wrapper wrapped;
        private final String prefix;
        private boolean isWrapper;
        private String methodSupplier;
        private Method method;

        public ConsoleLog(Object source, String prefix) {
            this.source = source;
            this.prefix = prefix;
        }

        public ConsoleLog(Object source) {
            this(source, "");
        }

        public <T, S> void suppliers(SerializableFunction<T, S>... supplier) {
            if (supplier.length > 0) {
                methodSupplier = supplier[0].method().getName();
            }
        }

        public String getMethodSupplier() {
            return methodSupplier;
        }

        public void setMethodSupplier(String methodSupplier) {
            this.methodSupplier = methodSupplier;
        }

        @OnEvent
        public boolean log() {
            Object src = isWrapper ? wrapped.event() : source;
            if (method != null) {
                try {
                    System.out.println(prefix + method.invoke(src));
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    System.out.println(prefix + "N/A");
                }
            } else {
                System.out.println(prefix + src.toString());
            }
            return false;
        }

        @Initialise
        public void init() {
            if (source instanceof Wrapper) {
                wrapped = (Wrapper) source;
                isWrapper = true;
            }
            try {
                if (isWrapper) {
                    method = wrapped.eventClass().getMethod(methodSupplier);
                } else {
                    method = source.getClass().getMethod(methodSupplier);
                }
            } catch (Exception e) {
                method = null;
            }
        }

    }
}
