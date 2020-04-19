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
package com.fluxtion.ext.streaming.api.test;

import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.streaming.api.FilterWrapper;
import com.fluxtion.ext.streaming.api.Test;
import com.fluxtion.ext.streaming.api.Wrapper;

/**
 * A filtering wrapper that propagates the event wave when a referenced test is in the
 * passed state, validated with {@link Test#passed() }
 *
 *
 * @author V12 Technology Ltd.
 * @param <T> The filtered type
 */
public class TestFilter<T> implements FilterWrapper<T> {

    @NoEventReference
    private final Test test;
    private final T tracked;
    private final Wrapper<T> trackedWrapper;

    public TestFilter(Wrapper<T> trackedWrapper, Test test) {
        this.test = test;
        this.tracked = null;
        this.trackedWrapper = trackedWrapper;
    }

    public TestFilter(T tracked, Test notifier) {
        this.test = notifier;
        this.tracked = tracked;
        this.trackedWrapper = null;
    }
    
    @OnEvent
    public boolean updated(){
        return test.passed();
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
