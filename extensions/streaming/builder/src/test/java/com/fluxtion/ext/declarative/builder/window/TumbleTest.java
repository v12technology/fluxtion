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

import com.fluxtion.api.time.ClockStrategy;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.WrappedCollection;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import com.fluxtion.ext.streaming.builder.factory.Duration;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.avg;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.cumSum;
import static com.fluxtion.ext.streaming.builder.factory.WindowBuilder.tumble;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class TumbleTest extends StreamInprocessTest {
    
    @Test
    @Ignore
    public void countTumble() {
        //TODO fix this test - tumble windows are broken
        final int COUNT_SIZE = 6;
        final int COUNT_SIZE_LARGER = 9;
//        final int TIME_WINDOW = 25;
        sep(c -> {
            Wrapper<Double> doubleIn = select(Double.class);
            tumble(doubleIn.collect(), COUNT_SIZE).id("listSmallWindow");
            tumble(doubleIn.collect(), COUNT_SIZE_LARGER).id("listLargeWindow");
//            tumble(doubleIn.collect(), Duration.millis(TIME_WINDOW));
            tumble(doubleIn.map(avg()), COUNT_SIZE).id("avgSmallWindow");
            tumble(doubleIn.map(cumSum()), COUNT_SIZE_LARGER).id("cumsumLargeWindow");
//            tumble(doubleIn.map(cumSum()), Duration.millis(TIME_WINDOW));
        });

        WrappedCollection<Number, ?, ?> listSmallWindow = getWrappedField("listSmallWindow");
        WrappedCollection<Number, ?, ?> listLargeWindow = getWrappedField("listLargeWindow");
        Number avgSmallWindow = getWrappedField("avgSmallWindow");
        Number cumsumLargeWindow = getWrappedField("cumsumLargeWindow");

        for (int i = 0; i < 6; i++) {
            onEvent((double) i);
        }
        assertEquals(listSmallWindow.collection(), Arrays.asList());
        assertEquals(listLargeWindow.collection(), Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0, 5.0));
        assertEquals(avgSmallWindow.doubleValue(), 2.5, 0.001);
        assertEquals(cumsumLargeWindow.doubleValue(), 15, 0.001);

        onEvent((double) 5);
        onEvent((double) 6);
        onEvent((double) 4);
        assertEquals(listSmallWindow.collection(), Arrays.asList(5.0, 6.0, 4.0));
        assertEquals(listLargeWindow.collection(), Arrays.asList());
        assertEquals(avgSmallWindow.doubleValue(), 5, 0.001);
        assertEquals(cumsumLargeWindow.doubleValue(), 30, 0.001);
    }

    @Test
    public void timeTumble() {
        final int TIME_WINDOW = 25;
        final int TIME_WINDOW_LARGER = 90;
        sep(c -> {
            Wrapper<Double> doubleIn = select(Double.class);
            tumble(doubleIn.collect(), Duration.millis(TIME_WINDOW)).id("listWindow");
            tumble(doubleIn.map(cumSum()), Duration.millis(TIME_WINDOW_LARGER)).id("cumWindow");
        });

        WrappedCollection<Number, ?, ?> listWindow = getWrappedField("listWindow");
        Number cumWindow = getWrappedField("cumWindow");

        MutableNumber n = new MutableNumber();
        n.set(1);
        onEvent(new ClockStrategy.ClockStrategyEvent(n::longValue));

        for (int i = 0; i < 6; i++) {
            onEvent((double) i);
        }
        assertEquals(listWindow.collection(), Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0, 5.0));
        assertEquals(cumWindow.doubleValue(), 15, 0.001);

        n.set(50);
        onEvent(new ClockStrategy.ClockStrategyEvent(n::longValue));
        onEvent((double) 5);
        onEvent((double) 6);
        onEvent((double) 4);
        assertEquals(listWindow.collection(), Arrays.asList(5.0, 6.0, 4.0));
        assertEquals(cumWindow.doubleValue(), 30, 0.001);

        n.set(100);
        onEvent(new ClockStrategy.ClockStrategyEvent(n::longValue));
        onEvent((double) 6);
        assertEquals(listWindow.collection(), Arrays.asList(6.0));
        assertEquals(cumWindow.doubleValue(), 6, 0.001);
    }

}
