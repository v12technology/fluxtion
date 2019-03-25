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

import com.fluxtion.ext.declarative.api.Stateful;
import com.fluxtion.ext.declarative.api.Wrapper;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import static com.fluxtion.ext.declarative.builder.stream.StreamFunctionsBuilder.count;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class StreamingFilterTest extends StreamInprocessTest {

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
