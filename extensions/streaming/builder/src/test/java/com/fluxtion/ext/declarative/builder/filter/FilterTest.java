
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
import com.fluxtion.ext.streaming.api.Wrapper;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.gt;
import com.fluxtion.ext.streaming.api.test.BooleanFilter;
import com.fluxtion.ext.streaming.builder.factory.BooleanBuilder;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import com.fluxtion.ext.streaming.builder.factory.FilterBuilder;
import com.fluxtion.ext.streaming.builder.factory.FilterByNotificationBuilder;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.count;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.cumSum;
import static com.fluxtion.ext.streaming.builder.factory.MappingBuilder.map;
import static com.fluxtion.ext.streaming.builder.factory.MappingBuilder.mapSet;
import static com.fluxtion.ext.streaming.builder.util.FunctionArg.arg;
import lombok.Data;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
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
        Assert.assertThat(sum.intValue(), is (4));
        Assert.assertThat(count.intValue(), is (0));
        
        de1.value = 10;
        sep.onEvent(de1);
        Assert.assertThat(sum.intValue(), is (14));
        Assert.assertThat(count.intValue(), is (1));
    }
    
    @Test
    public void complexNaryTest(){
        sep((c) -> {
            Wrapper<Boolean> map = map(FilterTest::withinRange, arg(DataEvent::getValue), arg(MinAge::getMin), arg(MaxAge::getMax));
            FilterByNotificationBuilder.filter(select(DataEvent.class), BooleanBuilder.and(map)).console("receivedEvent age:", DataEvent::getValue);
            //TODO build a test template from 
        }); 
    }
    
    public static boolean withinRange(int test, int min, int max){
        return min < test && test > max;
    }
    
    @Data
    public static class MinAge{
        final int min;
    }
    
    @Data
    public static class MaxAge{
        final int max;
    }
}
