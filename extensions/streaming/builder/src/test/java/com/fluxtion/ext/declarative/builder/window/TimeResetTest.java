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
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import com.fluxtion.ext.streaming.api.window.TimeReset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class TimeResetTest {

    private Clock clock;
    private StatefulTest stateMock;
    private MutableNumber time;

    @Before
    public void init() {
        time = new MutableNumber();
        clock = new Clock();
        clock.setClockStrategy(new ClockStrategy.ClockStrategyEvent(time::longValue));
        time.set(1);
        stateMock = new StatefulTest();

    }

    @Test
    public void testNoExpire() {
        TimeReset timer = new TimeReset(stateMock, 100, clock);
        timer.anyEvent("");
        assertEquals(0, timer.getWindowsExpired());
        assertFalse(stateMock.resetState);
    }

    @Test
    public void testSingleExpire() {
        TimeReset timer = new TimeReset(stateMock, 100, clock);
        assertEquals(0, timer.getWindowsExpired());
        assertFalse(stateMock.resetState);
        setTime(timer, 120);
        assertEquals(1, timer.getWindowsExpired());
        timer.resetIfNecessary();
        assertTrue(stateMock.resetState);
    }

    @Test
    public void testMultipleExpire() {
        TimeReset timer = new TimeReset(stateMock, 100, clock);
        assertEquals(0, timer.getWindowsExpired());
        assertFalse(stateMock.resetState);
        setTime(timer, 520);
        assertEquals(5, timer.getWindowsExpired());
        timer.resetIfNecessary();
        assertTrue(stateMock.resetState);
    }

    @Test
    public void testMultipleTimesNoExpire() {
        TimeReset timer = new TimeReset(stateMock, 100, clock);
        assertEquals(0, timer.getWindowsExpired());
        assertFalse(stateMock.resetState);
        setTime(timer, 520);
        assertEquals(5, timer.getWindowsExpired());
        timer.resetIfNecessary();
        assertTrue(stateMock.resetState);
        stateMock.init();
        
        setTime(timer, 530);
        assertEquals(0, timer.getWindowsExpired());
        timer.resetIfNecessary();
        assertFalse(stateMock.resetState);
        
        setTime(timer, 560);
        assertEquals(0, timer.getWindowsExpired());
        timer.resetIfNecessary();
        assertFalse(stateMock.resetState);

        setTime(timer, 590);
        assertEquals(0, timer.getWindowsExpired());
        timer.resetIfNecessary();
        assertFalse(stateMock.resetState);


        setTime(timer, 600);
        assertEquals(1, timer.getWindowsExpired());
        timer.resetIfNecessary();
        assertTrue(stateMock.resetState);

    }

    private void setTime(TimeReset timer, long newTime) {
        time.set(newTime);
        timer.anyEvent("");
    }

    public static class StatefulTest implements Stateful<Object> {

        public boolean resetState = false;

        @Override
        public void reset() {
            resetState = true;
        }

        public void init() {
            resetState = false;
        }
    }

}
