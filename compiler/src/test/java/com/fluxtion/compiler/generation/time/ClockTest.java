/*
 * Copyright (c) 2019, 2024 gregory higgins.
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
package com.fluxtion.compiler.generation.time;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.time.Clock;
import com.fluxtion.runtime.time.ClockStrategy.ClockStrategyEvent;
import com.fluxtion.runtime.time.Tick;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class ClockTest extends MultipleSepTargetInProcessTest {

    public ClockTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testClock() {
        sep(c -> c.addPublicNode(new MyClockProxy(), "proxy"));
        MyClockProxy proxy = getField("proxy");
        Assert.assertEquals(proxy.clock, proxy.clock2);
        MutableNumber n = new MutableNumber();
        onEvent(new ClockStrategyEvent(n::longValue));
        //
        n.set(1);
        Tick tick = new Tick();
        tick.setEventTime(50);
        onEvent(tick);
        onEvent(tick);
        onEvent(tick);
        assertThat(proxy.tickCount, is(3));
        assertThat(proxy.clock.getWallClockTime(), is(1L));
        assertThat(proxy.clock.getEventTime(), is(50L));
        //
        n.set(100);
        assertThat(proxy.clock.getWallClockTime(), is(100L));
        assertThat(proxy.clock.getEventTime(), is(50L));
        //tick
        onEvent(tick);
        assertThat(proxy.clock.getWallClockTime(), is(100L));
        assertThat(proxy.clock.getEventTime(), is(50L));
        //send an event
        n.set(1900);
        onEvent(new TestTimeEvent());
        assertThat(proxy.clock.getWallClockTime(), is(1900L));
        assertThat(proxy.clock.getEventTime(), is(200L));

    }

    @Test
    public void testClockViaApiCall() {
        sep(c -> c.addPublicNode(new MyClockProxy(), "proxy"));
        MyClockProxy proxy = getField("proxy");
        Assert.assertEquals(proxy.clock, proxy.clock2);
        MutableNumber n = new MutableNumber();
        sep.setClockStrategy(n::longValue);
        //
        n.set(1);
        Tick tick = new Tick();
        tick.setEventTime(50);
        onEvent(tick);
        onEvent(tick);
        onEvent(tick);
        assertThat(proxy.tickCount, is(3));
        assertThat(proxy.clock.getWallClockTime(), is(1L));
        assertThat(proxy.clock.getEventTime(), is(50L));
        //
        n.set(100);
        assertThat(proxy.clock.getWallClockTime(), is(100L));
        assertThat(proxy.clock.getEventTime(), is(50L));
        //tick
        onEvent(tick);
        assertThat(proxy.clock.getWallClockTime(), is(100L));
        assertThat(proxy.clock.getEventTime(), is(50L));
        //send an event
        n.set(1900);
        onEvent(new TestTimeEvent());
        assertThat(proxy.clock.getWallClockTime(), is(1900L));
        assertThat(proxy.clock.getEventTime(), is(200L));

    }

    public static class TestTimeEvent implements Event {

        @Override
        public long getEventTime() {
            return 200;
        }

    }

    public static class NoTimeEvent implements Event {
    }

    public static class MyClockProxy {

        @Inject
        public Clock clock;
        @Inject
        public Clock clock2;
        public int tickCount;

        @OnTrigger
        public boolean update() {
            return true;
        }

        @OnEventHandler
        public boolean tickHandler(Tick e) {
            tickCount++;
            return true;
        }

        @OnEventHandler
        public boolean noTimeEvent(NoTimeEvent e) {
            return true;
        }

        @OnEventHandler
        public boolean testTimedEvent(TestTimeEvent e) {
            return true;
        }

    }
}
