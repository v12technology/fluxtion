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
import com.fluxtion.ext.streaming.builder.factory.MappingBuilder;
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
            MappingBuilder.map(add(), DataEvent::getDoubleVal, DataEvent::getIntVal)
                    .id("sum");
        });
        Number sum = getWrappedField("sum");
        onEvent(new DataEvent(77, 10));
        Assert.assertThat(sum.intValue(), is(87));
        onEvent(new DataEvent(4, 7));
        Assert.assertThat(sum.intValue(), is(11));
    }

    @Test
    public void testSubClass() {
        sep(c -> {
            MappingBuilder.map(MapTest::toInt, DataEvent::getDoubleVal);
            MappingBuilder.map(MapTest::pipCount, arg(select(Apple.class)));
            MappingBuilder.map(MapTest::pipCount, arg(Apple.class));
            MappingBuilder.map(MapTest::pipCount, Food::getFruit);
            MappingBuilder.map(MapTest::pipCount, Food::getApple);
        });
    }

    @Data
    public static class DataEvent {

        private final double doubleVal;
        private final double intVal;
    }

    public static int toInt(double number) {
        return (int)number;
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
