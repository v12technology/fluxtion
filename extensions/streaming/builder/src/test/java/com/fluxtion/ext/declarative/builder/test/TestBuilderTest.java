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
package com.fluxtion.ext.declarative.builder.test;

import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.gt;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsBuilder.count;
import static com.fluxtion.ext.streaming.builder.factory.TestBuilder.test;
import lombok.Data;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class TestBuilderTest extends StreamInprocessTest{
   
    
    @Test
    public void testNotify(){
        sep(c ->{
            count(test(gt(10), DataEvent::getDoubleVal)).id("gt0");
            count(test(TestBuilderTest::gt1, DataEvent::getDoubleVal)).id("gt1");
            count(test(TestBuilderTest::gt2, DataEvent::getDoubleVal, DataEvent::getDoubleVal)).id("gt2");
            count(test(TestBuilderTest::gt3, DataEvent::getDoubleVal, DataEvent::getDoubleVal,  DataEvent::getDoubleVal)).id("gt3");
        });
        
        Number g0 = getWrappedField("gt0");
        Number g1 = getWrappedField("gt1");
        Number g2 = getWrappedField("gt2");
        Number g3 = getWrappedField("gt3");
        
        onEvent(new DataEvent(2));
        assertThat(g0.intValue(), is(0));
        assertThat(g1.intValue(), is(0));
        assertThat(g2.intValue(), is(0));
        assertThat(g3.intValue(), is(0));
        onEvent(new DataEvent(3));
        assertThat(g0.intValue(), is(0));
        assertThat(g1.intValue(), is(0));
        assertThat(g2.intValue(), is(0));
        assertThat(g3.intValue(), is(0));
        onEvent(new DataEvent(4));
        assertThat(g0.intValue(), is(0));
        assertThat(g1.intValue(), is(0));
        assertThat(g2.intValue(), is(0));
        assertThat(g3.intValue(), is(1));
        onEvent(new DataEvent(6));
        assertThat(g0.intValue(), is(0));
        assertThat(g1.intValue(), is(0));
        assertThat(g2.intValue(), is(1));
        assertThat(g3.intValue(), is(2));
        onEvent(new DataEvent(12));
        assertThat(g0.intValue(), is(1));
        assertThat(g1.intValue(), is(1));
        assertThat(g2.intValue(), is(2));
        assertThat(g3.intValue(), is(3));
        
    }
    
    public static boolean  gt1(double val){
        return val > 10;
    }
    
    public static boolean gt2(double val, double val2){
        return val+val2 > 10;
    }
    
    public static boolean gt3(double val, double val2, double val3){
        return val+val2+val3 > 10;
    }
    
    @Data
    public static class DataEvent{
        private final double doubleVal;
    }
    
}
