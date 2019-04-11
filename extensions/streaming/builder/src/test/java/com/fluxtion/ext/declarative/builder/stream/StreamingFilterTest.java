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

import com.fluxtion.ext.streaming.api.FilterWrapper;
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.Wrapper;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.gt;
import static com.fluxtion.ext.streaming.builder.event.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.stream.StreamFunctionsBuilder.count;
import static com.fluxtion.ext.streaming.builder.stream.StreamFunctionsBuilder.cumSum;
import static com.fluxtion.ext.streaming.builder.stream.StreamFunctionsBuilder.multiply;
import static com.fluxtion.ext.streaming.builder.util.FunctionArg.arg;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class StreamingFilterTest extends StreamInprocessTest {

    @Test
    public void elseNotifyOnce() {
//        fixedPkg = true;
        sep((c) -> {
            FilterWrapper<StreamData> filter = select(StreamData.class)
                    .filter(StreamData::getIntValue, gt(10)).notifyOnChange(true);
            //if - count
            filter.map(count()).id("filterCount");
            //else - count
            filter.elseStream().notifyOnChange(true).map(count()).id("elseCount");
        });
        
        Number filterCount = getWrappedField("filterCount");
        Number elseCount = getWrappedField("elseCount");
        onEvent(new StreamData(9));
        assertThat(filterCount.intValue(), is(0));
        assertThat(elseCount.intValue(), is(1));
        
        onEvent(new StreamData(9));
        assertThat(filterCount.intValue(), is(0));
        assertThat(elseCount.intValue(), is(1));
        
        onEvent(new StreamData(9));
        assertThat(filterCount.intValue(), is(0));
        assertThat(elseCount.intValue(), is(1));
        
        onEvent(new StreamData(19));
        assertThat(filterCount.intValue(), is(1));
        assertThat(elseCount.intValue(), is(1));
        
        onEvent(new StreamData(9));
        assertThat(filterCount.intValue(), is(1));
        assertThat(elseCount.intValue(), is(2));

        onEvent(new StreamData(19));
        assertThat(filterCount.intValue(), is(2));
        assertThat(elseCount.intValue(), is(2));
        
        onEvent(new StreamData(19));
        assertThat(filterCount.intValue(), is(2));
        assertThat(elseCount.intValue(), is(2));
        
        onEvent(new StreamData(9));
        assertThat(filterCount.intValue(), is(2));
        assertThat(elseCount.intValue(), is(3));
        
        onEvent(new StreamData(19));
        assertThat(filterCount.intValue(), is(3));
        assertThat(elseCount.intValue(), is(3));
        
        onEvent(new StreamData(19));
        assertThat(filterCount.intValue(), is(3));
        assertThat(elseCount.intValue(), is(3));
        
        onEvent(new StreamData(19));
        assertThat(filterCount.intValue(), is(3));
        assertThat(elseCount.intValue(), is(3));
    }

    @Test
    public void testElse() {
        sep((c) -> {
            FilterWrapper<StreamData> filter = select(StreamData.class)
                    .filter(StreamData::getIntValue, gt(10));
            filter.map(multiply(), StreamData::getIntValue, 10).id("x10").map(cumSum()).id("cumSum");
            multiply(arg(filter, StreamData::getIntValue), arg(10)).id("x10_2").map(cumSum()).id("cumSum2");
            //if - count
            filter.map(count()).id("filterCount");
            //else - count
            filter.elseStream().map(count()).id("elseCount");
        });

        Number filterCount = getWrappedField("filterCount");
        Number elseCount = getWrappedField("elseCount");
        Number cumSum = getWrappedField("cumSum");
        Number cumSum2 = getWrappedField("cumSum2");
        onEvent(new StreamData(89));
        assertThat(filterCount.intValue(), is(1));
        assertThat(elseCount.intValue(), is(0));
        assertThat(cumSum.intValue(), is(890));
        assertThat(cumSum2.intValue(), is(890));

        onEvent(new StreamData(9));
        assertThat(filterCount.intValue(), is(1));
        assertThat(elseCount.intValue(), is(1));
        assertThat(cumSum.intValue(), is(890));
        assertThat(cumSum2.intValue(), is(890));

        onEvent(new StreamData(9));
        assertThat(filterCount.intValue(), is(1));
        assertThat(elseCount.intValue(), is(2));
        assertThat(cumSum.intValue(), is(890));
        assertThat(cumSum2.intValue(), is(890));

        onEvent(new StreamData(19));
        assertThat(filterCount.intValue(), is(2));
        assertThat(elseCount.intValue(), is(2));
        assertThat(cumSum.intValue(), is(1080));
        assertThat(cumSum2.intValue(), is(1080));
    }

    @Test
    public void mapRef2Ref() {
//        fixedPkg = true;
        sep((c) -> {
            Wrapper<StreamData> in = select(StreamData.class);
            in.filter(StreamData::getIntValue, FilterFunctions::posStatic)
                    .map(new MapFunctions()::count).id("countStatic");
            in.filter(StreamData::getIntValue, new FilterFunctions()::positive).id("data")
                    .map(new MapFunctions()::count).id("count");
        });
        Wrapper<StreamData> data = getField("data");
        Wrapper<Number> count = getField("count");
        Wrapper<Number> countStatic = getField("countStatic");
        onEvent(new StreamData(89));
        assertThat(count.event().intValue(), is(1));
        assertThat(countStatic.event().intValue(), is(1));
        onEvent(new StreamData(89));
        assertThat(count.event().intValue(), is(2));
        assertThat(countStatic.event().intValue(), is(2));
        onEvent(new StreamData(-10));
        assertThat(count.event().intValue(), is(2));
        assertThat(countStatic.event().intValue(), is(2));
    }

    @Test
    public void countIntervals() {
        sep((c) -> {
            select(StreamData.class)
                    .filter(new FilterIntervalCount(3)::interval)
                    .map(count()).id("intervalCount");
        });
        Wrapper<Number> count = getField("intervalCount");
        assertThat(count.event().intValue(), is(0));
        //1
        onEvent(new StreamData(1));
        assertThat(count.event().intValue(), is(0));

        //2
        onEvent(new StreamData(1));
        assertThat(count.event().intValue(), is(0));

        //3
        onEvent(new StreamData(1));
        assertThat(count.event().intValue(), is(1));

        //4
        onEvent(new StreamData(1));
        assertThat(count.event().intValue(), is(1));

        //5
        onEvent(new StreamData(1));
        assertThat(count.event().intValue(), is(1));

        //6
        onEvent(new StreamData(1));
        assertThat(count.event().intValue(), is(2));
    }

    public static class FilterIntervalCount implements Stateful {

        private final int interval;
        private int count = 0;

        public FilterIntervalCount(int interval) {
            this.interval = interval;
        }

        public <T> boolean interval(T o) {
            return (++count) % interval == 0;
        }

        @Override
        public void reset() {
            count = 0;
        }

    }
}
