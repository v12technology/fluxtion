/*
 * Copyright (c) 2019, V12 Technology Ltd.
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
package com.fluxtion.ext.declarative.builder.time;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.event.Event;
import com.fluxtion.api.event.GenericEvent;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import com.fluxtion.ext.streaming.api.time.Clock;
import com.fluxtion.ext.streaming.api.time.ClockStrategy;
import com.fluxtion.ext.streaming.api.time.Tick;
import com.fluxtion.ext.streaming.api.time.TimeEvent;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class ClockTest extends StreamInprocessTest {

    @Test
    public void testClock() {
        sep(c -> c.addPublicNode(new MyClockProxy(), "proxy"));
//        sep(com.fluxtion.ext.declarative.builder.time.clocktest_testclock_1559501154128.TestSep_testClock.class);
        MyClockProxy proxy = getField("proxy");
        MutableNumber n = new MutableNumber();
        onEvent(new GenericEvent(ClockStrategy.class, (ClockStrategy) n::longValue));
        //
        n.set(1);
        onEvent(new Tick());
        onEvent(new Tick());
        onEvent(new Tick());
        assertThat(proxy.tickCount, is(3));
        assertThat(proxy.clock.getWallClockTime(), is(1L));
        assertThat(proxy.clock.getIngestTime(), is(1L));
        assertThat(proxy.clock.getEventTime(), is(1L));
        //
        n.set(100);
        assertThat(proxy.clock.getWallClockTime(), is(100L));
        assertThat(proxy.clock.getIngestTime(), is(1L));
        assertThat(proxy.clock.getEventTime(), is(1L));
        //tick
        onEvent(new Tick());
        assertThat(proxy.clock.getWallClockTime(), is(100L));
        assertThat(proxy.clock.getIngestTime(), is(100L));
        assertThat(proxy.clock.getEventTime(), is(100L));
        //send an event
        n.set(900);
        onEvent(new NoTimeEvent());
        assertThat(proxy.clock.getWallClockTime(), is(900L));
        assertThat(proxy.clock.getIngestTime(), is(900L));
        assertThat(proxy.clock.getEventTime(), is(900L));
        //send an event
        n.set(1900);
        onEvent(new TestTimeEvent());
        assertThat(proxy.clock.getWallClockTime(), is(1900L));
        assertThat(proxy.clock.getIngestTime(), is(1900L));
        assertThat(proxy.clock.getEventTime(), is(200L));

    }

    public static class TestTimeEvent extends Event implements TimeEvent {

        @Override
        public long getEventTime() {
            return 200;
        }

    }
    
    public static class NoTimeEvent extends Event{}

    public static class MyClockProxy {

        @Inject
        public Clock clock;
        public int tickCount;

        @OnEvent
        public void update() {
            tickCount++;
        }
        
        @EventHandler
        public void noTimeEvent(NoTimeEvent e){
            
        }
        
        @EventHandler
        public void testTimedEvent(TestTimeEvent e){
            
        }

    }
}
