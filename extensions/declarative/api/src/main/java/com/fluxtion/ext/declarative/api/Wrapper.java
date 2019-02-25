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

    /**
     * pushes a data item from the current node in the stream to any node.The
     * target node will become part of the same execution graph as the
     * source.<p>
     * The returned node is the current node in the stream.
     *
     * @param <T>
     * @param <R>
     * @param <S>
     * @param supplier
     * @param mapper
     * @return the com.fluxtion.ext.declarative.api.Wrapper<T>
     */
    default <T, R, S extends R> Wrapper<T> push(SerializableFunction<T, S> supplier, SerializableConsumer< R> mapper) {
        StreamOperator.service().push(this, supplier.method(), mapper);
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

    static LongAdder counter = new LongAdder();

    /**
     * dump this node to console, prefixed with the supplied message. {@link Object#toString()
     * } will be invoked on the node instance.
     *
     * @param prefix String prefix for the console message
     * @return The current node
     */
    default Wrapper<T> console(String prefix) {
        StreamOperator.PrefixToConsole console = new StreamOperator.PrefixToConsole(prefix);
        counter.increment();
        StreamOperator.service().nodeId(console, "consoleMsg_" + counter.intValue());
        return StreamOperator.service().forEach(console::standardOut, this, "consoleLog_" + counter.intValue());
    }

    /**
     * Attaches a reset notifier instance to the current stream node. If the
     * node is {@link Stateful} it may be desirable to reset its state under
     * controlled conditions. When the resetNotifier is on an execution path it
     * will invoke the reset method of this node if it is {@link Stateful}
     *
     * @param resetNotifier external notifier
     * @return
     */
    default Wrapper<T> resetNotifier(Object resetNotifier) {
        return this;
    }

    /**
     * Attaches an event notification instance to the current stream node. When
     * the notifier updates all the child nodes of this stream node will be on
     * the execution path and invoked following normal SEP rules.
     * 
     * The existing execution path will be unaltered if either the parent wrapped
     * node or the eventNotifier updates then the execution path will progress.
     *
     * @param eventNotifier external event notifier
     * @return
     */
    default Wrapper<T> eventNotifier(Object eventNotifier) {
        return StreamOperator.service().eventNotifer(this, eventNotifier);
    }
    
    /**
     * Attaches an event notification instance to the current stream node. When
     * the notifier updates all the child nodes of this stream node will be on
     * the execution path and invoked following normal SEP rules.
     *
     * @param eventNotifier external event notifier
     * @return
     */
    default Wrapper<T> eventFilter(Object eventNotifier) {
        return StreamOperator.service().eventFilter(this, eventNotifier);
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
