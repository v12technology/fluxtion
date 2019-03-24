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
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;
import static com.fluxtion.ext.declarative.api.MergingWrapper.merge;
import com.fluxtion.ext.declarative.api.Wrapper;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import static com.fluxtion.ext.declarative.builder.stream.FilterBuilder.map;
import static com.fluxtion.ext.declarative.builder.stream.FunctionBuilder.mapSet;
import static com.fluxtion.ext.declarative.builder.stream.StreamFunctionsBuilder.cumSum;
import static com.fluxtion.ext.declarative.builder.stream.StreamFunctionsHelper.divide;
import static com.fluxtion.ext.declarative.builder.stream.StreamFunctionsHelper.intCount;
import static com.fluxtion.ext.declarative.builder.stream.StreamFunctionsHelper.multiply;
import static com.fluxtion.ext.declarative.builder.stream.StreamFunctionsHelper.subtract;
import com.fluxtion.ext.declarative.builder.util.FunctionArg;
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

    public static int intFun(int a, int b) {
        return a + b;
    }

    public int numFun2(Number a, Number b) {
        return a.intValue() + b.intValue();
    }

    public static <T extends Number, S extends Number> Wrapper<Number> intHelper(FunctionArg<T> arg1, FunctionArg<S> arg2) {
        return FilterBuilder.map(MathFunctionTest::intFun, arg1, arg2);
    }

    public static <S extends Number> Wrapper<Number> ceil(SerializableSupplier< S> supplier) {
        return FilterBuilder.map(Math::ceil, arg(supplier));
    }

    public static <T, S extends Number> Wrapper<Number> ceil(SerializableFunction<T, S> supplier) {
        return FilterBuilder.map(Math::ceil, arg(supplier));
    }

    public static <T, S extends Number> Wrapper<Number> ceil(Wrapper<T> wrapper, SerializableFunction<T, S> supplier) {
        return FilterBuilder.map(Math::ceil, arg(wrapper, supplier));
    }

    public static <T extends Number> Wrapper<Number> ceil(Wrapper<T> wrapper) {
        return FilterBuilder.map(Math::ceil, arg(wrapper));
    }

    @Test
    public void generateProcessor() throws Exception {
        sep((c) -> {
            StreamFunctionsHelper.add(DataEvent::getValue, Data1::getVal);
            multiply(select(DataEvent.class, "temp"), DataEvent::getValue, select(DataEvent.class, "offset"), DataEvent::getValue);
        });

    }

    @Test
    public void testTwoMultiArgAdd() {
        sep((c) -> {
            Wrapper<Number> sum1 = intHelper(arg(Data1::getVal), arg(Data1::getVal)).id("sum1");
            Wrapper<Number> sum2 = intHelper(arg(Data1::getVal), arg(Data2::getVal)).id("sum2");
            multiply(select(Data1.class), Data1::getVal, select(Data2.class), Data2::getVal).id("multiply");
            subtract(arg(Data1::getVal), arg(Data2::getVal)).id("subtract");
            subtract(sum2, sum1).id("subtractSum");
            FunctionBuilder.map(divide(), arg(Data1::getVal), arg(Data2::getVal)).id("divide");
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
            Wrapper<Number> sum = intHelper(arg(Data1::getVal), arg(Data1::getVal))
                    .mapDouble(Math::rint).id("random")
                    .map(cumSum()).console("sum:").id("add")
                    .map(intCount()).id("intCount")
                    .map(StreamFunctionsHelper.ceil(), Number::doubleValue)
                    .map(StreamFunctionsBuilder.avg());
            ceil(Data1::getVal).id("celiFromEvent").map(cumSum()).id("cumSum");
            ceil(Data1Handler::val).id("ceilFromHandler");
            ceil(new Data1Handler()::val).id("ceilFromInstanceHandler");
            ceil(sum).id("ceilFromWrapper");
            StreamFunctionsHelper.add(Data1::getVal, Data2::getVal).id("adding");
        });
    }

    @Test
    public void test1() {
        sep((c) -> {
            FunctionBuilder.map(this::add, Data1::getVal, Data2::getVal)
                    .notifyOnChange(true).id("addInstance").console("[addInstance]");

            intHelper(arg(Data1::getVal), arg(Data2::getVal)).map(cumSum()).console("sum:");

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

        public double doubleVal() {
            return 56;
        }

        public void setVal(int val) {
            this.val = val;
        }

    }
}
