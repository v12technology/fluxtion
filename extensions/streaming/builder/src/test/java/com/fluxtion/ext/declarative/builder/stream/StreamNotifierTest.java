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

import com.fluxtion.ext.streaming.api.Wrapper;
import org.junit.Test;

import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author V12 Technology Ltd.
 */
public class StreamNotifierTest  extends StreamInprocessTest {

    @Test
    public void testOnNotifyControl(){
        sep((c) -> {
            Wrapper<StreamData> in = select(StreamData.class);
            in.filter(StreamData::getIntValue, FilterFunctions::posStatic)
                    .map(new MapFunctions()::count).id("countStatic");
            in.filter(StreamData::getIntValue, new FilterFunctions()::positive).id("data")
                    .map(new MapFunctions()::count).id("count");
        });
        Number count = getWrappedField("count");
        Number countStatic = getWrappedField("countStatic");
        onEvent(new StreamData(89));
        assertThat(count.intValue(), is(1));
        assertThat(countStatic.intValue(), is(1));
        onEvent(new StreamData(89));
        assertThat(count.intValue(), is(2));
        assertThat(countStatic.intValue(), is(2));
        onEvent(new StreamData(-10));
        assertThat(count.intValue(), is(2));
        assertThat(countStatic.intValue(), is(2));
    }

    
}
