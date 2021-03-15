/*
 * Copyright (C) 2021 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.declarative.builder.mapfield;

import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.stream.FieldMapper;
import com.fluxtion.ext.streaming.api.stream.PartitioningFieldMapper;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import static org.hamcrest.CoreMatchers.is;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class PartitionMapTest extends StreamInprocessTest {

    @Test
    public void partioniningTest() {

        sep(c -> {
            FieldMapper.setField(
                    select(DataToBeMapped.class),
                    DataToBeMapped::getKey,
                    DataToBeMapped::getValue,
                    DataToBeMapped::setCumSum,
                    new MyFunctionFactory(5)::buildFunction
            )
                    .id("data");
        });

        onEvent(new DataToBeMapped("eu", 10, 0));
        onEvent(new DataToBeMapped("eu", 10, 0));
        DataToBeMapped data = getWrappedField("data");
        MatcherAssert.assertThat(data.cumSum, is(100));

        onEvent(new DataToBeMapped("uc", 50, 0));
        onEvent(new DataToBeMapped("uc", 50, 0));
        onEvent(new DataToBeMapped("uc", 50, 0));
        data = getWrappedField("data");
        MatcherAssert.assertThat(data.cumSum, is(750));

        onEvent(new DataToBeMapped("eu", 10, 0));
        data = getWrappedField("data");
        MatcherAssert.assertThat(data.cumSum, is(150));
    }

    @Test
    public void partioniningWrapperTest() {
        sep(c -> {
            select(DataToBeMapped.class).mapField(
                    DataToBeMapped::getKey,
                    DataToBeMapped::getValue,
                    DataToBeMapped::setCumSum,
                    new MyFunctionFactory(5)::buildFunction
            )
                    .id("data");
        });

        onEvent(new DataToBeMapped("eu", 10, 0));
        onEvent(new DataToBeMapped("eu", 10, 0));
        DataToBeMapped data = getWrappedField("data");
        MatcherAssert.assertThat(data.cumSum, is(100));

        onEvent(new DataToBeMapped("uc", 50, 0));
        onEvent(new DataToBeMapped("uc", 50, 0));
        onEvent(new DataToBeMapped("uc", 50, 0));
        data = getWrappedField("data");
        MatcherAssert.assertThat(data.cumSum, is(750));

        onEvent(new DataToBeMapped("eu", 10, 0));
        data = getWrappedField("data");
        MatcherAssert.assertThat(data.cumSum, is(150));
    }

    @Value
    public static class MyFunctionFactory {

        int multiplier;

        public LambdaReflection.SerializableFunction<Integer, Integer> buildFunction() {
            return new CumSum(multiplier)::cumSum;
        }
    }

    @Data
    public static class CumSum {

        private final int multiplier;
        private transient int sum;

        public int cumSum(int add) {
            sum += add * multiplier;
            return sum;
        }
    }

    @Data
    @AllArgsConstructor
    public static class DataToBeMapped {

        String key;
        int value;
        int cumSum;
    }

}
