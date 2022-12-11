/*
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fluxtion.runtime;

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.audit.EventLogControlEvent;
import com.fluxtion.runtime.audit.EventLogControlEvent.LogLevel;
import com.fluxtion.runtime.event.Signal;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.SinkDeregister;
import com.fluxtion.runtime.stream.SinkRegistration;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/**
 * Processes events of any type and dispatches to registered {@link EventHandlerNode}
 * and methods annotated with {@link OnEventHandler}. An subclass of a StaticEventProcessor is
 * the product of running the event stream compiler on user input. On receipt of an event
 * the processor selects an execution path that comprises a set of application nodes that
 * have a reference to an incoming {@link OnEventHandler} for the specific event.
 * <p>
 * The StaticEventProcessor
 * has the following functionality:
 *
 * <ul>
 * <li>StaticEventProcessor process events of multiple type in a predictable order</li>
 * <li>Application classes are nodes managed by StaticEventProcessor and are notified in a predictable manner when an event is processed</li>
 * <li>An execution path is the set of connected nodes to a matching {@link OnEventHandler} for the incoming event</li>
 * <li>Nodes on the execution path are invoked in topological order, where object reference determine precedence</li>
 * <li>The root of the execution path is an {@link OnEventHandler}</li>
 * <li>Dispatches events based on type to the correct handler</li>
 * <li>Optional String or int filtering can be supplied to narrow the handler selection in conjunction with event type</li>
 * <li>An execution path that is unique for the Event and filter is invoked when an event is received.</li>
 * <li>All node instances are created and managed by StaticEventProcessor</li>
 * </ul>
 * starting point of event dispatch
 *
 * @author Greg Higgins
 */
public interface StaticEventProcessor {

    StaticEventProcessor NULL_EVENTHANDLER = e -> {
    };

    BooleanSupplier ALWAYS_FALSE = () -> false;

    /**
     * Called when a new event e is ready to be processed.
     *
     * @param e the {@link com.fluxtion.runtime.event.Event Event} to process.
     */
    void onEvent(Object e);

    default void onEvent(byte value) {
        onEvent((Byte) value);
    }

    default void onEvent(char value) {
        onEvent((Character) value);
    }

    default void onEvent(short value) {
        onEvent((Short) value);
    }

    default void onEvent(int value) {
        onEvent((Integer) value);
    }

    default void onEvent(float value) {
        onEvent((Float) value);
    }

    default void onEvent(double value) {
        onEvent((Double) value);
    }

    default void onEvent(long value) {
        onEvent((Long) value);
    }

    default <T> void addSink(String id, Consumer<T> sink) {
        onEvent(SinkRegistration.sink(id, sink));
    }

    default void addIntSink(String id, IntConsumer sink) {
        onEvent(SinkRegistration.intSink(id, sink));
    }

    default void addDoubleSink(String id, DoubleConsumer sink) {
        onEvent(SinkRegistration.doubleSink(id, sink));
    }

    default void addLongSink(String id, LongConsumer sink) {
        onEvent(SinkRegistration.longSink(id, sink));
    }

    default void removeSink(String id) {
        onEvent(SinkDeregister.sink(id));
    }

    default void publishSignal(String filter) {
        publishSignal(filter, new Object());
    }

    default <T> void publishSignal(String filter, T value) {
        onEvent(new Signal<>(filter, value));
    }

    default void publishSignal(String filter, int value) {
        onEvent(Signal.intSignal(filter, value));
    }

    default void publishIntSignal(String filter, int value) {
        publishSignal(filter, value);
    }

    default void publishSignal(String filter, double value) {
        onEvent(Signal.doubleSignal(filter, value));
    }

    default void publishDoubleSignal(String filter, double value) {
        publishSignal(filter, value);
    }

    default void publishSignal(String filter, long value) {
        onEvent(Signal.longSignal(filter, value));
    }

    default void publishLongSignal(String filter, long value) {
        publishSignal(filter, value);
    }

    default <T> T getNodeById(String id) throws NoSuchFieldException {
        throw new NoSuchFieldException(id);
    }

    default <T> T getStreamed(String name) throws NoSuchFieldException {
        EventStream<T> stream = getNodeById(name);
        return stream.get();
    }

    default void setAuditLogLevel(LogLevel logLevel) {
        onEvent(new EventLogControlEvent(logLevel));
    }
}
