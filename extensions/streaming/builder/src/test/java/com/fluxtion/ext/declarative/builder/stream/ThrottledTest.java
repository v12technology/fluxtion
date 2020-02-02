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
package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.api.event.Event;
import com.fluxtion.api.event.GenericEvent;
import com.fluxtion.ext.declarative.builder.stream.ThrottledTest.MyEvent;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import static com.fluxtion.ext.streaming.api.stream.TimerFilter.throttle;
import com.fluxtion.api.time.ClockStrategy;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.count;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class ThrottledTest extends StreamInprocessTest {

    @Test
    public void testThrottle() {
        sep(c -> {
            select(MyEvent.class)
                    .filter(throttle(10))
                    .map(count()).id("count");
        });

        MutableNumber n = new MutableNumber();
        n.set(1);
        onEvent(new GenericEvent(ClockStrategy.class, (ClockStrategy) n::longValue));
        Number count = getWrappedField("count");
        for (int i = 0; i < 100; i++) {
            onEvent(new MyEvent());
        }
        n.set(20);
        for (int i = 0; i < 100; i++) {
            onEvent(new MyEvent());
        }
        assertThat(count.intValue(), is(1));

    }

    public static class MyEvent extends Event {
    }

}
