/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
import com.fluxtion.api.event.Event;
import static com.fluxtion.ext.declarative.api.MergingWrapper.merge;
import com.fluxtion.ext.declarative.api.Wrapper;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import static com.fluxtion.ext.declarative.builder.stream.FilterBuilder.map;
import static com.fluxtion.ext.declarative.builder.stream.FunctionBuilder.mapSet;
import static com.fluxtion.ext.declarative.builder.stream.StreamFunctionsBuilder.cumSum;
import static com.fluxtion.ext.declarative.builder.util.FunctionArg.arg;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class MathFunctionTest extends BaseSepInprocessTest {


    
    @Test
    public void test1(){
        sep((c) -> {
            FunctionBuilder.map(this::add, Data1::getVal, Data2::getVal)
                    .notifyOnChange(true).id("addInstance").console("[addInstance]");
            FunctionBuilder.map(MathFunctionTest::addStatic, Data1::getVal, Data2::getVal)
                    .notifyOnChange(true).id("addStatic").console("[addStatic]");
        });
        Wrapper<Number> addInstance = getField("addInstance");
        Wrapper<Number> addStatic = getField("addStatic");
        sep.onEvent(new Data1(10));
        assertThat(addStatic.event().intValue(), is(0));
        assertThat(addInstance.event().intValue(), is(0));
        
        sep.onEvent(new Data1(20));
        assertThat(addStatic.event().intValue(), is(0));
        assertThat(addInstance.event().intValue(), is(0));
        
        sep.onEvent(new Data2(40));
        assertThat(addStatic.event().intValue(), is(60));
        assertThat(addInstance.event().intValue(), is(60));
        
        sep.onEvent(new Data2(10));
        assertThat(addStatic.event().intValue(), is(30));
        assertThat(addInstance.event().intValue(), is(30));
        
        sep.onEvent(new Data1(40));
        assertThat(addStatic.event().intValue(), is(50));
        assertThat(addInstance.event().intValue(), is(50));
    }

    @Test
    public void testNaryFunction() {
        sep((c) -> {
            try {
                Method m = MathFunctionTest.class.getDeclaredMethod("add", int.class, int.class);
                Wrapper result = map(new MathFunctionTest(), m,
                        arg(new Data1Handler()::val), arg(new Data2Handler()::val)).build();
                result.id("result").console("[add=]");

            } catch (NoSuchMethodException | SecurityException ex) {
                Logger.getLogger(MathFunctionTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        Wrapper<Number> sum = getField("result");
        sep.onEvent(new Data1(10));
        assertThat(sum.event().intValue(), is(0));
        sep.onEvent(new Data1(20));
        assertThat(sum.event().intValue(), is(0));
        sep.onEvent(new Data2(40));
        assertThat(sum.event().intValue(), is(60));
        sep.onEvent(new Data2(10));
        assertThat(sum.event().intValue(), is(30));
        sep.onEvent(new Data1(40));
        assertThat(sum.event().intValue(), is(50));
    }

    @Test
    public void testMapSet() {
        sep((c) -> {
            mapSet(cumSum(), arg(Data1::getVal), arg(Data2::getVal))
                    .id("cumSum").console("[cumsum]");
        });
        Wrapper<Number> sum = getField("cumSum");
        sep.onEvent(new Data1(10));
        assertThat(sum.event().intValue(), is(10));
        sep.onEvent(new Data1(20));
        assertThat(sum.event().intValue(), is(20));
        sep.onEvent(new Data1(40));
        assertThat(sum.event().intValue(), is(40));
        sep.onEvent(new Data2(10));
        assertThat(sum.event().intValue(), is(50));
        sep.onEvent(new Data2(5));
        assertThat(sum.event().intValue(), is(45));

    }

    @Test
    public void testIncSumArray() throws Exception {
        sep((c) -> {
            merge(select(DataEvent.class, "RED", "GREEN"))
                    .map(cumSum(), DataEvent::getValue)
                    .id("redGreen");

            merge(select(DataEvent.class, 1, 2, 3))
                    .map(cumSum(), DataEvent::getValue)
                    .id("num_1_2_3");
        });

        //fire some events for FX - ignored ny EQ 
        DataEvent de1 = new DataEvent();
        de1.setFilterString("RED");
        de1.value = 200;
        sep.onEvent(de1);
        de1.setFilterString("BLUE");
        sep.onEvent(de1);
        de1.setFilterString("GREEN");
        sep.onEvent(de1);
        de1.value = 600;
        sep.onEvent(de1);
        de1.setFilterInt(2);
        sep.onEvent(de1);
        //test
        Wrapper<Number> colours = getField("redGreen");
        Wrapper<Number> nums = getField("num_1_2_3");
        assertThat(colours.event().intValue(), is(1000));
        assertThat(nums.event().intValue(), is(600));
    }

    public static class DataEvent extends Event {

        public static final int ID = 1;

        public DataEvent() {
            super(ID);
        }

        public int value;

        public int getValue() {
            return value;
        }

        public void setFilterString(String key) {
            this.filterString = key;
            this.filterId = Integer.MAX_VALUE;
        }

        public void setFilterInt(int id) {
            this.filterString = "";
            this.filterId = id;

        }

        public String getStringValue() {
            return filterString.toString();
        }

    }
    
    public static int addStatic(int a, int b){
        return a+b;
    }

    public int add(int a, int b) {
        return a + b;
    }

    public static class Data1Handler {

        double val;

        @EventHandler
        public void data1(Data1 event) {
            val = event.val;
        }

        public double val() {
            return val;
        }
    }

    public static class Data2Handler {

        double val;

        @EventHandler
        public void data1(Data2 event) {
            val = event.val;

        }

        public double val() {
            return val;
        }
    }

    public static class Data1 extends Event {

        public int val;

        public Data1(int val) {
            super();
            this.val = val;
        }

        public int getVal() {
            return val;
        }

        public void setVal(int val) {
            this.val = val;
        }

    }

    public static class Data2 extends Event {

        public int val;

        public Data2(int val) {
            super();
            this.val = val;
        }

        public int getVal() {
            return val;
        }

        public void setVal(int val) {
            this.val = val;
        }

    }
}
