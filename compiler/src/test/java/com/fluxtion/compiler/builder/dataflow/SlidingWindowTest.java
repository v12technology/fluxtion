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

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.helpers.Aggregates;
import org.junit.Assert;
import org.junit.Test;

public class SlidingWindowTest extends MultipleSepTargetInProcessTest {

    public SlidingWindowTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }


    @Test
    public void testSizedSlidingWindow() {
        sep(c -> DataFlow
                .subscribe(Integer.class)
                .slidingAggregateByCount(Aggregates.intSumFactory(), 5)
                .id("sum")
        );

        int[] expected = new int[]{0, 0, 0, 0, 10, 15, 20, 25, 30, 35};

        for (int i = 0; i < 10; i++) {
            onEvent(i);
            if (i < 4) {
                Assert.assertNull(getStreamed("sum", Integer.class));
            } else {
                Assert.assertEquals(expected[i], getStreamed("sum", Integer.class).intValue());
            }
        }
    }

    @Test
    public void slidingByTimeTest() {
        sep(c -> DataFlow
                .subscribe(Integer.class)
                .slidingAggregate(Aggregates.intSumFactory(), 100, 2)
                .id("sum"));

        startTime(0);

        setTime(50);
        onEvent(30);
        setTime(80);
        onEvent(50);
        Assert.assertNull(getStreamed("sum", Integer.class));

        setTime(150);
        onEvent(60);
        Assert.assertNull(getStreamed("sum", Integer.class));


        setTime(230);
        onEvent(70);
        Assert.assertEquals(140, getStreamed("sum", Integer.class).intValue());

        setTime(350);
        onEvent(90);
        Assert.assertEquals(130, getStreamed("sum", Integer.class).intValue());

        tick(550);
        Assert.assertEquals(90, getStreamed("sum", Integer.class).intValue());

        onEvent(888888888);
        Assert.assertEquals(90, getStreamed("sum", Integer.class).intValue());

        setTime(910);
        onEvent(125);
        Assert.assertEquals(0, getStreamed("sum", Integer.class).intValue());


        setTime(1155);
        onEvent(500);
        Assert.assertEquals(125, getStreamed("sum", Integer.class).intValue());

        tick(1199);
        Assert.assertEquals(125, getStreamed("sum", Integer.class).intValue());

        tick(1200);
        Assert.assertEquals(500, getStreamed("sum", Integer.class).intValue());
    }


//    public static void buildGraphSliding(EventProcessorConfig processorConfig) {
//        DataFlow.subscribe(Integer.class)
//                .slidingAggregate(Aggregates.intSumFactory(), 300, 4)
//                .console("current sliding 1.2 second sum:{} eventTime:%dt");
//    }
//
//    public static void main(String[] args) throws InterruptedException {
//        var processor = Fluxtion.interpret(SlidingWindowTest::buildGraphSliding);
//        processor.init();
//        Random rand = new Random();
//
//        try (ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor()) {
//            executor.scheduleAtFixedRate(
//                    () -> {
////                        processor.onEvent("tick");
//                        processor.onEvent(rand.nextInt(100));
//                    },
//                    10, 10, TimeUnit.MILLISECONDS);
//            Thread.sleep(4_000);
//        }
//    }
}
