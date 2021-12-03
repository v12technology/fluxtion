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

import com.fluxtion.ext.streaming.api.WrappedList;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.FilterBuilder.filter;
import java.util.Arrays;
import java.util.Comparator;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class SubListTest extends StreamInProcessTest {
    
    @Test
    public void subTop(){
        sep(c ->{
            WrappedList<Integer> numbers = select(Integer.class).collect().comparator(new MyComparator()).id("numbers");
            numbers.resetNotifier(filter("clear"::equalsIgnoreCase));
            numbers.top(4).id("top4");
            numbers.last(4).id("last4");
            numbers.skip(4).id("skip4");
        });
        for (int i = 0; i < 10; i++) {
            onEvent(i);
        }
        
        WrappedList numbers = getField("numbers");
        assertEquals(numbers.collection(), Arrays.asList(9,8,7,6,5,4,3,2,1,0));
        
        WrappedList top4 = getField("top4");
        assertEquals(top4.collection(), Arrays.asList(9,8,7,6));
        WrappedList last4 = getField("last4");
        assertEquals(last4.collection(), Arrays.asList(3,2,1, 0));
        WrappedList skip4 = getField("skip4");
        assertEquals(skip4.collection(), Arrays.asList(5,4,3,2,1,0));
        
        onEvent("falseClear");
        assertEquals(top4.collection(), Arrays.asList(9,8,7,6));
        assertEquals(last4.collection(), Arrays.asList(3,2,1, 0));
        assertEquals(skip4.collection(), Arrays.asList(5,4,3,2,1,0));
        
        onEvent("clear");
        assertEquals(numbers.size(), 0);
        assertEquals(top4.size(), 0);
        assertEquals(last4.size(), 0);
        assertEquals(skip4.size(), 0);
        
    }
    
    public static class MyComparator implements Comparator<Integer>{

        @Override
        public int compare(Integer o1, Integer o2) {
            return o2.compareTo(o1);
        }
    
    }
    
}
