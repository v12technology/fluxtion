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
import com.fluxtion.runtime.StaticEventProcessor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class InLineSinkTest {

    @Test
    public void sinkTest() {
        StaticEventProcessor eventProcessor = DataFlow.subscribe(String.class)
                .sink("out")
                .build();

        List<String> results = new ArrayList<>();
        eventProcessor.addSink("out", (String s) -> results.add(s));

        eventProcessor.onEvent("1");
        eventProcessor.onEvent("2");
        eventProcessor.onEvent("3");

        MatcherAssert.assertThat(results, Matchers.contains("1", "2", "3"));
    }
}
