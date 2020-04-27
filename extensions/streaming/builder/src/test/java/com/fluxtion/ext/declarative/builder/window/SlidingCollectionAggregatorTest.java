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
import com.fluxtion.generator.compiler.InprocessSepCompiler;
import java.io.File;
import static java.util.Arrays.asList;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
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
        time.set(1);
        timer = new TimeReset(inputCollection, 100, clock);
        GenerationContext.setupStaticContext("", "", 
                new File(InprocessSepCompiler.JAVA_TESTGEN_DIR), 
                new File(InprocessSepCompiler.RESOURCE_TEST_DIR));
    }

    @Test
    public void testCombine() {
        Object notifier = "";
        int size = 10;
        ArrayListWrappedCollection outputCollection = new ArrayListWrappedCollection();
        
        SlidingCollectionAggregator aggregator = new SlidingCollectionAggregator(notifier, inputCollection, size);
        aggregator.setTimeReset(timer);
        aggregator.setTargetCollection(outputCollection);
        aggregator.init();
        
        inputCollection.addItem(1);
        validateValue(101, asList(1), aggregator);
        
        inputCollection.addItem(2);
        inputCollection.addItem(3);
        validateValue(150, asList(1), aggregator);
        validateValue(201, asList(1,2,3), aggregator);
        validateValue(1_101, asList(2,3), aggregator);
        validateValue(1_201, asList(), aggregator);
    }
    
    private void validateValue(int time, List expected, SlidingCollectionAggregator aggregator){
        setTime(time);
        aggregator.aggregate();
        Assert.assertThat(aggregator.getTargetCollection().collection(), is(expected));
        timer.resetIfNecessary();
    }

    private void setTime(long newTime) {
        time.set(newTime);
        timer.anyEvent("");
    }
}
