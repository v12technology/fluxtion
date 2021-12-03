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
package com.fluxtion.ext.declarative.builder.group;

import com.fluxtion.ext.declarative.builder.stream.StreamInProcessTest;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import static com.fluxtion.ext.streaming.builder.factory.FilterBuilder.filter;
import static com.fluxtion.ext.streaming.builder.group.Frequency.frequency;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class FrequencyTest extends StreamInProcessTest {

    
    @Test
    public void testWc(){
        fixedPkg = true;
//        reuseSep = true;
        sep((c) -> frequency(String::toString).id("frequency"));
 
        //send events
        onEvent("one");
        onEvent("two");
        onEvent("two");
        onEvent("three");
        onEvent("three");
        onEvent("three");
        
        //test
        GroupBy<MutableNumber> frequency = getField("frequency");
        assertThat(frequency.value("one").intValue(), is(1));
        assertThat(frequency.value("two").intValue(), is(2));
        assertThat(frequency.value("three").intValue(), is(3));
    
    }
    
    @Test
    public void testFrequency() throws Exception {
        //build
        sep((c) -> frequency(MyData::getId).id("frequency"));
        
        //send events
        onEvent(new MyData("one"));
        onEvent(new MyData("two"));
        onEvent(new MyData("two"));
        onEvent(new MyData("three"));
        onEvent(new MyData("three"));
        onEvent(new MyData("three"));
        
        //test
        GroupBy<MutableNumber> frequency = getField("frequency");
        assertThat(frequency.value("one").intValue(), is(1));
        assertThat(frequency.value("two").intValue(), is(2));
        assertThat(frequency.value("three").intValue(), is(3));
    }
    
    @Test
    public void testFrequencyWithFilter() throws Exception {
        //build
        sep((c) -> 
            frequency(filter(MyData2::getLocation, "NY"::equalsIgnoreCase), MyData2::getId).id("frequency")
        );
        
        //send events
        onEvent(new MyData2("NY","one"));
        onEvent(new MyData2("NY","two"));
        onEvent(new MyData2("NY","two"));
        onEvent(new MyData2("NY","three"));
        onEvent(new MyData2("NY","three"));
        onEvent(new MyData2("NY","three"));
        
        onEvent(new MyData2("LN","one"));
        onEvent(new MyData2("LN","two"));
        onEvent(new MyData2("LN","two"));
        onEvent(new MyData2("LN","three"));
        onEvent(new MyData2("LN","three"));
        onEvent(new MyData2("LN","three"));
        
        //test
        GroupBy<MutableNumber> frequency = getField("frequency");
        assertThat(frequency.value("one").intValue(), is(1));
        assertThat(frequency.value("two").intValue(), is(2));
        assertThat(frequency.value("three").intValue(), is(3));
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyData{
    
        String id;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyData2{
        String location;
        String id;
    }
}
