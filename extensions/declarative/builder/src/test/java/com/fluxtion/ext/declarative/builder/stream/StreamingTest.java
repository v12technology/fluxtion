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

import com.fluxtion.ext.declarative.api.EventWrapper;
import com.fluxtion.ext.declarative.api.Wrapper;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class StreamingTest extends BaseSepInprocessTest {

    @Test
    public void mapNumberFromString() {
        sep((c) -> {
            Wrapper<StreamData> in = select(StreamData.class);
            in.map(new StreamFunctions()::String2Number, StreamData::getStringValue).id("str2Number");
            in.map(new StreamFunctions()::String2Int, StreamData::getStringValue).id("str2Int");
            in.map(new StreamFunctions()::String2Double, StreamData::getStringValue).id("str2Double");
        });
        onEvent(new StreamData("23"));
        Wrapper<Number> valNumber = getField("str2Number");
        assertThat(valNumber.event().intValue(), is(23));
        Wrapper<Number> valInt = getField("str2Int");
        assertThat(valInt.event().intValue(), is(23));
        //
        Wrapper<Number> valDouble = getField("str2Double");
        assertThat(valDouble.event().doubleValue(), is(23.0));
        //double
        onEvent(new StreamData("3.14159"));
        assertThat(valDouble.event().doubleValue(), is(3.14159));
    }

    @Test
    public void mapBooleanFromString() {
        sep((c) -> {
            Wrapper<StreamData> in = select(StreamData.class);
            in.map(new StreamFunctions()::String2Boolean, StreamData::getStringValue).id("str2Boolean");
        });
        Wrapper<Boolean> val = getField("str2Boolean");
        onEvent(new StreamData("true"));
        assertThat(val.event(), is(true));
        onEvent(new StreamData("false"));
        assertThat(val.event(), is(false));
    }

}
