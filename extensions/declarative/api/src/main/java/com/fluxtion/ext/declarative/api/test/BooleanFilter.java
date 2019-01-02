/*
 * Copyright (C) 2019 V12 Technology Ltd.
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
package com.fluxtion.ext.declarative.api.test;

import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.declarative.api.Wrapper;

/**
 * A filtering wrapper that propagates the event wave when a notifier object
 * both indicate a positive change.
 *
 * Can be useful to combine a failed validation with a tracked object for broadcasting
 * to dependent nodes. The tracked object ma
 *
 * @author V12 Technology Ltd.
 * @param <T> The filtered type
 */
public class BooleanFilter<T> implements Wrapper<T> {

    private final Object notifier;
    @NoEventReference
    private final T tracked;
    private final Wrapper<T> trackedWrapper;

    public BooleanFilter(Wrapper<T> trackedWrapper, Object notifier) {
        this.notifier = notifier;
        this.tracked = null;
        this.trackedWrapper = trackedWrapper;
    }

    public BooleanFilter(T tracked, Object notifier) {
        this.notifier = notifier;
        this.tracked = tracked;
        this.trackedWrapper = null;
    }
    
    @OnEvent
    public boolean updated(){
        return true;
    }

    public boolean filteredUpdate() {
        return true;
    }

    @Override
    public T event() {
        return tracked == null ? trackedWrapper.event() : tracked;
    }

    @Override
    public Class<T> eventClass() {
        return (Class<T>) (tracked == null ? trackedWrapper.eventClass() : tracked.getClass());
    }

}
