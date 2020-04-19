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

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.ext.declarative.builder.helpers.MyData;
import com.fluxtion.ext.declarative.builder.helpers.MyDataHandler;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.count;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.cumSum;
import static com.fluxtion.ext.streaming.builder.stream.StreamFunctionCompiler.get;
import static com.fluxtion.ext.streaming.builder.stream.StreamOperatorService.stream;
import lombok.Data;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class StreamFieldTest extends StreamInprocessTest {
    
    
    @Test
    public void testGetPrimitiveField(){
        sep(c ->{
            MyDataHandler handler = c.addNode(new MyDataHandler("test"));
            get(handler::getDoubleVal).map(cumSum()).id("sum");
        });
        
        onEvent(new MyData(10, 10, "test"));
        onEvent(new MyData(100, 10, "ignore"));
        onEvent(new MyData(10, 10, "test"));
        
        Number sum = getWrappedField("sum");
        assertThat(sum.intValue(), is(20));
    }
    
    @Test
    public void testStreamPrimitiveField(){
        sep(c ->{
            stream(new MyDataHandler("test")).get(MyDataHandler::getDoubleVal).map(cumSum()).id("sum");
        });
        onEvent(new MyData(10, 10, "test"));
        onEvent(new MyData(100, 10, "ignore"));
        onEvent(new MyData(10, 10, "test"));
        
        Number sum = getWrappedField("sum");
        assertThat(sum.intValue(), is(20));
    }
    
    @Test
    public void testGetReferenceField(){
        sep(c ->{
            StringHandler handler = c.addNode(new StringHandler("1"));
            get(handler::getIn).map(Double::parseDouble).map(cumSum()).id("sum");
        });
        onEvent("test");
        onEvent("100");
        onEvent("23");
        onEvent("14");
        
        Number sum = getWrappedField("sum");
        assertThat(sum.intValue(), is(114));
    }
    
    @Test
    public void testStreamReferenceField(){
        sep(c ->{
            stream(new StringHandler("test")::getIn).map(count()).id("count");
        });
        onEvent("test");
        onEvent("IGNOREtest");
        onEvent("testXXXX");
        onEvent("testYYYY");
        
        Number count = getWrappedField("count");
        assertThat(count.intValue(), is(3));
    }
    
    @Test
    public void testStreamAndGetReferenceField(){
        sep(c ->{
            stream(new StringHandler("1")).get(StringHandler::getIn)
                    .map(Double::parseDouble).map(cumSum()).id("sum");
        });
        onEvent("test");
        onEvent("100");
        onEvent("23");
        onEvent("14");
        
        Number sum = getWrappedField("sum");
        assertThat(sum.intValue(), is(114));
    }
    
    @Data
    public static class StringHandler{
    
        private final String filter;
        private String in;
        
        @EventHandler
        public boolean filterString(String in){
            boolean match = false;
            if(in.startsWith(filter)){
                this.in = in;
                match = true;
            }
            return match;
        }
        
    }
    
}
