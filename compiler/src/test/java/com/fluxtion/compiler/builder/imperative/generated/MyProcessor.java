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
* Server Side License for more details.
*
* You should have received a copy of the Server Side Public License
* along with this program.  If not, see
*
<http://www.mongodb.com/licensing/server-side-public-license>.
*/
package com.fluxtion.compiler.builder.imperative.generated;

import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.audit.EventLogManager;
import com.fluxtion.runtime.audit.NodeNameAuditor;
import com.fluxtion.runtime.callback.CallbackDispatcherImpl;
import com.fluxtion.runtime.callback.InternalEventProcessor;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.input.EventFeed;
import com.fluxtion.runtime.input.SubscriptionManagerNode;
import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.runtime.node.MutableEventProcessorContext;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/*
 *
 * <pre>
 * generation time                 : 2023-03-17T14:30:43.653854
 * eventProcessorGenerator version : ${generator_version_information}
 * api version                     : ${api_version_information}
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked", "rawtypes"})
public class MyProcessor implements
        EventProcessor<MyProcessor>, StaticEventProcessor, InternalEventProcessor, BatchHandler, Lifecycle {

    public final NodeNameAuditor nodeNameLookup = new NodeNameAuditor();
    //Node declarations
    private final CallbackDispatcherImpl callbackDispatcher = new CallbackDispatcherImpl();
    private final SubscriptionManagerNode subscriptionManager = new SubscriptionManagerNode();
    private final MutableEventProcessorContext context = new MutableEventProcessorContext(nodeNameLookup, callbackDispatcher, subscriptionManager, callbackDispatcher);
    private final IdentityHashMap<Object, BooleanSupplier> dirtyFlagSupplierMap = new IdentityHashMap<>(0);
    private final IdentityHashMap<Object, Consumer<Boolean>> dirtyFlagUpdateMap = new IdentityHashMap<>(0);
    //Dirty flags
    private boolean initCalled = false;
    private boolean processing = false;
    private boolean buffering = false;


    //Filter constants


    public MyProcessor(Map<Object, Object> contextMap) {
        context.replaceMappings(contextMap);
        //node auditors
        initialiseAuditor(nodeNameLookup);
        subscriptionManager.setSubscribingEventProcessor(this);
        context.setEventProcessorCallback(this);
    }

    public MyProcessor() {
        this(null);
    }

    @Override
    public void setContextParameterMap(Map<Object, Object> newContextMapping) {
        context.replaceMappings(newContextMapping);
    }


    @Override
    public void addContextParameter(Object key, Object value) {
        context.addMapping(key, value);
    }

    @Override
    public void onEvent(Object event) {
        if (buffering) {
            triggerCalculation();
        }
        if (processing) {
            callbackDispatcher.processReentrantEvent(event);
        } else {
            processing = true;
            onEventInternal(event);
            callbackDispatcher.dispatchQueuedCallbacks();
            processing = false;
        }
    }

    public void onEventInternal(Object event) {
        if (event instanceof java.lang.String) {
            String typedEvent = (String) event;
            handleEvent(typedEvent);
        }


    }


    public void handleEvent(String typedEvent) {
        auditEvent(typedEvent);
        //Default, no filter methods
        //event stack unwind callbacks
        afterEvent();
    }

    public void bufferEvent(Object event) {
        buffering = true;
        if (event instanceof java.lang.String) {
            String typedEvent = (String) event;
            auditEvent(typedEvent);

            //event stack unwind callbacks
        }
    }

    public void triggerCalculation() {
        buffering = false;
        String typedEvent = "No event information - buffered dispatch";
        afterEvent();

    }

    private void auditEvent(Object typedEvent) {
        nodeNameLookup.eventReceived(typedEvent);
    }

    private void auditEvent(Event typedEvent) {
        nodeNameLookup.eventReceived(typedEvent);
    }

    private void initialiseAuditor(Auditor auditor) {
        auditor.init();
        auditor.nodeRegistered(subscriptionManager, "subscriptionManager");
        auditor.nodeRegistered(context, "context");
    }


    private void afterEvent() {
        nodeNameLookup.processingComplete();

    }

    @Override
    public void init() {
        initCalled = true;
        //initialise dirty lookup map
        isDirty("test");
    }

    @Override
    public void start() {
        if (!initCalled) {
            throw new RuntimeException("init() must be called before start()");
        }

    }

    @Override
    public void stop() {
        if (!initCalled) {
            throw new RuntimeException("init() must be called before stop()");
        }

    }

    @Override
    public void tearDown() {
        initCalled = false;
        nodeNameLookup.tearDown();
        subscriptionManager.tearDown();
    }

    @Override
    public void batchPause() {

    }

    @Override
    public void batchEnd() {

    }

    @Override
    public boolean isDirty(Object node) {
        if (dirtyFlagSupplierMap.isEmpty()) {

        }
        return dirtyFlagSupplierMap.getOrDefault(node, StaticEventProcessor.ALWAYS_FALSE).getAsBoolean();
    }

    @Override
    public void setDirty(Object node, boolean dirtyFlag) {
        if (dirtyFlagUpdateMap.isEmpty()) {

        }
        dirtyFlagUpdateMap.get(node).accept(dirtyFlag);
    }


    @Override
    public <T> T getNodeById(String id) throws NoSuchFieldException {
        return nodeNameLookup.getInstanceById(id);
    }

    @Override
    public <A extends Auditor> A getAuditorById(String id) throws NoSuchFieldException, IllegalAccessException {
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
    public MyProcessor newInstance() {
        return new MyProcessor();
    }

    @Override
    public MyProcessor newInstance(Map<Object, Object> contextMap) {
        return new MyProcessor();
    }

    @Override
    public String getLastAuditLogRecord() {
        try {
            EventLogManager eventLogManager = (EventLogManager) this.getClass().getField(EventLogManager.NODE_NAME).get(this);
            return eventLogManager.lastRecordAsString();
        } catch (Throwable e) {
            return "";
        }
    }
}