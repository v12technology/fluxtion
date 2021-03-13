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
package com.fluxtion.ext.declarative.builder.mapfield;

import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import com.fluxtion.ext.streaming.api.stream.FieldMapper;
import lombok.Data;
import lombok.Value;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class MapTest extends StreamInprocessTest {

    @Test
    public void testFieldSet() {
//        reuseSep = true;
        fixedPkg = true;
        sep(c -> {
            FieldMapper.setField(
                select(DataEvent.class).id("dataEvent"),
                DataEvent::getIntVal,
                DataEvent::setDoubleVal,
                MapTest::multiply10X
            );
        });
        onEvent(new DataEvent(10));
        DataEvent dataEvent = getWrappedField("dataEvent");
        assertThat(dataEvent.getDoubleVal(), is(100d));
        onEvent(new DataEvent(20));
        dataEvent = getWrappedField("dataEvent");
        assertThat(dataEvent.getDoubleVal(), is(200d));
        onEvent(new DataEvent(30));
        dataEvent = getWrappedField("dataEvent");
        assertThat(dataEvent.getDoubleVal(), is(300d));
    }

    @Test
    public void testFieldSetInstanceFunction() {
//        reuseSep = true;
        fixedPkg = true;
        sep(c -> {
            FieldMapper.setField(
                    select(DataEvent.class).id("dataEvent"),
                    DataEvent::getIntVal,
                    DataEvent::setDoubleVal,
                    new MyMapper(-6)::multiply
            )
                .map(Math::abs, DataEvent::getDoubleVal).id("absValue");             
        });
        onEvent(new DataEvent(10));
        DataEvent dataEvent = getWrappedField("dataEvent");
        Number absValue = getWrappedField("absValue");
        assertThat(dataEvent.getDoubleVal(), is(-60d));
        assertThat(absValue.intValue(), is(60));
        
        onEvent(new DataEvent(20));
        dataEvent = getWrappedField("dataEvent");
        assertThat(dataEvent.getDoubleVal(), is(-120d));
        assertThat(absValue.intValue(), is(120));
        
        
        onEvent(new DataEvent(30));
        dataEvent = getWrappedField("dataEvent");
        assertThat(dataEvent.getDoubleVal(), is(-180d));
        assertThat(absValue.intValue(), is(180));
    }

    @Test
    public void testWrapperFieldSet() {
//        reuseSep = true;
        fixedPkg = true;
        sep(c -> {
            select(DataEvent.class).mapField(
                DataEvent::getIntVal,
                DataEvent::setDoubleVal,
                MapTest::multiply10X
            ).id("dataEvent");
        });

        onEvent(new DataEvent(10));
        DataEvent dataEvent = getWrappedField("dataEvent");
        assertThat(dataEvent.getDoubleVal(), is(100d));
        onEvent(new DataEvent(20));
        dataEvent = getWrappedField("dataEvent");
        assertThat(dataEvent.getDoubleVal(), is(200d));
        onEvent(new DataEvent(30));
        dataEvent = getWrappedField("dataEvent");
        assertThat(dataEvent.getDoubleVal(), is(300d));
    }

    public static double multiply10X(int number) {
        return number * 10;
    }

    public static int multiply10X(double number) {
        return (int) (number * 10 + 10);
    }

    @Data
    public static class DataEvent {

        private double doubleVal;
        private int calcIntVal;
        private final int intVal;
    }

    @Value
    public static class MyMapper {

        int multiplier;

        public double multiply(int number) {
            return (number * multiplier);
        }
    }

}