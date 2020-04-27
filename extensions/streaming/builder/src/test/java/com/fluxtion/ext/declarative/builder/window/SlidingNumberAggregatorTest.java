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
package com.fluxtion.ext.declarative.builder.window;

import com.fluxtion.api.time.Clock;
import com.fluxtion.api.time.ClockStrategy;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions.Sum;
import com.fluxtion.ext.streaming.api.window.SlidingNumberAggregator;
import com.fluxtion.ext.streaming.api.window.TimeReset;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class SlidingNumberAggregatorTest {

    private Clock clock;
    private TimeResetTest.StatefulTest stateMock;
    private MutableNumber time;
    private TimeReset timer;

    @Before
    public void init() {
        time = new MutableNumber();
        clock = new Clock();
        clock.setClockStrategy(new ClockStrategy.ClockStrategyEvent(time::longValue));
        time.set(1);
        stateMock = new TimeResetTest.StatefulTest();
        timer = new TimeReset(stateMock, 100, clock);

    }

    @Test
    public void testCombine() {
        Object notifier = "";
        Sum sum = new Sum();
        int size = 10;

        SlidingNumberAggregator aggregator = new SlidingNumberAggregator(notifier, sum, size);
        aggregator.setTimeReset(timer);
        aggregator.init();

        sum.addValue(10);
        validateValue(101, 10, aggregator);

        for (int i = 2; i < 21; i++) {
            sum.addValue(10);
            setTime(i * 100 + 1);
            aggregator.aggregate();
            timer.resetIfNecessary();
        }
        Assert.assertThat(aggregator.event().intValue(), is(100));
        validateValue(2050, 100, aggregator);
        validateValue(2_101, 90, aggregator);
        validateValue(2_501, 50, aggregator);
        validateValue(25_001, 0, aggregator);
    }
    
    private void validateValue(int time, int value, SlidingNumberAggregator aggregator){
        setTime(time);
        aggregator.aggregate();
        Assert.assertThat(aggregator.event().intValue(), is(value));
        timer.resetIfNecessary();
    }

    private void setTime(long newTime) {
        time.set(newTime);
        timer.anyEvent("");
    }
}
