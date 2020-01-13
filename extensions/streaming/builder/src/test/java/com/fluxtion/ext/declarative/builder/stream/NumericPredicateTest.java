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
package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.ext.streaming.api.FilterWrapper;
import com.fluxtion.ext.streaming.api.stream.NumericPredicates;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.num;
import static com.fluxtion.ext.streaming.builder.stream.StreamFunctionsBuilder.count;
import static com.fluxtion.ext.streaming.builder.stream.StreamFunctionsBuilder.toDouble;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class NumericPredicateTest extends StreamInprocessTest {

    @Test
    public void elseNotifyOnce() {
//        fixedPkg = true;
        sep((c) -> {
            FilterWrapper<Number> filter = toDouble(StreamData::getIntValue)
                    .filter( num(10,"gt")::greaterThan)
                    .notifyOnChange(true);
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
        
        onEvent(new NumericPredicates.FilterConfig("gt", 5));
        
        onEvent(new StreamData(9));
        assertThat(filterCount.intValue(), is(1));
        assertThat(elseCount.intValue(), is(1));
        
        onEvent(new StreamData(4));
        assertThat(filterCount.intValue(), is(1));
        assertThat(elseCount.intValue(), is(2));

    }
    
}
