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
package com.fluxtion.runtime.node;

import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.TearDown;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.Lifecycle;

import java.util.Objects;
import java.util.function.BooleanSupplier;

/**
 * {@inheritDoc}
 */
public final class DefaultEventHandlerNode<T>
        extends EventLogNode
        implements EventHandlerNode<T>, TriggeredFlowFunction<T>, NamedNode, Lifecycle {

    private final int filterId;
    private final String filterString;
    private final Class<T> eventClass;
    private final String name;
    private final transient EventSubscription<?> subscription;
    private T event;
    private BooleanSupplier dirtySupplier;
    @Inject
    private final EventProcessorContext eventProcessorContext;

    public DefaultEventHandlerNode(Class<T> eventClass) {
        this(Event.NO_INT_FILTER, Event.NO_STRING_FILTER, eventClass);
    }

    public DefaultEventHandlerNode(int filterId, Class<T> eventClass) {
        this(filterId, Event.NO_STRING_FILTER, eventClass);
    }

    public DefaultEventHandlerNode(String filterString, Class<T> eventClass) {
        this(Event.NO_INT_FILTER, filterString, eventClass);
    }

    public DefaultEventHandlerNode(
            int filterId,
            String filterString,
            Class<T> eventClass
    ) {
        this.filterId = filterId;
        this.filterString = filterString;
        this.eventClass = eventClass;
        if (filterId != Event.NO_INT_FILTER) {
            name = "handler" + eventClass.getSimpleName() + "_" + filterId;
        } else if (!filterString.equals(Event.NO_STRING_FILTER)) {
            name = "handler" + eventClass.getSimpleName() + "_" + filterString;
        } else {
            name = "handler" + eventClass.getSimpleName();
        }
        subscription = new EventSubscription(filterId, filterString, eventClass);
        eventProcessorContext = null;
    }

    public DefaultEventHandlerNode(
            int filterId,
            @AssignToField("filterString") String filterString,
            Class<T> eventClass,
            @AssignToField("name") String name,
            EventProcessorContext eventProcessorContext) {
        this.eventProcessorContext = eventProcessorContext;
        this.filterId = filterId;
        this.filterString = filterString;
        this.eventClass = eventClass;
        this.name = name;
        subscription = new EventSubscription(filterId, filterString, eventClass);
    }

    @Override
    public void parallel() {

    }

    @Override
    public boolean parallelCandidate() {
        return false;
    }

    @Override
    public int filterId() {
        return filterId;
    }

    @Override
    public boolean onEvent(T e) {
        auditLog.info("inputEvent", e.getClass().getSimpleName());
        this.event = e;
        return true;
    }

    @Override
    public String filterString() {
        return filterString;
    }

    @Override
    public Class<T> eventClass() {
        return eventClass;
    }


    @Override
    public boolean hasChanged() {
        return dirtySupplier.getAsBoolean();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + this.filterId;
        hash = 13 * hash + Objects.hashCode(this.eventClass);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultEventHandlerNode<?> other = (DefaultEventHandlerNode<?>) obj;
        if (this.filterId != other.filterId) {
            return false;
        }
        if (this.filterString != other.filterString) {
            return false;
        }
        if (!Objects.equals(this.eventClass, other.eventClass)) {
            return false;
        }
        return true;
    }

    @Initialise
    @Override
    public void init() {
        dirtySupplier = eventProcessorContext.getDirtyStateMonitor().dirtySupplier(this);
        eventProcessorContext.getSubscriptionManager().subscribe(subscription);
    }

    @Override
    @TearDown
    public void tearDown() {
        eventProcessorContext.getSubscriptionManager().unSubscribe(subscription);
    }

    @Override
    public T get() {
        return event;
    }

    @Override
    public void setUpdateTriggerNode(Object updateTriggerNode) {
        //do nothing
    }

    @Override
    public void setPublishTriggerNode(Object publishTriggerNode) {
        //do nothing
    }

    @Override
    public void setResetTriggerNode(Object resetTriggerNode) {
        //do nothing
    }

    @Override
    public void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode) {
    }

    @Override
    public String getName() {
        return name;
    }
}
