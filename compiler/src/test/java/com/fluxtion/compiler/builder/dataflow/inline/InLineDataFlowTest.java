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
import com.fluxtion.runtime.dataflow.Stateful;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

public class InLineDataFlowTest {

    @Test
    public void inline() {

        MyFunction func = new MyFunction();

        StaticEventProcessor eventProcessor = DataFlow.subscribe(String.class)
                .mapBi(DataFlow.subscribe(Integer.class), func::parseString)
                .resetTrigger(DataFlow.subscribeToSignal("reset"))
                .build();

        eventProcessor.onEvent("world");
        Assert.assertEquals(0, func.getReceivedInt());

        eventProcessor.onEvent(42);
        Assert.assertEquals(42, func.getReceivedInt());

        eventProcessor.onEvent("500");
        Assert.assertEquals(500, func.getReceivedInt());

        eventProcessor.publishSignal("reset");
        Assert.assertEquals(0, func.getReceivedInt());


        eventProcessor = DataFlow.subscribe(String.class)
                .map( func::parseString)
                .resetTrigger(DataFlow.subscribeToSignal("reset"))
                .build();

        eventProcessor.onEvent("world");
        Assert.assertEquals(0, func.getReceivedInt());

        eventProcessor.onEvent(42);
        Assert.assertEquals(0, func.getReceivedInt());

        eventProcessor.onEvent("500");
        Assert.assertEquals(500, func.getReceivedInt());

        eventProcessor.onEvent(42);
        Assert.assertEquals(500, func.getReceivedInt());

        eventProcessor.publishSignal("reset");
        Assert.assertEquals(0, func.getReceivedInt());
    }

    @Data
    public static class MyFunction implements Stateful<Integer> {
        private int receivedInt;

        public Integer parseString(String s) {
            try{
                receivedInt = Integer.parseInt(s);
            }catch (NumberFormatException e){
                receivedInt = 0;
            }
            return receivedInt;
        }

        public Integer parseString(String s, Integer defaultValue) {
            try{
                receivedInt = Integer.parseInt(s);
            }catch (NumberFormatException e){
                receivedInt = defaultValue;
            }
            return receivedInt;
        }

        public Integer reset() {
            receivedInt = 0;
            return receivedInt;
        }
    }
}
