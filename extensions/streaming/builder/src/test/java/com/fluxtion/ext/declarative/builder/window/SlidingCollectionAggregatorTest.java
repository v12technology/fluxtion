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
package com.fluxtion.ext.declarative.builder.window;

import com.fluxtion.api.time.Clock;
import com.fluxtion.api.time.ClockStrategy;
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.streaming.api.ArrayListWrappedCollection;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import com.fluxtion.ext.streaming.api.window.SlidingCollectionAggregator;
import com.fluxtion.ext.streaming.api.window.TimeReset;
import com.fluxtion.generator.compiler.OutputRegistry;
import java.io.File;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class SlidingCollectionAggregatorTest {

    private Clock clock;
    private MutableNumber time;
    private TimeReset timer;
    private final ArrayListWrappedCollection inputCollection = new ArrayListWrappedCollection();

    @Before
    public void init() {
        time = new MutableNumber();
        clock = new Clock();
        clock.setClockStrategy(new ClockStrategy.ClockStrategyEvent(time::longValue));
        time.set(0);
        timer = new TimeReset(inputCollection, 100, clock);
        GenerationContext.setupStaticContext("", "",
            new File(OutputRegistry.JAVA_TESTGEN_DIR),
            new File(OutputRegistry.RESOURCE_TEST_DIR));
    }

    @Test
    public void testTImedSlidingCollection() {
        Object notifier = "";
        int size = 10;
        ArrayListWrappedCollection outputCollection = new ArrayListWrappedCollection();

        SlidingCollectionAggregator aggregator = new SlidingCollectionAggregator(notifier, inputCollection, size);
        aggregator.setTimeReset(timer);
        aggregator.setTargetCollection(outputCollection);
        aggregator.init();

        inputCollection.addItem(1);
        validateValue(25, asList(), aggregator);
        inputCollection.addItem(99);
        validateValue(45, asList(), aggregator);
        inputCollection.addItem(555);
        validateValue(85, asList(), aggregator);

        inputCollection.addItem(2);
        inputCollection.addItem(3);
        validateValue(150, asList(), aggregator);
        validateValue(450, asList(), aggregator);
        validateValue(1_099, asList(1, 99, 555, 2, 3), aggregator);
        validateValue(1_100, asList(2, 3), aggregator);
        validateValue(1_105, asList(2, 3), aggregator);
        validateValue(1_200, asList(), aggregator);
        validateValue(1_451, asList(), aggregator);
    }

    @Test
    public void testCountSlidingColllection() {
        Object notifier = "";
        int bucketCount = 3;
        int bucketSize = 5;
        ArrayListWrappedCollection outputCollection = new ArrayListWrappedCollection();
        SlidingCollectionAggregator aggregator = new SlidingCollectionAggregator(notifier, inputCollection, bucketCount);
        aggregator.setTargetCollection(outputCollection);
        aggregator.init();
        //send some data
        int i;
        for (i = 0; i < 3 * bucketSize; i++) {
//            System.out.println(" -> " + i);
            inputCollection.addItem(i);
            if (i > 0 && i % bucketSize == 0) {
                aggregator.aggregate();
            }
        }
        assertThat(aggregator.getTargetCollection().collection(), is(Arrays.asList()));

        for (; i < 5 * bucketSize; i++) {
//            System.out.println(" -> " + i);
            inputCollection.addItem(i);
            if (i > 0 && i % bucketSize == 0) {
                aggregator.aggregate();
            }
        }
        assertThat(aggregator.getTargetCollection().collection(), is(Arrays.asList(6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)));
//        System.out.println(" -> " + i);
        inputCollection.addItem(i);
        aggregator.aggregate();
        assertThat(aggregator.getTargetCollection().collection(), is(Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25)));
    }

    private void validateValue(int time, List expected, SlidingCollectionAggregator aggregator) {
        setTime(time);
        aggregator.aggregate();
        assertThat(aggregator.getTargetCollection().collection(), is(expected));
        timer.resetIfNecessary();
    }

    private void setTime(long newTime) {
        time.set(newTime);
        timer.anyEvent("");
    }
}
