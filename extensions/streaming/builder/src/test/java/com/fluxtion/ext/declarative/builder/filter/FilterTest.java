
/*
 * Copyright (C) 2019 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.declarative.builder.filter;

import com.fluxtion.ext.declarative.builder.helpers.DataEvent;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.gt;
import static com.fluxtion.ext.streaming.builder.factory.FilterBuilder.filter;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.count;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.cumSum;
import static com.fluxtion.ext.streaming.builder.factory.TestBuilder.test;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
@Slf4j
public class FilterTest extends StreamInprocessTest {

    @Test
    public void testOnNotifyControl() {
        sep((c) -> {
            cumSum(DataEvent::getValue).id("sum")
                    .filter(gt(10))
                    .map(count()).id("count");
        });

        Number count = getWrappedField("count");
        Number sum = getWrappedField("sum");
        DataEvent de1 = new DataEvent();
        de1.value = 2;
        sep.onEvent(de1);
        sep.onEvent(de1);
        assertThat(sum.intValue(), is(4));
        assertThat(count.intValue(), is(0));

        de1.value = 10;
        sep.onEvent(de1);
        assertThat(sum.intValue(), is(14));
        assertThat(count.intValue(), is(1));
    }

    @Test
    public void testFilterByProp() {
        sep((c) -> {
            filter(DataEvent::getValue, gt(10)).map(count()).id("count");
        });
        Number count = getWrappedField("count");
        onEvent(new DataEvent(10));
        assertThat(count.intValue(), is(0));
        onEvent(new DataEvent(12));
        assertThat(count.intValue(), is(1));
    }

    @Test
    public void testInstanceFilter(){
        sep((c) -> {
            filter("matched"::equalsIgnoreCase).map(count()).id("count");
        });
        Number count = getWrappedField("count");
        onEvent(new DataEvent(10));
        assertThat(count.intValue(), is(0));
        onEvent("not matched");
        assertThat(count.intValue(), is(0));
        onEvent("matched");
        assertThat(count.intValue(), is(1));        
    }
    
    @Test
    public void complexNaryTest() {
        sep((c) -> {
            filter(DataEvent.class,
                    test(FilterTest::withinRange, DataEvent::getValue, MinAge::getMin, MaxAge::getMax))
                    .map(count()).id("count");
        });

        Number count = getWrappedField("count");
        onEvent(new DataEvent(10));
        assertThat(count.intValue(), is(0));

        onEvent(new MinAge(7));
        onEvent(new DataEvent(10));
        assertThat(count.intValue(), is(0));

        onEvent(new MaxAge(17));
        onEvent(new DataEvent(10));
        assertThat(count.intValue(), is(1));

        onEvent(new DataEvent(20));
        onEvent(new DataEvent(2));
        assertThat(count.intValue(), is(1));

        onEvent(new MaxAge(37));
        onEvent(new DataEvent(20));
        assertThat(count.intValue(), is(2));

    }

    @Test
    public void testNaryTest() {
        sep((c) -> {
            filter(DataEvent.class,
                    test(FilterTest::withinRange, Timenow::getNow, MinAge::getMin, MaxAge::getMax))
                    .map(count()).id("count");
        });

        Number count = getWrappedField("count");
        onEvent(new DataEvent(11));
        onEvent(new MinAge(7));
        onEvent(new DataEvent(12));
        onEvent(new MaxAge(17));
        onEvent(new DataEvent(13));
        onEvent(new Timenow(10));
        assertThat(count.intValue(), is(0));

        onEvent(new DataEvent(14));
        assertThat(count.intValue(), is(1));

        onEvent(new DataEvent(20));
        assertThat(count.intValue(), is(2));

        onEvent(new Timenow(25));
        onEvent(new DataEvent(2));
        assertThat(count.intValue(), is(2));

        onEvent(new MaxAge(37));
        onEvent(new DataEvent(22));
        assertThat(count.intValue(), is(3));

    }

    public static boolean withinRange(int test, int min, int max) {
        return min < test && test < max;
    }

    @Data
    public static class MinAge {

        final int min;
    }

    @Data
    public static class MaxAge {

        final int max;
    }

    @Data
    public static class Timenow {

        final int now;
    }

}
