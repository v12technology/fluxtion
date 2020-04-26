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
import com.fluxtion.ext.streaming.api.WrappedList;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import com.fluxtion.ext.streaming.api.stream.CollectionFunctions;
import static com.fluxtion.ext.streaming.api.stream.CollectionFunctions.listSum;
import static com.fluxtion.ext.streaming.api.util.NumberComparator.numberComparator;
import com.fluxtion.ext.streaming.builder.factory.Duration;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.cumSum;
import static com.fluxtion.ext.streaming.builder.factory.WindowBuilder.sliding;
import static com.fluxtion.ext.streaming.builder.factory.WindowBuilder.tumble;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.avg;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class SlidingTest extends StreamInprocessTest {

    @Test
    public void countSliding() {
        fixedPkg = true;
        final int BUCKET_SIZE = 6;
        final int BUCKET_SIZE_LARGER = 9;
        final int BUCKET_COUNT = 2;
        final int BUCKET_COUNT_LARGER = 4;
//        final int TIME_WINDOW = 25;
        sep(c -> {
            Wrapper<Double> doubleIn = select(Double.class);
            sliding(doubleIn.collect(), BUCKET_SIZE, BUCKET_COUNT).id("listSmallWindow");//.console("listSmallWindow:");
            sliding(doubleIn.collect(), BUCKET_SIZE_LARGER, BUCKET_COUNT_LARGER).id("listLargeWindow");//.console("listLargeWindow:");
            sliding(doubleIn, avg(), BUCKET_SIZE, BUCKET_COUNT).id("avgSmallWindow");//.console("avgWrappedList: ", Number::doubleValue);
            sliding(doubleIn, cumSum(), BUCKET_SIZE_LARGER, BUCKET_COUNT_LARGER).id("cumSumLargeWindow");//.console("cumSumLargeWindow: ", Number::doubleValue);
        });

//        sep(com.fluxtion.ext.declarative.builder.window.slidingtest_counttumble.TestSep_countTumble.class);
        WrappedList<Number> listSmallWindow = getField("listSmallWindow");
        WrappedList<Number> listLargeWindow = getField("listLargeWindow");
        Number avgSmallWindow = getWrappedField("avgSmallWindow");
        Number cumSumLargeWindow = getWrappedField("cumSumLargeWindow");

        for (int i = 0; i < 6; i++) {
            onEvent(3.0);
        }

        assertEquals(Arrays.asList(3.0, 3.0, 3.0, 3.0, 3.0, 3.0), listSmallWindow.collection());
        assertEquals(Arrays.asList(), listLargeWindow.collection());
        assertEquals(3.0, avgSmallWindow.doubleValue(), 0.001);
        assertEquals(0, cumSumLargeWindow.doubleValue(), 0.001);

        for (int i = 0; i < 6; i++) {
            onEvent(6.0);
        }

        assertEquals(Arrays.asList(3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0), listSmallWindow.collection());
        assertEquals(Arrays.asList(3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 6.0, 6.0, 6.0), listLargeWindow.collection());
        assertEquals(4.5, avgSmallWindow.doubleValue(), 0.001);
        assertEquals(36, cumSumLargeWindow.doubleValue(), 0.001);

        for (int i = 0; i < 9; i++) {
            onEvent(9.0);
        }

        assertEquals(Arrays.asList(6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0), listSmallWindow.collection());
        assertEquals(Arrays.asList(3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0), listLargeWindow.collection());
        assertEquals(7.5, avgSmallWindow.doubleValue(), 0.001);
        assertEquals(108, cumSumLargeWindow.doubleValue(), 0.001);

        onEvent(12.0);

        assertEquals(Arrays.asList(6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0), listSmallWindow.collection());
        assertEquals(Arrays.asList(3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0), listLargeWindow.collection());
        assertEquals(7.5, avgSmallWindow.doubleValue(), 0.001);
        assertEquals(108, cumSumLargeWindow.doubleValue(), 0.001);

        onEvent(12.0);
        onEvent(12.0);
        onEvent(12.0);

        assertEquals(Arrays.asList(9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 12.0, 12.0, 12.0), listSmallWindow.collection());
        assertEquals(Arrays.asList(3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0), listLargeWindow.collection());
        assertEquals(9.75, avgSmallWindow.doubleValue(), 0.001);
        assertEquals(108, cumSumLargeWindow.doubleValue(), 0.001);

        for (int i = 0; i < 12; i++) {
            onEvent(12.0);
        }
        assertEquals(Arrays.asList(
                3.0, 3.0, 3.0, 3.0, 3.0, 3.0,
                6.0, 6.0, 6.0, 6.0, 6.0, 6.0,
                9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0,
                12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0
        ), listLargeWindow.collection());

    }

    @Test
    public void comparatorFilteredTest() {
        sep(c -> {
            WrappedList<Integer> sc = sliding(select(Integer.class).collect(), 6, 2).id("slidingWindow");
            WrappedList<Integer> filteredList = sc.comparator(numberComparator()).reverse().top(4).id("topFilteredWindow");//.console("Filtered sliding: ")
//            filteredList.map(listSum()).id("topFilteredSum");//.console("sum filered: ");
            filteredList.map(CollectionFunctions::sumList).id("topFilteredSum");//.console("sum filered: ");
        });
        
        Number topFilteredSum = getWrappedField("topFilteredSum");
        WrappedList<Number> slidingWindow = getField("slidingWindow");
        WrappedList<Number> topFilteredWindow = getField("topFilteredWindow");

        for (int i = 0; i < 14; i++) {
            onEvent(i);
        }
        
        assertEquals(38, topFilteredSum.doubleValue(), 0.001);
        assertEquals(Arrays.asList(11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0), slidingWindow.collection());
        assertEquals(Arrays.asList(11,10,9,8), topFilteredWindow.collection());        
    }

//    @Test
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
