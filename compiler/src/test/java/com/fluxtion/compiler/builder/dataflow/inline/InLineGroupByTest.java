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

package com.fluxtion.compiler.builder.dataflow.inline;

import com.fluxtion.compiler.builder.dataflow.DataFlow;
import com.fluxtion.compiler.builder.dataflow.GroupByTest;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import com.fluxtion.runtime.dataflow.groupby.GroupByKey;
import lombok.SneakyThrows;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;

public class InLineGroupByTest {

    @SneakyThrows
    @Test
    public void groupByCompoundKeyIdentityTest() {
        Map<GroupByKey<GroupByTest.Data>, GroupByTest.Data> expected = new HashMap<>();
        StaticEventProcessor processor = DataFlow
                .groupByFields(GroupByTest.Data::getName, GroupByTest.Data::getValue)
                .map(GroupBy::toMap)
                .id("results")
                .build();

        GroupByTest.Data data_A_25 = new GroupByTest.Data("A", 25);
        GroupByTest.Data data_A_50 = new GroupByTest.Data("A", 50);
        processor.onEvent(data_A_25);
        processor.onEvent(data_A_50);

        GroupByKey<GroupByTest.Data> key = new GroupByKey<>(GroupByTest.Data::getName, GroupByTest.Data::getValue);

        expected.put(key.toKey(data_A_25), new GroupByTest.Data("A", 25));
        expected.put(key.toKey(data_A_50), new GroupByTest.Data("A", 50));
        Map<GroupByKey<GroupByTest.Data>, GroupByTest.Data> actual = processor.getStreamed("results");
        MatcherAssert.assertThat(actual, is(expected));

        GroupByTest.Data data_A_10 = new GroupByTest.Data("A", 10);
        GroupByTest.Data data_B_11 = new GroupByTest.Data("B", 11);
        processor.onEvent(data_A_10);
        processor.onEvent(data_B_11);

        expected.put(key.toKey(data_A_10), new GroupByTest.Data("A", 10));
        expected.put(key.toKey(data_B_11), new GroupByTest.Data("B", 11));
        expected.put(key.toKey(data_A_25), new GroupByTest.Data("A", 25));
        expected.put(key.toKey(data_A_50), new GroupByTest.Data("A", 50));

        MatcherAssert.assertThat(actual, is(expected));
    }
}
