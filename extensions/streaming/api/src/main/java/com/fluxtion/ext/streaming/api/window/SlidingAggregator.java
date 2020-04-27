/*
 * Copyright (c) 2020, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.ext.streaming.api.window;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.Wrapper;
import java.util.ArrayDeque;
import lombok.Data;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Data
public class SlidingAggregator<T> implements Wrapper<T> {

    private final Object notifier;
    private final Class<T> clazz;
    @NoEventReference
    private final Stateful<T> source;
    private final int size;
    private Stateful<T> aggregator;
    private ArrayDeque<Stateful> deque;
    @NoEventReference
    private TimeReset timeReset;
    
    @OnEvent
    public void aggregate() {        
        int expiredBuckete = timeReset == null ? 1 : timeReset.getWindowsExpired();
        if(expiredBuckete==0){
            return;
        }
        Stateful popped1 = deque.poll();
        aggregator.deduct(popped1);
        for (int i = 1; i < expiredBuckete; i++) {
            Stateful popped2 = deque.poll();
            aggregator.deduct(popped2);
            popped2.reset();
            deque.add(popped2);
        }
        popped1.reset();
        popped1.combine(source);
        deque.add(popped1);
        //add
        aggregator.combine(source);
        source.reset(); 
        
    }

    @Override
    public T event() {
        return (T)aggregator;
    }

    @Override
    public Class<T> eventClass() {
        return clazz;
    }

    @Initialise
    public void init() {
        try {
            deque = new ArrayDeque<>(size);
            aggregator = source.getClass().getDeclaredConstructor().newInstance();
            aggregator.reset();
            for (int i = 0; i < size; i++) {
                final Stateful function = source.getClass().getDeclaredConstructor().newInstance();
                function.reset();
                deque.add(function);
            }
        } catch (Exception ex) {
            throw new RuntimeException("missing default constructor for:" + source.getClass());
        }
    }

}
