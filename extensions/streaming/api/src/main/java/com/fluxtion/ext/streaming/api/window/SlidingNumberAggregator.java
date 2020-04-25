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

    private final Object notifier;
    private MutableNumber value;
    private MutableNumber emptyValue;
    @NoEventReference
    private final StatefulNumber source;
    private final int size;
    
    private StatefulNumber aggregator;
    private ArrayDeque<StatefulNumber> deque;

    @OnEvent
    public void aggregate() {
        aggregator.combine(source, value);
        StatefulNumber popped = deque.poll();
        aggregator.deduct(popped, value);
        popped.reset();
        popped.combine(source, emptyValue);
        deque.add(popped);
    }

    @Override
    public Number event() {
        return value;
    }

    @Override
    public Class<Number> eventClass() {
        return Number.class;
    }

    @Initialise
    public void init() {
        try {
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
