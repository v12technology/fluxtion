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
package com.fluxtion.runtime.event;

import com.fluxtion.runtime.FilteredEventHandler;
import com.fluxtion.runtime.Named;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.stream.TriggeredEventStream;

import java.util.Objects;

/**
 * {@inheritDoc}
 */
public final class DefaultFilteredEventHandler<T>
        extends EventLogNode
        implements FilteredEventHandler<T>, TriggeredEventStream<T>, Named {

    private final int filterId;
    private final String filterString;
    private final Class<T> eventClass;
    private final transient String name;
    public T event;

    public DefaultFilteredEventHandler(Class<T> eventClass) {
        this.eventClass = eventClass;
        this.filterId = Event.NO_INT_FILTER;
        this.filterString = Event.NO_STRING_FILTER;
        name = "handler" + eventClass.getSimpleName();
    }

    public DefaultFilteredEventHandler(int filterId, Class<T> eventClass) {
        this.filterId = filterId;
        this.filterString = Event.NO_STRING_FILTER;
        this.eventClass = eventClass;
        name = "handler" + eventClass.getSimpleName() + "_" + filterId;
    }

    public DefaultFilteredEventHandler(String filterString, Class<T> eventClass) {
        this.filterId = Event.NO_INT_FILTER;
        this.filterString = filterString;
        this.eventClass = eventClass;
        name = "handler" + eventClass.getSimpleName() + "_" + filterString;
    }

    public DefaultFilteredEventHandler(int filterId, String filterString, Class<T> eventClass) {
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
    }

    @Override
    public int filterId() {
        return filterId;
    }

    @Override
    public void onEvent(T e) {
        auditLog.info("inputEvent", e.getClass().getSimpleName());
        this.event = e;
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
        final DefaultFilteredEventHandler<?> other = (DefaultFilteredEventHandler<?>) obj;
        if (this.filterId != other.filterId) {
            return false;
        }
        if (!Objects.equals(this.eventClass, other.eventClass)) {
            return false;
        }
        return true;
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
    public String getName() {
        return name;
    }
}
