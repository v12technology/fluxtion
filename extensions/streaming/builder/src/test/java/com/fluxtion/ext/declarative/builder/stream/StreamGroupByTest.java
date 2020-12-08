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

import com.fluxtion.ext.streaming.api.group.AggregateFunctions;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class StreamGroupByTest extends StreamInprocessTest {

    @Test
    public void groupByStream() {
        sep((c) -> {
            select(StreamData.class)
                .group(StreamData::getStringValue, StreamData::getIntValue, AggregateFunctions::calcSum)
                .id("group");//.log("groupBy Map -> ");
        });
        GroupBy<Number> group = getField("group");
        sep.onEvent(new StreamData("one", 1000));
        sep.onEvent(new StreamData("one", 500));
        sep.onEvent(new StreamData("one", 1200));
        sep.onEvent(new StreamData("two", 60));
        sep.onEvent(new StreamData("two", 40));
        sep.onEvent(new StreamData("two", 100));

        assertThat(group.value("one").intValue(), is(2700));
        assertThat(group.value("two").intValue(), is(200));
    }

}
