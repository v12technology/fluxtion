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

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.declarative.api.Wrapper;

/**
 * A filtering wrapper that propagates the event wave when the tracked object
 * and a notifier object both indicate a positive change.
 * 
 * Can be useful to combine a validation with a tracked object for broadcasting
 * to dependent nodes.
 * 
 * @author V12 Technology Ltd.
 * @param <T> The filtered type
 */
public class BooleanMatchFilter<T> implements Wrapper<T> {

    private final Object notifier;
    private final T tracked;
    private final Wrapper<T> trackedWrapper;
    private boolean notifierUpdate;
    private boolean trackedUpdate;

    public BooleanMatchFilter(Wrapper<T>  trackedWrapper, Object notifier) {
        this.notifier = notifier;
        this.tracked = null;
        this.trackedWrapper = trackedWrapper;
    }

    public BooleanMatchFilter(T tracked, Object notifier) {
        this.notifier = notifier;
        this.tracked = tracked;
        this.trackedWrapper = null;
    }

    @OnParentUpdate("notifier")
    public void notifier(Object notifier){
        notifierUpdate = true;
    }

    @OnParentUpdate("tracked")
    public void trackedUpdated(T tracked){
        trackedUpdate = true;
    }
    

    @OnParentUpdate("trackedWrapper")
    public void trackedWrapperUpdated(Wrapper<T> tracked){
        trackedUpdate = true;
    }
    
    @OnEvent
    public boolean filteredUpdate(){
        boolean sendUpdate = notifierUpdate & trackedUpdate;
        notifierUpdate = false;
        trackedUpdate = false;
        return sendUpdate;
    }
    
    @Initialise
    public void init(){
        notifierUpdate = false;
        trackedUpdate = false;
    }
    
    @Override
    public T event() {
        return tracked==null?trackedWrapper.event(): tracked;
    }

    @Override
    public Class<T> eventClass() {
        return (Class<T>) (tracked==null?trackedWrapper.eventClass():tracked.getClass());
    }

}
