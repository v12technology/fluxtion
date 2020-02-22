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
package com.fluxtion.generator.time;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.event.Event;
import com.fluxtion.api.event.GenericEvent;
import com.fluxtion.api.time.Clock;
import com.fluxtion.api.time.ClockStrategy;
import com.fluxtion.api.time.Tick;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class ClockTest extends BaseSepInprocessTest {

    @Test
    public void testClock() {
        sep(c -> c.addPublicNode(new MyClockProxy(), "proxy"));
//        sep(com.fluxtion.ext.declarative.builder.time.clocktest_testclock_1559501154128.TestSep_testClock.class);
        MyClockProxy proxy = getField("proxy");
        Assert.assertEquals(proxy.clock, proxy.clock2);
        MutableNumber n = new MutableNumber();
        onEvent(new GenericEvent(ClockStrategy.class, (ClockStrategy) n::longValue));
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

    public static class TestTimeEvent implements Event  {

        @Override
        public long getEventTime() {
            return 200;
        }

    }
    
    public static class NoTimeEvent implements Event{}

    public static class MyClockProxy {

        @Inject
        public Clock clock;
        @Inject
        public Clock clock2;
        public int tickCount;

        @OnEvent
        public void update() {
        }
        
        @EventHandler
        public void tickHandler(Tick e){
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
