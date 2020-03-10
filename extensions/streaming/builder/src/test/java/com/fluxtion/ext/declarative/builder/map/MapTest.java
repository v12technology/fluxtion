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
package com.fluxtion.ext.declarative.builder.map;

import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.add;
import static com.fluxtion.ext.streaming.builder.factory.MappingBuilder.map;
import static com.fluxtion.ext.streaming.builder.util.FunctionArg.arg;
import lombok.Data;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class MapTest extends StreamInprocessTest {

    @Test
    public void testNotify() {
        sep(c -> {
            map(add(), DataEvent::getDoubleVal, DataEvent::getDoubleVal).id("sum");
            map(MapTest::addInt, DataEvent::getIntVal, DataEvent::getIntVal)
                    .get(Number::doubleValue).map(Math::abs).id("sum2");
        });
        Number sum = getWrappedField("sum");
        Number sum2 = getWrappedField("sum2");
        onEvent(new DataEvent(77, 10));
        Assert.assertThat(sum.intValue(), is(154));
        Assert.assertThat(sum2.intValue(), is(20));
        onEvent(new DataEvent(4, -7));
        Assert.assertThat(sum.intValue(), is(8));
        Assert.assertThat(sum2.intValue(), is(14));
    }
    
    
    @Test
    public void testSubClass() {
        sep(c -> {
            map(MapTest::toInt, DataEvent::getDoubleVal);
            map(MapTest::pipCount, arg(select(Apple.class)));
            map(MapTest::pipCount, arg(Apple.class));
            map(MapTest::pipCount, Food::getFruit);
            map(MapTest::pipCount, Food::getApple);
        });
    }

    @Data
    public static class DataEvent {

        private final double doubleVal;
        private final int intVal;
    }

    public static int toInt(double number) {
        return (int)number;
    }
    
    public static int addInt(int x, int y){
        return x + y;
    }
    public static class Fruit{ }
    public static class Meat{ }
    
    @Data
    public static class Food{
       private Fruit fruit;
       private Apple apple;
       private Meat meat;
    }
    
    public static class Apple extends Fruit{}
    
    
    
    public static int pipCount(Fruit f){
        return 1;
    }
}
