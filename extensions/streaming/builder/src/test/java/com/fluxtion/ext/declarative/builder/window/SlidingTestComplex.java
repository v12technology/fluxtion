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

import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.api.time.ClockStrategy;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.ArrayListWrappedCollection;
import com.fluxtion.ext.streaming.api.WrappedList;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import com.fluxtion.ext.streaming.api.stream.CollectionFunctions;
import static com.fluxtion.ext.streaming.api.util.NumberComparator.numberComparator;
import com.fluxtion.ext.streaming.api.Duration;
import com.fluxtion.ext.streaming.api.util.FunctionComparator;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsBuilder.cumSum;
import static com.fluxtion.ext.streaming.builder.factory.WindowBuilder.sliding;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsBuilder.avg;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Function;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class SlidingTestComplex extends StreamInprocessTest {

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
            sliding(doubleIn.collect(), BUCKET_SIZE, BUCKET_COUNT).id("listSmallWindow");//.log("listSmallWindow:");
            sliding(doubleIn.collect(), BUCKET_SIZE_LARGER, BUCKET_COUNT_LARGER).id("listLargeWindow");//.log("listLargeWindow:");
            sliding(doubleIn, avg(), BUCKET_SIZE, BUCKET_COUNT).id("avgSmallWindow");//.log("avgWrappedList: ", Number::doubleValue);
            sliding(doubleIn, cumSum(), BUCKET_SIZE_LARGER, BUCKET_COUNT_LARGER).id("cumSumLargeWindow");//.log("cumSumLargeWindow: ", Number::doubleValue);
        });

//        sep(com.fluxtion.ext.declarative.builder.window.slidingtest_counttumble.TestSep_countTumble.class);

        for (int i = 0; i < 6; i++) {
            onEvent(3.0);
        }
        WrappedList<Number> listSmallWindow = getField("listSmallWindow");
        WrappedList<Number> listLargeWindow = getField("listLargeWindow");
        Number avgSmallWindow = getWrappedField("avgSmallWindow");
        Number cumSumLargeWindow = getWrappedField("cumSumLargeWindow");

        assertEquals(Arrays.asList(3.0, 3.0, 3.0, 3.0, 3.0, 3.0), listSmallWindow.collection());
        assertEquals(Arrays.asList(), listLargeWindow.collection());
        assertEquals(0.0, avgSmallWindow.doubleValue(), 0.001);
        assertEquals(0, cumSumLargeWindow.doubleValue(), 0.001);

        for (int i = 0; i < 6; i++) {
            onEvent(6.0);
        }
        listSmallWindow = getField("listSmallWindow");
        listLargeWindow = getField("listLargeWindow");
        avgSmallWindow = getWrappedField("avgSmallWindow");
        cumSumLargeWindow = getWrappedField("cumSumLargeWindow");

        assertEquals(Arrays.asList(3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0), listSmallWindow.collection());
        assertEquals(Arrays.asList(3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 6.0, 6.0, 6.0), listLargeWindow.collection());
        assertEquals(4.5, avgSmallWindow.doubleValue(), 0.001);
        assertEquals(0, cumSumLargeWindow.doubleValue(), 0.001);

        for (int i = 0; i < 9; i++) {
            onEvent(9.0);
        }
        listSmallWindow = getField("listSmallWindow");
        listLargeWindow = getField("listLargeWindow");
        avgSmallWindow = getWrappedField("avgSmallWindow");
        cumSumLargeWindow = getWrappedField("cumSumLargeWindow");

        assertEquals(Arrays.asList(6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0), listSmallWindow.collection());
        assertEquals(Arrays.asList(3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0), listLargeWindow.collection());
        assertEquals(7.5, avgSmallWindow.doubleValue(), 0.001);
        assertEquals(0, cumSumLargeWindow.doubleValue(), 0.001);

        onEvent(12.0);
        listSmallWindow = getField("listSmallWindow");
        listLargeWindow = getField("listLargeWindow");
        avgSmallWindow = getWrappedField("avgSmallWindow");
        cumSumLargeWindow = getWrappedField("cumSumLargeWindow");

        assertEquals(Arrays.asList(6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0), listSmallWindow.collection());
        assertEquals(Arrays.asList(3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0), listLargeWindow.collection());
        assertEquals(7.5, avgSmallWindow.doubleValue(), 0.001);
        assertEquals(0, cumSumLargeWindow.doubleValue(), 0.001);

        onEvent(12.0);
        onEvent(12.0);
        onEvent(12.0);
        listSmallWindow = getField("listSmallWindow");
        listLargeWindow = getField("listLargeWindow");
        avgSmallWindow = getWrappedField("avgSmallWindow");
        cumSumLargeWindow = getWrappedField("cumSumLargeWindow");

        assertEquals(Arrays.asList(9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 12.0, 12.0, 12.0), listSmallWindow.collection());
        assertEquals(Arrays.asList(3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0), listLargeWindow.collection());
        assertEquals(9.75, avgSmallWindow.doubleValue(), 0.001);
        assertEquals(0, cumSumLargeWindow.doubleValue(), 0.001);

        for (int i = 0; i < 12; i++) {
            onEvent(12.0);
        }
        listSmallWindow = getField("listSmallWindow");
        listLargeWindow = getField("listLargeWindow");
        avgSmallWindow = getWrappedField("avgSmallWindow");
        cumSumLargeWindow = getWrappedField("cumSumLargeWindow");
        assertEquals(Arrays.asList(
            3.0, 3.0, 3.0, 3.0, 3.0, 3.0,//sum:18
            6.0, 6.0, 6.0, 6.0, 6.0, 6.0,//sum:36
            9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0, 9.0,//sum:81
            12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0//sum:180
        ), listLargeWindow.collection());
        assertEquals(315, cumSumLargeWindow.doubleValue(), 0.001);

    }

    public static class StringComparator implements Comparator<String>{

        @Override
        public int compare(String o1, String o2) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    }
    
    @Test
    public void comparatorFilteredTest() {
        sep(c -> {
            sliding(Integer.class, 6, 2).id("slidingWindow")
                .comparator(numberComparator()).reverse().top(4).id("topFilteredWindow")
                .map(CollectionFunctions::sumList).id("topFilteredSum");
        });

        Number topFilteredSum = getWrappedField("topFilteredSum");
        WrappedList<Number> slidingWindow = getField("slidingWindow");
        WrappedList<Number> topFilteredWindow = getField("topFilteredWindow");

        for (int i = 0; i < 14; i++) {
            onEvent(i);
        }

        assertEquals(38, topFilteredSum.doubleValue(), 0.001);
        assertEquals(Arrays.asList(11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0), slidingWindow.collection());
        assertEquals(Arrays.asList(11, 10, 9, 8), topFilteredWindow.collection());
    }
    
    @Test
    public void comparatorFunctionTest() {
        sep(c -> {
            select(Integer.class)
                    .sliding(6, 2).id("slidingWindow")
                    .comparing(Integer::intValue).reverse().top(4).id("topFilteredWindow")
                    .map(CollectionFunctions::sumList).id("topFilteredSum");
        });

        Number topFilteredSum = getWrappedField("topFilteredSum");
        WrappedList<Number> slidingWindow = getField("slidingWindow");
        WrappedList<Number> topFilteredWindow = getField("topFilteredWindow");

        for (int i = 0; i < 14; i++) {
            onEvent(i);
        }

        assertEquals(38, topFilteredSum.doubleValue(), 0.001);
        assertEquals(Arrays.asList(11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0), slidingWindow.collection());
        assertEquals(Arrays.asList(11, 10, 9, 8), topFilteredWindow.collection());
    }

    @Test
    public void timeSliding() {
        final int TIME_WINDOW = 25;
        final int BUCKETS = 4;
        final int TIME_WINDOW_LARGER = 90;
        final int BUCKETS_LARGER = 14;
        sep(c -> {
            sliding(Double.class, Duration.seconds(TIME_WINDOW), BUCKETS).id("listWindow");
            sliding(Double.class, cumSum(), Duration.seconds(TIME_WINDOW_LARGER), BUCKETS_LARGER).id("cumWindow");
        });

        WrappedList<Number> listWindow = getField("listWindow");
        Number cumWindow = getWrappedField("cumWindow");

        MutableNumber n = new MutableNumber();
        n.set(1);
        onEvent(new ClockStrategy.ClockStrategyEvent(n::longValue));

        for (int i = 0; i < 6; i++) {
            onEvent((double) i);
        }
        assertEquals(Arrays.asList(), listWindow.collection());
        assertEquals(0, cumWindow.doubleValue(), 0.001);

        n.set(50_000);
        onEvent(new Object());
        assertEquals(Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0, 5.0), listWindow.collection());
        assertEquals(0, cumWindow.doubleValue(), 0.001);

        n.set(95_000);
        onEvent(new Object());
        listWindow = getField("listWindow");
        cumWindow = getWrappedField("cumWindow");
        assertEquals(Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0, 5.0), listWindow.collection());
        assertEquals(0, cumWindow.doubleValue(), 0.001);

        n.set(195_000);
        onEvent(new Object());
        listWindow = getField("listWindow");
        cumWindow = getWrappedField("cumWindow");
        assertEquals(Arrays.asList(), listWindow.collection());
        assertEquals(0.0, cumWindow.doubleValue(), 0.001);

        onEvent(50.0);
        n.set(280_000);
        onEvent(new Object());
        listWindow = getField("listWindow");
        cumWindow = getWrappedField("cumWindow");
        assertEquals(0, cumWindow.doubleValue(), 0.001);

        n.set(1_380_000);
        onEvent(new Object());
        listWindow = getField("listWindow");
        cumWindow = getWrappedField("cumWindow");
        assertEquals(50, cumWindow.doubleValue(), 0.001);

    }

}
