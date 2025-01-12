/*
 * Copyright (c) 2025 gregory higgins.
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

package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import lombok.Data;
import lombok.Value;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static com.fluxtion.compiler.builder.dataflow.MultiJoinBuilder.multiJoinLeg;

public class MultiJoinTest extends MultipleSepTargetInProcessTest {
    public MultiJoinTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void resetJoin() {
        sep(c -> MultiJoinBuilder
                .builder(String.class, MergedData::new)
                .addJoin(DataFlow.groupBy(LeftData::getName), MergedData::setLeftData)
                .addJoin(DataFlow.groupBy(MiddleData::getName), MergedData::setMiddleData)
                .addJoin(DataFlow.groupBy(RightData::getName), MergedData::setRightData)
                .dataFlow()
                .mapValues(MergedData::formattedString)
                .map(GroupBy::toMap)
                .id("results"));

        onEvent(new LeftData("greg", 47));
        onEvent(new MiddleData("greg", "male"));
        onEvent(new RightData("greg", "UK"));

        Map<String, String> resultMap = getStreamed("results");

        Assert.assertEquals(1, resultMap.size());
        Assert.assertEquals("47 male UK", resultMap.get("greg"));

        onEvent(new LeftData("greg", 55));
        Assert.assertEquals("55 male UK", resultMap.get("greg"));


        onEvent(new LeftData("tim", 47));
        onEvent(new MiddleData("greg", "male"));
        onEvent(new RightData("greg", "UK"));
    }

    @Test
    public void multiJoinFromHelper() {
        sep(c -> {
            DataFlow.multiJoin(
                            MergedData::new,
                            multiJoinLeg(DataFlow.groupBy(LeftData::getName), MergedData::setLeftData),
                            multiJoinLeg(DataFlow.groupBy(MiddleData::getName), MergedData::setMiddleData),
                            multiJoinLeg(DataFlow.groupBy(RightData::getName), MergedData::setRightData))
                    .mapValues(MergedData::formattedString)
                    .map(GroupBy::toMap)
                    .id("results");
        });

        onEvent(new LeftData("greg", 47));
        onEvent(new MiddleData("greg", "male"));
        onEvent(new RightData("greg", "UK"));

        Map<String, String> resultMap = getStreamed("results");

        Assert.assertEquals(1, resultMap.size());
        Assert.assertEquals("47 male UK", resultMap.get("greg"));

        onEvent(new LeftData("greg", 55));
        Assert.assertEquals("55 male UK", resultMap.get("greg"));


        onEvent(new LeftData("tim", 47));
        onEvent(new MiddleData("greg", "male"));
        onEvent(new RightData("greg", "UK"));
    }


    @Data
    public static class MergedData {
        private LeftData leftData;
        private MiddleData middleData;
        private RightData rightData;

        public String formattedString() {
            return leftData.getAge() + " " + middleData.getSex() + " " + rightData.getCountry();
        }
    }

    @Value
    public static class LeftData {
        String name;
        int age;
    }

    @Value
    public static class MiddleData {
        String name;
        String sex;
    }


    @Value
    public static class RightData {
        String name;
        String country;
    }
}
