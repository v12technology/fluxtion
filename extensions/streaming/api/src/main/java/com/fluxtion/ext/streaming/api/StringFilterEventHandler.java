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
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * {@inheritDoc}
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"eventClass", "filterString"})
public final class StringFilterEventHandler<T extends Event> implements FilteredEventHandler<T>, Wrapper<T> {

    private final String filterString;
    private final Class<T> eventClass;
    public T event;

    public StringFilterEventHandler(Class<T> eventClass) {
        this.eventClass = eventClass;
        this.filterString = "";
    }

    @Override
    public int filterId() {
        return Event.NO_ID;
    }

    @Override
    public String filterString() {
        return filterString;
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
}
