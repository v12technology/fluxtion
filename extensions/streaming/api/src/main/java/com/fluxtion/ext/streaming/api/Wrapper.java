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
package com.fluxtion.ext.streaming.api;

import com.fluxtion.api.SepContext;
import com.fluxtion.api.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.stream.Argument;
import com.fluxtion.ext.streaming.api.stream.StreamOperator;
import com.fluxtion.ext.streaming.api.window.WindowBuildOperations;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

/**
 * A wrapper class that holds a reference to a node in the SEP. Any node in SEP
 * can be a source of a stream of values.<p>
 * Stream operations are provided to filter and map the underlying wrapped type.
 *
 * @author Greg Higgins
 * @param <T>
 */
public interface Wrapper<T> extends Stateful<T>{

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
    
    @Override
    default void reset(){}

    default <S> Argument<S> arg(SerializableFunction<T, S> supplier){
        return Argument.arg(this, supplier);
    }

    default <S> Argument<S> arg(){
        return Argument.arg(this);
    }
    
    /**
     * Set the default value for this instance. The default value will be set when any call to reset is made. Having a 
     * default value allows an instance to be used by its children before an event has been processed.
     * 
     * @param defaultValue the default value to use
     * @return 
     */
    default Wrapper<T> defaultVal(T defaultValue){
        return  StreamOperator.service().defaultVal(this, defaultValue);
    }
    
    default <S extends T> FilterWrapper<T> filter(SerializableFunction<S, Boolean> filter) {
        return StreamOperator.service().filter(filter, this, true);
    }

    default <S> FilterWrapper<T> filter(SerializableFunction<T, S> supplier, SerializableFunction<? extends S, Boolean> filter) {
        return StreamOperator.service().filter(filter, this, supplier.method(), true);
    }

    default <S> Wrapper<S> get(SerializableFunction<T, S> supplier) {
        return StreamOperator.service().get(supplier, this);
    }
    
    default  WrappedList<T> collect(){
       return  SepContext.service().add(new ArrayListWrappedCollection<>(this));
    }

    default <S extends Number, R extends Number> GroupBy<R> group(
            SerializableFunction<T, S> supplier,
            SerializableBiFunction<? super R, ? super R, ? extends R> functionClass) {
        return StreamOperator.service().group(this, supplier, functionClass);
    }
    
    default <K, S extends Number, R extends Number> GroupBy<R> group(
            SerializableFunction<T, K> key,
            SerializableFunction<T, S> supplier,
            SerializableBiFunction<? super R, ? super R, ? extends R> functionClass) {
        return StreamOperator.service().group(this, key, supplier, functionClass);
    }
    
    /**
     * Maps a value using the provided mapping function.The input is the
     * wrapped instance inside this {@link Wrapper}.
     *
     * @param <R> The return type of the mapping function
     * @param <S>
     * @param mapper the mapping function
     * @return A wrapped value containing the result of the mapping operation
     */
    default <R, S extends T> Wrapper<R> map(SerializableFunction<S, R> mapper) {
        return (Wrapper<R>) StreamOperator.service().map((SerializableFunction) mapper, this, true);
    }

    /**
     * Maps a value using the provided mapping function. The input is the return
     * value of the supplier function invoked on the wrapped instance.
     *
     * @param <R> The return type of the mapping function
     * @param <S> The input type required by the mapping function
     * @param mapper the mapping function
     * @param supplier
     * @return A wrapped value containing the result of the mapping operation
     */
    default <R, S> Wrapper<R> map(SerializableFunction<? extends S, R> mapper, SerializableFunction<T, S> supplier) {
        return StreamOperator.service().map((SerializableFunction) mapper, this, supplier.method(), true);
    }

    default <R, S, U> Wrapper<R> map(SerializableBiFunction<U, S, R> mapper, Argument<S> arg1, Argument<U> arg2) {
        return (Wrapper<R>) StreamOperator.service().map((SerializableBiFunction) mapper, arg1, arg2);
    }

    default <R, S, U> Wrapper<R> map(SerializableBiFunction<? extends U, ? extends S, R> mapper,
            SerializableFunction<T, S> supplier1, SerializableFunction<T, U> supplier2) {
        return (Wrapper<R>) StreamOperator.service().map((SerializableBiFunction) mapper, Argument.arg(this, supplier1), Argument.arg(this, supplier2));
    }

    default <R, S, U> Wrapper<R> map(SerializableBiFunction<? extends U, ? extends S, R> mapper, SerializableFunction<T, S> supplier, Argument<U> arg) {
        return (Wrapper<R>) StreamOperator.service().map((SerializableBiFunction) mapper, Argument.arg(this, supplier), arg);
    }

    default <R, S, U> Wrapper<R> map(SerializableBiFunction<? extends U, ? extends S, R> mapper, Argument<U> arg, SerializableFunction<T, S> supplier) {
        return (Wrapper<R>) StreamOperator.service().map((SerializableBiFunction) mapper, arg, Argument.arg(this, supplier));
    }

    default <R, S, U> Wrapper<R> map(SerializableBiFunction<? extends U, S, R> mapper, SerializableFunction<T, ? extends U> supplier, double arg) {
        return (Wrapper<R>) StreamOperator.service().map((SerializableBiFunction) mapper, Argument.arg(this, supplier), Argument.arg(arg));
    }

    default <R, S, U> Wrapper<R> map(SerializableBiFunction<? extends U, ? extends S, R> mapper, double arg, SerializableFunction<T, S> supplier) {
        return (Wrapper<R>) StreamOperator.service().map((SerializableBiFunction) mapper, Argument.arg(arg), Argument.arg(this, supplier));
    }

    /**
     * Maps a binary function using the wrapped instance as the first argument
     * to the binary function.
     *
     * @param <R> The result type of the mapping function
     * @param <S> The type of the supplied argument
     * @param <U> The input type for first argument to mapping function
     * @param <V> The input type for second argument to mapping function
     * @param mapper The mapping function
     * @param arg1 The second argument of the binary mapping function
     * @return
     */
    default <R, S, U extends T, V extends S> Wrapper<R> map(SerializableBiFunction<U, S, R> mapper, Argument<V> arg1) {
        return (Wrapper<R>) StreamOperator.service().map((SerializableBiFunction) mapper, Argument.arg(this), arg1);
    }

    default <R extends Number, S, U extends T, V extends S> Wrapper<R> map(SerializableBiFunction<U, S, R> mapper, double arg1) {
        return (Wrapper<R>) StreamOperator.service().map((SerializableBiFunction) mapper, Argument.arg(this), Argument.arg(arg1));
    }

    //windows reducing
    /**
     * Create a time based tumbling window aggregate result expiring after a duration has passed. The window combines the results of the supplied
     * function for all events that occur within the timed window
     * @param <R> the result type of the function
     * @param mapper The function to apply to each event in the window
     * @param time The duration of the window
     * @return A result that is updated at the window expiry
     */
    default <R> Wrapper<R> tumbling(SerializableFunction<T, R> mapper, Duration time) {
        return WindowBuildOperations.service().tumbling(this, mapper, time); 
    }
    
    /**
     * Create a count based tumbling window aggregate result expiring after receiving a number of events. The window combines the results of the supplied
     * function for all events that occur within the window
     * @param <R> the result type of the function
     * @param mapper The function to apply to each event in the window
     * @param itemCount the number of events in a window
     * @return A result that is updated at the window expiry
     */
    default <R> Wrapper<R> tumbling(SerializableFunction<T, R> mapper, int itemCount) {
        return WindowBuildOperations.service().tumbling(this, mapper, itemCount); 
    }
    
    /**
     * Create a time based sliding window aggregate result publishing after a duration has passed. The window combines the results of the supplied
     * function for all events that occur within the number of buckets. The total window time = time per bucket X number of buckets
     * @param <R> the result type of the function
     * @param mapper The function to apply to each event in the window
     * @param time The duration of a bucket
     * @param numberOfBuckets the number of buckets in the window 
     * @return A result that is updated at the window expiry
     */
    default <R> Wrapper<R> sliding(SerializableFunction<T, R> mapper, Duration time,  int numberOfBuckets) {
        return WindowBuildOperations.service().sliding(this, mapper, time, numberOfBuckets); 
    }
    
    /**
     * Create a count based sliding window aggregate result publishing after a count has passed. The window combines the results of the supplied
     * function for all events that occur within the number of buckets. The total window count = the number of events per bucket X number of buckets
     * @param <R> the result type of the function
     * @param mapper The function to apply to each event in the window
     * @param itemCountPerBuket the number of events per bucket
     * @param numberOfBuckets the number of buckets in the window 
     * @return A result that is updated at the window expiry
     */
    default <R> Wrapper<R> sliding(SerializableFunction<T, R> mapper, int itemCountPerBuket, int numberOfBuckets) {
        return WindowBuildOperations.service().sliding(this, mapper, itemCountPerBuket, numberOfBuckets); 
    }
    
    //window collecting
    /**
     * Collects the events into a WrappedList using a time based sliding window strategy. 
     * @param timePerBucket  time per bucket
     * @param numberOfBuckets number of buckets in sliding window
     * @return The collection of events in sliding window
     */
    default WrappedList<T> sliding(Duration timePerBucket, int numberOfBuckets){
        return WindowBuildOperations.service().sliding(collect(), timePerBucket, numberOfBuckets); 
    }    
    
    /**
     * Collects the events into a WrappedList using a count based sliding window strategy. 
     * @param itemCount the count of events per bucket
     * @param numberOfBuckets number of buckets in sliding window
     * @return The collection of events in sliding window
     */
    default WrappedList<T> sliding(int itemCount, int numberOfBuckets){
        return WindowBuildOperations.service().sliding(collect(), itemCount, numberOfBuckets); 
    }  
    
    /**
     * Collects the events into a WrappedList using a time based tumbling window strategy. 
     * @param time duration of the tumbling window
     * @return The collection of events in sliding window
     */
    default WrappedList<T> tumbling(Duration time){
        return WindowBuildOperations.service().tumbling(collect(), time); 
    }    
    
     /**
     * Collects the events into a WrappedList using a time based tumbling window strategy. 
     * @param itemCount number of items in the tumbling window
     * @return The collection of events in sliding window
     */
    default WrappedList<T> tumbling(int itemCount){
        return WindowBuildOperations.service().tumbling(collect(), itemCount); 
    }    
    
    /**
     * pushes a data item from the current node in the stream to any node.The
     * target node will become part of the same execution graph as the
     * source.<p>
     * The returned node is the current node in the stream.
     *
     * @param <R>
     * @param <S>
     * @param supplier
     * @param mapper
     * @return this
     */
    default <R, S extends R> Wrapper<T> push(SerializableFunction<T, S> supplier, SerializableConsumer<R> mapper) {
        StreamOperator.service().push(this, supplier.method(), mapper);
        return (Wrapper<T>) this;
    }

    default <R extends T> Wrapper<T> push(SerializableConsumer<R> mapper) {
        StreamOperator.service().push(this, null, mapper);
        return (Wrapper<T>) this;
    }

    /**
     * Registers a {@link Consumer} to operate on the current node when an event
     * wave is passing through this node. The consumer can perform any operation
     * on the node including mutations. This node, possibly mutated, is passed
     * as a reference to child nodes. No new nodes are created in the stream as
     * a side-effect of this processing.
     *
     * @param consumer {@link Consumer} of this node
     * @return The current node
     */
    default Wrapper<T> forEach(SerializableConsumer<T> consumer) {
        return (Wrapper<T>) StreamOperator.service().forEach(consumer, this, null);
    }

    default Wrapper<T> forEach(SerializableConsumer<T> consumer, String consumerId) {
        return (Wrapper<T>) StreamOperator.service().forEach(consumer, this, consumerId);
    }

    LongAdder counter = new LongAdder();

    /**
     * dump this node to log, prefixed with the supplied
 message.{@link Object#toString()} will be invoked on the node instance.
     *
     * @param prefix String prefix for the log message
     * @param supplier
     * @return The current node
     */
    default Wrapper<T> log(String prefix, SerializableFunction<T, ?>... supplier) {
        if (!prefix.contains("{}")) {
            prefix += " {}";
        }
        return (Wrapper<T>) StreamOperator.service().log(this, prefix, supplier);
    }
    
    default Wrapper<T> log() {
        return log("");
    }

    /**
     * Attaches an event notification instance to the current stream node. When
     * the notifier updates all the child nodes of this stream node will be on
     * the execution path and invoked following normal SEP rules.
     *
     * The existing execution path will be unaltered if either the parent
     * wrapped node or the eventNotifier updates then the execution path will
     * progress.
     *
     * @param eventNotifier external event notifier
     * @return
     */
    default Wrapper<T> notiferMerge(Object eventNotifier) {
        return StreamOperator.service().notiferMerge(this, eventNotifier);
    }

    /**
     * Attaches an event notification instance to the current stream node,
     * overriding the execution path of the current stream. Only when the
     * notifier updates will the child nodes of this stream node be on the
     * execution path.
     *
     * @param eventNotifier external event notifier
     * @return
     */
    default Wrapper<T> notifierOverride(Object eventNotifier) {
        return StreamOperator.service().notifierOverride(this, eventNotifier);
    }

    /**
     * Publishes the current value to all child dependencies and then resets. After all children have processed the trigger a reset is 
     * invoked on the wrapped instance. The publish and reset is triggered when the supplied notifier triggers in the
     * execution graph.
     *
     * @param notifier trigger for publish and reset
     * @return
     */
    default Wrapper<T> publishAndReset(Object notifier) {
        return this;
    }
    
    /**
     * Resets the current value without notifying children of a change. The reset is triggered when the supplied notifier triggers in the
     * execution graph.
     *
     * @param notifier trigger for reset
     * @return
     */
    default Wrapper<T> resetNoPublish(Object notifier){
        return this;
    }

    /**
     * Resets the stateful node and publishes the current value by notifying child nodes. The reset is
     * before the notification is broadcast. The reset and publish is triggered when the supplied notifier triggers in the
     * execution graph.
     *
     * @param notifier trigger for reset and publish
     * @return
     */
    default Wrapper<T> resetAndPublish(Object notifier) {
        return this;
    }

    /**
     * Controls the notification policy of event notification to child nodes for
     * this stream node. The default policy is to invoke child nodes when the
     * return of the parent event method is true. NotifyOnChange notifies the
     * child only when the parent node return of the previous cycle is false and
     * this one is true.
     * <p>
     *
     * This can be useful if a single notification of a breach is required and
     * subsequent continued breaches are swallowed, for example this can prevent
     * logging spamming when combined with filters.
     *
     * @param notifyOnChange false = notify always. true = notify on change only
     * @return The current node
     */
    default Wrapper<T> notifyOnChange(boolean notifyOnChange) {
        return this;
    }

    /**
     * Set this property to signal the wrapper has a valid value and child nodes do not have to wait for a trigger 
     * notification before using the data from this instance.
     * 
     * @param validOnStart
     * @return 
     */
    default Wrapper<T> validOnStart(boolean validOnStart) {
        return this;
    }

    default boolean isValidOnStart() {
        return false;
    }

    /**
     * Set the node id for this node within the generated SEP. This is the
     * variable name of the node in a Java SEP. The id must be unique for the
     * SEP.
     *
     * @param id the unique id of the node in the SEP
     * @return The current node
     */
    default Wrapper<T> id(String id) {
        return StreamOperator.service().nodeId(this, id);
    }

}
