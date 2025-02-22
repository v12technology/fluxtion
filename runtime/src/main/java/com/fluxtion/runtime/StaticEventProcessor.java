/*
 * Copyright (c) 2025 gregory higgins.
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
package com.fluxtion.runtime;

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.FluxtionIgnore;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.*;
import com.fluxtion.runtime.audit.EventLogControlEvent.LogLevel;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.event.Signal;
import com.fluxtion.runtime.input.EventFeed;
import com.fluxtion.runtime.input.SubscriptionManager;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.runtime.node.EventHandlerNode;
import com.fluxtion.runtime.node.InstanceSupplier;
import com.fluxtion.runtime.output.SinkDeregister;
import com.fluxtion.runtime.output.SinkRegistration;
import com.fluxtion.runtime.service.Service;
import com.fluxtion.runtime.service.ServiceRegistry;
import com.fluxtion.runtime.time.ClockStrategy;

import java.util.Map;
import java.util.function.*;

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
public interface StaticEventProcessor extends ServiceRegistry, NodeDiscovery {

    @FluxtionIgnore
    StaticEventProcessor NULL_EVENTHANDLER = e -> {
    };

    BooleanSupplier ALWAYS_FALSE = () -> false;

    /**
     * The user can pass in a map of values to this instance. The map will be available in the graph by injecting
     * an {@link EventProcessorContext}.
     *
     * <pre>
     * {@literal @}Inject
     *  public EventProcessorContext context;
     *
     * </pre>
     * <p>
     * Calling this method before {@link Lifecycle#init()} will ensure all the nodes
     * see the context when their {@link com.fluxtion.runtime.annotations.Initialise} annotated methods are invoked
     */
    default void setContextParameterMap(Map<Object, Object> newContextMapping) {
        throw new UnsupportedOperationException("this StaticEventProcessor does not accept updated context map");
    }

    /**
     * inject an instance into the running instance, is available via:
     * {@link InstanceSupplier}
     * <p>
     * Can also be accessed via {@link EventProcessorContext#getInjectedInstance(Class)}
     * Can also be accessed via {@link EventProcessorContext#getInjectedInstanceAllowNull(Class)}
     *
     * @param instance the instance to inject
     */
    @SuppressWarnings("unckecked")
    default <T> void injectInstance(T instance) {
        injectInstance(instance, ((Class<T>) instance.getClass()));
    }

    /**
     * inject an instance into the running instance with a name qualifier, is available via:
     * {@link InstanceSupplier}. Set the qualifier of the injected with {@link Inject#instanceName()}
     * <p>
     * Can also be accessed via {@link EventProcessorContext#getInjectedInstance(Class)}
     * Can also be accessed via {@link EventProcessorContext#getInjectedInstanceAllowNull(Class)}
     *
     * @param instance the instance to inject
     * @param name     the qualifying name of the instance to inject
     */
    default void injectNamedInstance(Object instance, String name) {
        addContextParameter(instance.getClass().getCanonicalName() + "_" + name, instance);
    }

    /**
     * inject an instance into the running instance with a name qualifier, is available via:
     * {@link InstanceSupplier}.
     * Set the injected type supplied to the EventProcessor
     * <p>
     * Can also be accessed via {@link EventProcessorContext#getInjectedInstance(Class)}
     * Can also be accessed via {@link EventProcessorContext#getInjectedInstanceAllowNull(Class)}
     *
     * @param instance    the instance to inject
     * @param exposedType The type to make available at the injection site
     */
    default <T, S extends T> void injectInstance(S instance, Class<T> exposedType) {
        addContextParameter(exposedType.getCanonicalName(), instance);
    }

    /**
     * inject an instance into the running instance with a name qualifier, is available via:
     * {@link InstanceSupplier}.
     * Set the name with {@link Inject#instanceName()}
     * Set the injected type supplied to the EventProcessor
     * <p>
     * Can also be accessed via {@link EventProcessorContext#getInjectedInstance(Class, String)}
     * Can also be accessed via {@link EventProcessorContext#getInjectedInstanceAllowNull(Class, String)}
     *
     * @param instance    the instance to inject
     * @param exposedType The type to make available at the injection sit
     * @param name        the qualifying name of the instance to inject
     */
    default <T, S extends T> void injectNamedInstance(S instance, Class<T> exposedType, String name) {
        addContextParameter(exposedType.getCanonicalName() + "_" + name, instance);
    }

    default void addContextParameter(Object key, Object value) {
        throw new UnsupportedOperationException("this StaticEventProcessor does not accept updates to context map");
    }

    /**
     * Called when a new event e is ready to be processed. Calls a {@link #triggerCalculation()} first if any events
     * have been buffered.
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

    /**
     * Buffers an event as part of a transaction only EventHandler methods are invoked, no OnTrigger methods are
     * processed. EventHandlers are marked as dirty ready for {@link #triggerCalculation()} to invoke a full event cycle
     *
     * @param event
     */
    default void bufferEvent(Object event) {
        throw new UnsupportedOperationException("buffering of events not supported");
    }

    default void bufferEvent(byte value) {
        bufferEvent((Byte) value);
    }

    default void bufferEvent(char value) {
        bufferEvent((Character) value);
    }

    default void bufferEvent(short value) {
        bufferEvent((Short) value);
    }

    default void bufferEvent(int value) {
        bufferEvent((Integer) value);
    }

    default void bufferEvent(float value) {
        bufferEvent((Float) value);
    }

    default void bufferEvent(double value) {
        bufferEvent((Double) value);
    }

    default void bufferEvent(long value) {
        bufferEvent((Long) value);
    }

    /**
     * Runs a graph calculation cycle invoking any {@link com.fluxtion.runtime.annotations.OnTrigger} methods whose
     * parents are marked dirty. Used in conjunction with {@link #bufferEvent(Object)}, this method marks event handlers
     * as dirty.
     */
    default void triggerCalculation() {
        throw new UnsupportedOperationException("buffering of events not supported");
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

    /**
     * Publishes an instance wrapped in a Signal and applies a class filter to the published signal
     * <p>
     * receiving an event callback in a node
     * <pre>
     * {@literal @}OnEventHandler(filterStringFromClass = Date.class)
     *  public void handleEvent(Signal<Date> date) {
     *      count++;
     *  }
     *
     * </pre>
     * <p>
     * publishing:
     * <pre>
     *     eventProcessorInstance.publishObjectSignal(new Date());
     * </pre>
     *
     * @param instance
     * @param <T>
     */
    default <T> void publishObjectSignal(T instance) {
        onEvent(new Signal<>(instance));
    }

    default <S, T> void publishObjectSignal(Class<S> filterClass, T instance) {
        onEvent(new Signal<>(filterClass, instance));
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
        FlowFunction<T> stream = getNodeById(name);
        return stream.get();
    }

    @SuppressWarnings("unchecked")
    default <A extends Auditor> A getAuditorById(String id) throws NoSuchFieldException, IllegalAccessException {
        return getNodeById(id);
    }

    default void addEventFeed(EventFeed eventProcessorFeed) {
        throw new UnsupportedOperationException("addEventProcessorFeed not implemented");
    }

    default void removeEventFeed(EventFeed eventProcessorFeed) {
        throw new UnsupportedOperationException("removeEventProcessorFeed not implemented");
    }

    default void setAuditLogLevel(LogLevel logLevel) {
        onEvent(new EventLogControlEvent(logLevel));
    }

    default void setAuditLogLevel(LogLevel logLevel, String nodeName) {
        onEvent(new EventLogControlEvent(nodeName, null, logLevel));
    }

    default void setAuditLogProcessor(LogRecordListener logProcessor) {
        onEvent(new EventLogControlEvent(logProcessor));
    }

    default void setAuditLogRecordEncoder(LogRecord logRecord) {
        onEvent(new EventLogControlEvent(logRecord));
    }

    default void setAuditTimeFormatter(ObjLongConsumer<StringBuilder> timeFormatter) {
        onEvent(new EventLogControlEvent(timeFormatter));
    }

    /**
     * Attempts to get the last {@link com.fluxtion.runtime.audit.LogRecord} as a String if one is available. Useful
     * for error handling if there is a filure in the graph;
     *
     * @return The last logRecord as a String if it is available
     */
    default String getLastAuditLogRecord() {
        try {
            return this.<EventLogManager>getNodeById(EventLogManager.NODE_NAME).lastRecordAsString();
        } catch (Throwable e) {
            return "";
        }
    }

    default void setClockStrategy(ClockStrategy clockStrategy) {
        onEvent(ClockStrategy.registerClockEvent(clockStrategy));
    }

    /**
     * Returns an instance of the event processor cast to an interface type. The implemented interfaces of an event processor
     * are specified using the com.fluxtion.compiler.EventProcessorConfig#addInterfaceImplementation during the
     * building phase of the processor or using the @ExportService annotation.
     *
     * @param <T> the interface type to cast to
     * @return The {@link StaticEventProcessor} cast to an interface
     */
    @SuppressWarnings("unchecked")
    default <T> T getExportedService() {
        return (T) this;
    }

    /**
     * Determines if the event processor exports the service interface. The implemented interfaces of an event processor
     * are specified using the com.fluxtion.compiler.EventProcessorConfig#addInterfaceImplementation during the
     * building phase of the processor or using the @ExportService annotation.
     *
     * @param exportedServiceClass the type of service to search for
     * @param <T>                  the interface type to cast to
     * @return flag indicating the event processor exports the interface
     */
    default <T> boolean exportsService(Class<T> exportedServiceClass) {
        T svcExport = getExportedService();
        return exportedServiceClass.isInstance(svcExport);
    }

    /**
     * Returns an instance of the event processor cast to an interface type. The implemented interfaces of an event processor
     * are specified using the com.fluxtion.compiler.EventProcessorConfig#addInterfaceImplementation during the
     * building phase of the processor or using the @ExportService annotation.
     *
     * @param exportedServiceClass the type of service to search for
     * @param <T>                  the interface type to cast to
     * @return The {@link StaticEventProcessor} cast to an interface
     */
    default <T> T getExportedService(Class<T> exportedServiceClass) {
        return exportsService(exportedServiceClass) ? getExportedService() : null;
    }

    /**
     * Returns an instance of the event processor cast to an interface type, returning a default value if one cannot
     * be found
     *
     * @param exportedServiceClass the type of service to search for
     * @param defaultValue         default service instance to return if no service is exported
     * @param <T>                  the interface type to cast to
     * @return The {@link StaticEventProcessor} cast to an interface
     */
    default <T> T getExportedService(Class<T> exportedServiceClass, T defaultValue) {
        return exportsService(exportedServiceClass) ? getExportedService() : defaultValue;
    }

    /**
     * Passes the event processor cast to an interface for a consumer to process if the event processor exports service
     *
     * @param exportedServiceClass the type of service to search for
     * @param serviceConsumer      service consumer callback, invoked if the service is exported
     * @param <T>                  the interface type to cast to
     */
    default <T> void consumeServiceIfExported(Class<T> exportedServiceClass, Consumer<T> serviceConsumer) {
        T exportedService = getExportedService(exportedServiceClass);
        if (exportedService != null) {
            serviceConsumer.accept(exportedService);
        }
    }

    /**
     * Register a Consumer that will be called whenever an event is posted to the processor but there are no registered
     * event handlers for that type.
     *
     * @param consumer The Unknown event type handler
     * @param <T>      The type of the event
     */
    default <T> void setUnKnownEventHandler(Consumer<T> consumer) {
    }

    @Override
    default void registerService(Service<?> service) {

    }

    @Override
    default void deRegisterService(Service<?> service) {

    }

    default SubscriptionManager getSubscriptionManager() {
        throw new UnsupportedOperationException();
    }
}
