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

import com.fluxtion.runtime.StaticEventProcessor;
import org.junit.Test;

public class InLineDataFlowTest {

    @Test
    public void inline() {
        StaticEventProcessor eventProcessor = DataFlow.subscribe(String.class)
                .mapBiFunction(InLineDataFlowTest::x, DataFlow.subscribe(Integer.class))
                .console("Hello {}")
                .build();

        eventProcessor.onEvent("world");
        eventProcessor.onEvent(42);

//        EventProcessor eventProcessor2 = Fluxtion.interpret(c -> {
//            DataFlow.subscribe(String.class)
//                    .console("Hello, {}");
//        });
//        eventProcessor2.init();
//        eventProcessor2.onEvent("again :)");

    }

    private static Object x(String s, Integer integer) {
        return s + " -> " + integer;
    }
}
