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

import com.fluxtion.api.event.Event;
import com.fluxtion.api.lifecycle.FilteredEventHandler;
import java.util.Objects;

/**
 * {@inheritDoc}
 */
public final class ReusableEventHandler<T extends Event> implements FilteredEventHandler<T>, EventWrapper<T> {

    private final int filterId;
    private final Class<T> eventClass;
    public T event;

    public ReusableEventHandler(Class<T> eventClass) {
        this.eventClass = eventClass;
        filterId = Event.NO_ID;
    }

    public ReusableEventHandler(int filterId, Class<T> eventClass) {
        this.filterId = filterId;
        this.eventClass = eventClass;
    }

    @Override
    public int filterId() {
        return filterId;
    }

    @Override
    public void onEvent(T e) {
        this.event = e;
    }

    @Override
    public Class<T> eventClass() {
        return eventClass;
    }

    @Override
    public T event() {
        return event;
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
        final ReusableEventHandler<?> other = (ReusableEventHandler<?>) obj;
        if (this.filterId != other.filterId) {
            return false;
        }
        if (!Objects.equals(this.eventClass, other.eventClass)) {
            return false;
        }
        return true;
    }

}
