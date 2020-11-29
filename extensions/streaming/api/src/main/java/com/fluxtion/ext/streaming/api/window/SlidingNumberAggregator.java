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
import com.fluxtion.api.annotations.SepNode;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import java.util.ArrayDeque;
import lombok.Data;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Data
public class SlidingNumberAggregator implements Wrapper<Number> {

    @SepNode
    private final Object notifier;
    @NoEventReference
    @SepNode
    private final StatefulNumber source;
    private final int size;
    
    @NoEventReference
    @SepNode
    private TimeReset timeReset;
    private MutableNumber value;
    private MutableNumber emptyValue;
    private StatefulNumber aggregator;
    private ArrayDeque<StatefulNumber> deque;
    private int publishCount;


    @OnEvent
    public boolean aggregate() {
        int expiredBuckete = timeReset == null ? 1 : timeReset.getWindowsExpired();
        if(expiredBuckete==0){
            return false;
        }
        publishCount += expiredBuckete;
        publishCount = Math.min(size+1, publishCount);
        StatefulNumber popped1 = deque.poll();
        aggregator.deduct(popped1, value);
        for (int i = 1; i < expiredBuckete; i++) {
            StatefulNumber popped2 = deque.poll();
            aggregator.deduct(popped2, value);
            popped2.reset();
            deque.add(popped2);
        }
        popped1.reset();
        popped1.combine(source, emptyValue);
        deque.add(popped1);
        //add
        aggregator.combine(source, value);
        source.reset();
        return publishCount >=size;
    }

    @Override
    public Number event() {
        return publishCount >=size?value:0;
    }

    @Override
    public Class<Number> eventClass() {
        return Number.class;
    }

    @Initialise
    public void init() {
        try {
            publishCount = 0;
            value = new MutableNumber();
            emptyValue = new MutableNumber();
            deque = new ArrayDeque<>(size);
            aggregator = source.getClass().getDeclaredConstructor().newInstance();
            aggregator.reset();
            for (int i = 0; i < size; i++) {
                final StatefulNumber function = source.getClass().getDeclaredConstructor().newInstance();
                function.reset();
                deque.add(function);
            }
        } catch (Exception ex) {
            throw new RuntimeException("missing default constructor for:" + source.getClass());
        }
    }

}
