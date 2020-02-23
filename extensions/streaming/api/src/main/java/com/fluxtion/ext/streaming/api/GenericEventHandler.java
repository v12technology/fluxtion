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

import com.fluxtion.api.FilteredEventHandler;
import com.fluxtion.api.event.Event;
import com.fluxtion.api.event.GenericEvent;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * EventHandler for {@link GenericEvent}'s. GeneriEvents can wrap any type of
 * Object, thus allowing non-fluxtion {@link Event}'s to be handled as an entry
 * point in a SEP.
 *
 *
 * </pre> {@inheritDoc}
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"filterString"})
public final class GenericEventHandler<T extends GenericEvent<W>, W> implements FilteredEventHandler<T> {

    private final String filterString;
    public T event;

//    public GenericEventHandler(Class<W> eventClass) {
//        this.filterString = eventClass.getCanonicalName();
//    }
    @Override
    public int filterId() {
        return Event.NO_INT_FILTER;
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
        return (Class<T>) event.getClass();
    }

    public W event() {
        return event.value;
    }

}
