/*
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
*
<http://www.mongodb.com/licensing/server-side-public-license>.
*/
package com.fluxtion.runtime;

import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.audit.EventLogManager;
import com.fluxtion.runtime.audit.NodeNameAuditor;
import com.fluxtion.runtime.callback.CallbackDispatcherImpl;
import com.fluxtion.runtime.callback.ExportFunctionAuditEvent;
import com.fluxtion.runtime.callback.InternalEventProcessor;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.input.EventFeed;
import com.fluxtion.runtime.input.SubscriptionManager;
import com.fluxtion.runtime.input.SubscriptionManagerNode;
import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.runtime.node.MutableEventProcessorContext;
import com.fluxtion.runtime.node.ObjectEventHandlerNode;
import com.fluxtion.runtime.service.ServiceListener;
import com.fluxtion.runtime.service.ServiceRegistryNode;
import com.fluxtion.runtime.time.Clock;
import com.fluxtion.runtime.time.ClockStrategy.ClockStrategyEvent;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * <pre>
 * generation time                 : Not available
 * eventProcessorGenerator version : ${generator_version_information}
 * api version                     : ${api_version_information}
 * </pre>
 * <p>
 * Event classes supported:
 *
 * <ul>
 *   <li>com.fluxtion.compiler.generation.model.ExportFunctionMarker
 *   <li>com.fluxtion.runtime.time.ClockStrategy.ClockStrategyEvent
 *   <li>java.lang.Object
 * </ul>
 *
 * @author Greg Higgins
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class DefaultEventProcessor
        implements EventProcessor<DefaultEventProcessor>,
        /*--- @ExportService start ---*/
        @ExportService ServiceListener,
        /*--- @ExportService end ---*/
        StaticEventProcessor,
        InternalEventProcessor,
        BatchHandler,
        Lifecycle {

    //Node declarations
    private final transient CallbackDispatcherImpl callbackDispatcher = new CallbackDispatcherImpl();
    public final transient Clock clock = new Clock();
    public final transient NodeNameAuditor nodeNameLookup = new NodeNameAuditor();
    private final transient SubscriptionManagerNode subscriptionManager =
            new SubscriptionManagerNode();
    private final transient MutableEventProcessorContext context =
            new MutableEventProcessorContext(
                    nodeNameLookup, callbackDispatcher, subscriptionManager, callbackDispatcher);
    public final transient ServiceRegistryNode serviceRegistry = new ServiceRegistryNode();
    private final transient ObjectEventHandlerNode allEventHandler;// = new ObjectEventHandlerNode();
    private final transient ExportFunctionAuditEvent functionAudit = new ExportFunctionAuditEvent();
    //Dirty flags
    private boolean initCalled = false;
    private boolean processing = false;
    private boolean buffering = false;
    private final transient IdentityHashMap<Object, BooleanSupplier> dirtyFlagSupplierMap =
            new IdentityHashMap<>(1);
    private final transient IdentityHashMap<Object, Consumer<Boolean>> dirtyFlagUpdateMap =
            new IdentityHashMap<>(1);

    private boolean isDirty_clock = false;

    //Filter constants

    //unknown event handler
    private Consumer unKnownEventHandler = (e) -> {};

    public DefaultEventProcessor(ObjectEventHandlerNode allEventHandler, Map<Object, Object> contextMap) {
        this.allEventHandler = allEventHandler;
        if (context != null) {
            context.replaceMappings(contextMap);
        }
        context.setClock(clock);
        serviceRegistry.setEventProcessorContext(context);
        //node auditors
        initialiseAuditor(clock);
        initialiseAuditor(nodeNameLookup);
        initialiseAuditor(serviceRegistry);
        if (subscriptionManager != null) {
            subscriptionManager.setSubscribingEventProcessor(this);
        }
        if (context != null) {
            context.setEventProcessorCallback(this);
        }
    }

    public DefaultEventProcessor(ObjectEventHandlerNode allEventHandler) {
        this(allEventHandler, null);
    }

    @Override
    public void init() {
        initCalled = true;
        auditEvent(LifecycleEvent.Init);
        //initialise dirty lookup map
        isDirty("test");
        clock.init();
        allEventHandler.init();
        afterEvent();
    }

    @Override
    public void start() {
        if (!initCalled) {
            throw new RuntimeException("init() must be called before start()");
        }
        processing = true;
        auditEvent(LifecycleEvent.Start);
        allEventHandler.start();
        afterEvent();
        callbackDispatcher.dispatchQueuedCallbacks();
        processing = false;
    }

    @Override
    public void startComplete() {
        if (!initCalled) {
            throw new RuntimeException("init() must be called before startComplete()");
        }
        processing = true;
        auditEvent(LifecycleEvent.StartComplete);

        afterEvent();
        callbackDispatcher.dispatchQueuedCallbacks();
        processing = false;
    }

    @Override
    public void stop() {
        if (!initCalled) {
            throw new RuntimeException("init() must be called before stop()");
        }
        processing = true;
        auditEvent(LifecycleEvent.Stop);
        allEventHandler.stop();
        afterEvent();
        callbackDispatcher.dispatchQueuedCallbacks();
        processing = false;
    }

    @Override
    public void tearDown() {
        initCalled = false;
        auditEvent(LifecycleEvent.TearDown);
        serviceRegistry.tearDown();
        nodeNameLookup.tearDown();
        clock.tearDown();
        allEventHandler.tearDown();
        subscriptionManager.tearDown();
        afterEvent();
    }

    @Override
    public void setContextParameterMap(Map<Object, Object> newContextMapping) {
        context.replaceMappings(newContextMapping);
    }

    @Override
    public void addContextParameter(Object key, Object value) {
        context.addMapping(key, value);
    }

    //EVENT DISPATCH - START
    @Override
    @OnEventHandler(failBuildIfMissingBooleanReturn = false)
    public void onEvent(Object event) {
        if (buffering) {
            triggerCalculation();
        }
        if (processing) {
            callbackDispatcher.queueReentrantEvent(event);
        } else {
            processing = true;
            onEventInternal(event);
            callbackDispatcher.dispatchQueuedCallbacks();
            processing = false;
        }
    }

    @Override
    public void onEventInternal(Object event) {
        if (event instanceof ClockStrategyEvent) {
            ClockStrategyEvent typedEvent = (ClockStrategyEvent) event;
            handleEvent(typedEvent);
        } else if (event instanceof Object) {
            Object typedEvent = (Object) event;
            handleEvent(typedEvent);
        }
    }

    public void handleEvent(ClockStrategyEvent typedEvent) {
        auditEvent(typedEvent);
        //Default, no filter methods
        isDirty_clock = true;
        clock.setClockStrategy(typedEvent);
        allEventHandler.onEvent(typedEvent);
        afterEvent();
    }

    public void handleEvent(Object typedEvent) {
        auditEvent(typedEvent);
        //Default, no filter methods
        allEventHandler.onEvent(typedEvent);
        afterEvent();
    }
    //EVENT DISPATCH - END

    //EXPORTED SERVICE FUNCTIONS - START
    @Override
    public void deRegisterService(com.fluxtion.runtime.service.Service<?> arg0) {
        beforeServiceCall(
                "public void com.fluxtion.runtime.service.ServiceRegistryNode.deRegisterService(com.fluxtion.runtime.service.Service<?>)");
        ExportFunctionAuditEvent typedEvent = functionAudit;
        serviceRegistry.deRegisterService(arg0);
        afterServiceCall();
    }

    @Override
    public void registerService(com.fluxtion.runtime.service.Service<?> arg0) {
        beforeServiceCall(
                "public void com.fluxtion.runtime.service.ServiceRegistryNode.registerService(com.fluxtion.runtime.service.Service<?>)");
        ExportFunctionAuditEvent typedEvent = functionAudit;
        serviceRegistry.registerService(arg0);
        afterServiceCall();
    }
    //EXPORTED SERVICE FUNCTIONS - END

    //EVENT BUFFERING - START
    public void bufferEvent(Object event) {
        buffering = true;
        if (event instanceof ClockStrategyEvent) {
            ClockStrategyEvent typedEvent = (ClockStrategyEvent) event;
            auditEvent(typedEvent);
            isDirty_clock = true;
            clock.setClockStrategy(typedEvent);
            allEventHandler.onEvent(typedEvent);
        } else if (event instanceof Object) {
            Object typedEvent = (Object) event;
            auditEvent(typedEvent);
            allEventHandler.onEvent(typedEvent);
        }
    }

    public void triggerCalculation() {
        buffering = false;
        String typedEvent = "No event information - buffered dispatch";
        afterEvent();
    }
    //EVENT BUFFERING - END

    private void auditEvent(Object typedEvent) {
        clock.eventReceived(typedEvent);
        nodeNameLookup.eventReceived(typedEvent);
        serviceRegistry.eventReceived(typedEvent);
    }

    private void auditEvent(Event typedEvent) {
        clock.eventReceived(typedEvent);
        nodeNameLookup.eventReceived(typedEvent);
        serviceRegistry.eventReceived(typedEvent);
    }

    private void initialiseAuditor(Auditor auditor) {
        auditor.init();
        auditor.nodeRegistered(allEventHandler, "allEventHandler");
        auditor.nodeRegistered(callbackDispatcher, "callbackDispatcher");
        auditor.nodeRegistered(subscriptionManager, "subscriptionManager");
        auditor.nodeRegistered(context, "context");
    }

    private void beforeServiceCall(String functionDescription) {
        functionAudit.setFunctionDescription(functionDescription);
        auditEvent(functionAudit);
        if (buffering) {
            triggerCalculation();
        }
        processing = true;
    }

    private void afterServiceCall() {
        afterEvent();
        callbackDispatcher.dispatchQueuedCallbacks();
        processing = false;
    }

    private void afterEvent() {

        clock.processingComplete();
        nodeNameLookup.processingComplete();
        serviceRegistry.processingComplete();
        isDirty_clock = false;
    }

    @Override
    public void batchPause() {
        auditEvent(LifecycleEvent.BatchPause);
        processing = true;

        afterEvent();
        callbackDispatcher.dispatchQueuedCallbacks();
        processing = false;
    }

    @Override
    public void batchEnd() {
        auditEvent(LifecycleEvent.BatchEnd);
        processing = true;

        afterEvent();
        callbackDispatcher.dispatchQueuedCallbacks();
        processing = false;
    }

    @Override
    public boolean isDirty(Object node) {
        return dirtySupplier(node).getAsBoolean();
    }

    @Override
    public BooleanSupplier dirtySupplier(Object node) {
        if (dirtyFlagSupplierMap.isEmpty()) {
            dirtyFlagSupplierMap.put(clock, () -> isDirty_clock);
        }
        return dirtyFlagSupplierMap.getOrDefault(node, StaticEventProcessor.ALWAYS_FALSE);
    }

    @Override
    public void setDirty(Object node, boolean dirtyFlag) {
        if (dirtyFlagUpdateMap.isEmpty()) {
            dirtyFlagUpdateMap.put(clock, (b) -> isDirty_clock = b);
        }
        dirtyFlagUpdateMap.get(node).accept(dirtyFlag);
    }

    private boolean guardCheck_context() {
        return isDirty_clock;
    }

    @Override
    public <T> T getNodeById(String id) throws NoSuchFieldException {
        return nodeNameLookup.getInstanceById(id);
    }

    @Override
    public <A extends Auditor> A getAuditorById(String id)
            throws NoSuchFieldException, IllegalAccessException {
        return (A) this.getClass().getField(id).get(this);
    }

    @Override
    public void addEventFeed(EventFeed eventProcessorFeed) {
        subscriptionManager.addEventProcessorFeed(eventProcessorFeed);
    }

    @Override
    public void removeEventFeed(EventFeed eventProcessorFeed) {
        subscriptionManager.removeEventProcessorFeed(eventProcessorFeed);
    }

    @Override
    public DefaultEventProcessor newInstance() {
        return new DefaultEventProcessor(allEventHandler);
    }

    @Override
    public DefaultEventProcessor newInstance(Map<Object, Object> contextMap) {
        return new DefaultEventProcessor(allEventHandler);
    }

    @Override
    public String getLastAuditLogRecord() {
        try {
            EventLogManager eventLogManager =
                    (EventLogManager) this.getClass().getField(EventLogManager.NODE_NAME).get(this);
            return eventLogManager.lastRecordAsString();
        } catch (Throwable e) {
            return "";
        }
    }

    public void unKnownEventHandler(Object object) {
        unKnownEventHandler.accept(object);
    }

    @Override
    public <T> void setUnKnownEventHandler(Consumer<T> consumer) {
        unKnownEventHandler = consumer;
    }

    @Override
    public SubscriptionManager getSubscriptionManager() {
        return subscriptionManager;
    }
}
