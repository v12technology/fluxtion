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
package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.ext.streaming.api.Wrapper;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.ceil;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.cumSum;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class FunctionsTest extends StreamInprocessTest {

    @Test
    public void mapRef2Ref() {
        sep((c) -> {
            Wrapper<StreamData> in = select(StreamData.class);
            in.map(cumSum(), StreamData::getIntValue);

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
        assertThat(countStatic .event().intValue(), is(2));
        onEvent(new StreamData(-10));
        assertThat(count.event().intValue(), is(2));
        assertThat(countStatic .event().intValue(), is(2));
    }

    @Test
    public void testReflection() {
        sep((c) -> {
            cumSum(StreamData::getDoubleValue);
            cumSum(new StreamData()::getIntValue);
        });
        //TODO write tests
    }

    @Test
    public void testReflectionStatic() {
        sep((c) -> {
            ceil(new StreamData()::getDoubleValue);
        });
        //TODO write tests
    }
}
