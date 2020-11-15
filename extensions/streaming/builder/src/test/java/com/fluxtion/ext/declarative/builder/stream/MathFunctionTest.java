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
import com.fluxtion.api.event.DefaultEvent;
import com.fluxtion.builder.node.SEPConfig;
import static com.fluxtion.ext.streaming.api.MergingWrapper.merge;
import com.fluxtion.ext.streaming.api.Wrapper;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.inBand;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.outsideBand;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.avg;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.ceil;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.count;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.cumSum;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.divide;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.multiply;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.subtract;
import com.fluxtion.ext.streaming.builder.factory.MappingBuilder;
import static com.fluxtion.ext.streaming.builder.factory.MappingBuilder.map;
import static com.fluxtion.ext.streaming.builder.factory.MappingBuilder.mapSet;
import com.fluxtion.ext.streaming.builder.stream.StreamFunctionCompiler;
import static com.fluxtion.ext.streaming.api.stream.Argument.arg;
import static com.fluxtion.ext.streaming.builder.log.LogBuilder.log;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class MathFunctionTest extends StreamInprocessTest {

    public static int intFun(int a, int b) {
        return a + b;
    }

    public int numFun2(Number a, Number b) {
        return a.intValue() + b.intValue();
    }

    @Test
    public void generateProcessor() throws Exception {
        sep((SEPConfig c) -> {
            LibraryFunctionsBuilder.add(DataEvent::getValue, Data1::getVal);
            multiply(
//                    select(DataEvent.class, "temp").get(DataEvent::getValue), 
                    select(DataEvent::getValue, "temp"), 
                    select(DataEvent::getValue, "offset")
            ).id("multiply");
        });
        Number multiply = getWrappedField("multiply");
        assertThat(multiply.intValue(), is(0));
        sep.onEvent(new DataEvent().value(10));
        assertThat(multiply.intValue(), is(0));
        sep.onEvent(new DataEvent().value(10).setFilterString("temp"));
        assertThat(multiply.intValue(), is(0));
        sep.onEvent(new DataEvent().value(10).setFilterString("offset"));
        assertThat(multiply.intValue(), is(100));
    }

    @Test
    public void testTwoMultiArgAdd() {
        sep((c) -> {
            Wrapper<Integer> sum1 = map(MathFunctionTest::intFun, Data1::getVal, Data1::getVal).id("sum1");
            Wrapper<Integer> sum2 = map(MathFunctionTest::intFun, Data1::getVal, Data2::getVal).id("sum2");
            subtract(sum2, sum1).id("subtractSum");
            multiply(select(Data1::getVal), select(Data2::getVal)).id("multiply");
            subtract(arg(Data1::getVal), arg(Data2::getVal)).id("subtract");
            map(divide(), arg(Data1::getVal), arg(Data2::getVal)).id("divide");
            multiply(Data2::getVal, Data2::getVal).id("squared");
            multiply(arg(Data2::getVal), arg(25)).id("times25");
        });

        Wrapper<Number> sum1 = getField("sum1");
        Wrapper<Number> sum2 = getField("sum2");
        Wrapper<Number> multiply = getField("multiply");
        Wrapper<Number> subtract = getField("subtract");
        Wrapper<Number> subtractSum = getField("subtractSum");
        Wrapper<Number> divide = getField("divide");
        Wrapper<Number> squared = getField("squared");
        Wrapper<Number> times25 = getField("times25");

        sep.onEvent(new Data1(10));
        assertThat(sum1.event().intValue(), is(20));
        assertThat(sum2.event().intValue(), is(0));
        assertThat(multiply.event().intValue(), is(0));
        assertThat(subtract.event().intValue(), is(0));
        assertThat(subtractSum.event().intValue(), is(0));
        assertThat(divide.event().doubleValue(), is(0.0));
        assertThat(squared.event().intValue(), is(0));
        assertThat(times25.event().intValue(), is(0));

        sep.onEvent(new Data1(20));
        assertThat(sum1.event().intValue(), is(40));
        assertThat(sum2.event().intValue(), is(0));
        assertThat(multiply.event().intValue(), is(0));
        assertThat(subtract.event().intValue(), is(0));
        assertThat(subtractSum.event().intValue(), is(0));
        assertThat(divide.event().doubleValue(), is(0.0));
        assertThat(squared.event().intValue(), is(0));
        assertThat(times25.event().intValue(), is(0));

        sep.onEvent(new Data2(100));
        assertThat(sum1.event().intValue(), is(40));
        assertThat(sum2.event().intValue(), is(120));
        assertThat(multiply.event().intValue(), is(2000));
        assertThat(subtract.event().intValue(), is(-80));
        assertThat(subtractSum.event().intValue(), is(80));
        assertThat(divide.event().doubleValue(), is(0.2));
        assertThat(squared.event().intValue(), is(10000));
        assertThat(times25.event().intValue(), is(2500));
    }

    @Test
    public void testAdd() {
        sep((c) -> {
            Wrapper<Number> sum = map(MathFunctionTest::addNumber, Data1::getVal, Data1::getVal)
                    .map(Math::rint).id("random")
                    .map(cumSum()).console("sum:").id("add")
                    .map(count()).id("intCount")
                    .map(LibraryFunctionsBuilder.ceil(), Number::doubleValue)
                    .map(avg());
            ceil(Data1::getVal).id("celiFromEvent").map(cumSum()).id("cumSum");
            ceil(Data1Handler::val).id("ceilFromHandler");
            ceil(new Data1Handler()::val).id("ceilFromInstanceHandler");
            ceil(sum).id("ceilFromWrapper");
            LibraryFunctionsBuilder.add(Data1::getVal, Data2::getVal).id("adding");
        });
    }

    @Test
    public void test1() {
        sep((c) -> {
            MappingBuilder.map(this::add, Data1::getVal, Data2::getVal)
                    .notifyOnChange(true).id("addInstance").console("[addInstance]");

            LibraryFunctionsBuilder.add(Data1::getVal, Data2::getVal).map(cumSum()).console("sum:");

            MappingBuilder.map(MathFunctionTest::addStatic, Data1::getVal, Data2::getVal)
                    .notifyOnChange(true).id("addStatic").console("[addStatic] = ", Number::intValue);
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
                Wrapper result = StreamFunctionCompiler.map(new MathFunctionTest(), m,
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

    @Test
    public void testCombiningArray(){
        sep((c) -> {
                Wrapper<Number> eurDealtPos = merge(select(DataEvent.class, "EU", "EC", "EG", "EY"))
                        .map(cumSum(), DataEvent::getValue)
                        .id("eurDealtPos");

                Wrapper<Number> eurContraPos = merge(select(DataEvent.class, "UE", "CE", "GE", "YE"))
                        .map(cumSum(), DataEvent::getValue)
                        .id("eurContraPos");

                Wrapper<Number> netPos = subtract(eurDealtPos, eurContraPos);

                netPos.filter(inBand(-10, 20)).console("[REMOVE WARNING pos inside range -10 < pos < 20 ]").notifyOnChange(true);
                netPos.filter(outsideBand(-10, 20)).console("[WARNING outside range]").notifyOnChange(true);
                netPos.filter(inBand(-600, 600)).console("[REMOVE CRITICAL pos inside range -600 < pos < 600 ]").notifyOnChange(true);
                netPos.filter(outsideBand(-600, 600)).console("[CRITICAL outside range]").notifyOnChange(true);

                log("-> Trade recived:'{}'@'{}' ", 
                        arg(DataEvent::getStringValue), 
                        arg(DataEvent::getValue));
                log("<- Position update: EUR net:{} dealt:{} contra:{} ", 
                        arg(netPos, Number::intValue), 
                        arg(eurDealtPos, Number::intValue), 
                        arg(eurContraPos, Number::intValue)
                );
            });

        DataEvent de1 = new DataEvent();
        de1.setFilterString("EU");
        de1.value = 2;
        sep.onEvent(de1);
        de1.setFilterString("EC");
        sep.onEvent(de1);
        de1.setFilterString("UE");
        sep.onEvent(de1);
        de1.value = 50;
        sep.onEvent(de1);
        de1.setFilterString("EY");
        sep.onEvent(de1);
        de1.setFilterString("CE");
        sep.onEvent(de1);
        de1.value = -1000;
        sep.onEvent(de1);
        sep.onEvent(de1);
        de1.value = 500;
        sep.onEvent(de1);
        sep.onEvent(de1);
        sep.onEvent(de1);
        sep.onEvent(de1);
        de1.value = -1500;
        sep.onEvent(de1);
    }

    @Test
    public void chainedFunctions() {
        sep((c) -> {
            Wrapper<Number> add = LibraryFunctionsBuilder.add(arg(25), arg(DataEvent::getValue));
            multiply(arg(0.5), arg(add)).id("result");
        });
        DataEvent event = new DataEvent();
        event.value = 10;
        sep.onEvent(event);
        Wrapper<Number> sum = getField("result");
        assertThat(sum.event().doubleValue(), is(17.5));
    }

    public static class DataEvent extends DefaultEvent {

        public static final int ID = 1;

        public DataEvent() {
            super(ID);
        }
        
        public DataEvent value(int value){
            this.value = value;
            return this;
        }

        public int value;

        public int getValue() {
            return value;
        }

        public DataEvent setFilterString(String key) {
            this.filterString = key;
            this.filterId = Integer.MAX_VALUE;
            return this;
        }

        public DataEvent setFilterInt(int id) {
            this.filterString = "";
            this.filterId = id;
            return this;
        }

        public String getStringValue() {
            return filterString.toString();
        }

    }

    public static double addNumber(Number a, Number b) {
        return a.doubleValue() + b.doubleValue();
    }

    public static int addStatic(int a, int b) {
        return a + b;
    }

    public int add(int a, int b) {
        return a + b;
    }

    public static double addDouble(double a, double b) {
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

    public static class Data1 {

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

    public static class Data2 {

        public int val;

        public Data2(int val) {
            super();
            this.val = val;
        }

        public int getVal() {
            return val;
        }

        public double doubleVal() {
            return 56;
        }

        public void setVal(int val) {
            this.val = val;
        }

    }
}
