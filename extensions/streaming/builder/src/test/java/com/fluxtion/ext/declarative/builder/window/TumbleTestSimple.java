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

import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.declarative.builder.stream.StreamInProcessTest;
import com.fluxtion.ext.streaming.api.Duration;
import com.fluxtion.ext.streaming.api.WrappedList;
import com.fluxtion.generator.compiler.OutputRegistry;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsBuilder.cumSum;
import static com.fluxtion.ext.streaming.builder.factory.WindowBuilder.tumble;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class TumbleTestSimple extends StreamInProcessTest {

    @Test
    public void sumTumbleCountWrapper() {
        final int bucketSize = 5;
        sep(c -> {
            tumble(select(Integer.class), cumSum(), bucketSize).id("cumSum");
        });
        testCumSum(0, 0, 0);
        testCumSum(2, 4, 0);
        testCumSum(2, 1, 10);
        testCumSum(5, 4, 10);
        testCumSum(6, 3, 26);
        testCumSum(6, 2, 26);
        testCumSum(6, 1, 30);
    }

    @Test
    public void sumTumbleCountImpliedSelect() {
        final int bucketSize = 5;        
        sep(c -> {
            tumble(Integer.class, cumSum(), bucketSize).id("cumSum");
        });
        testCumSum(0, 0, 0);
        testCumSum(2, 4, 0);
        testCumSum(2, 1, 10);
        testCumSum(5, 4, 10);
        testCumSum(6, 3, 26);
        testCumSum(6, 2, 26);
        testCumSum(6, 1, 30);

    }

    @Test
    public void sumTumbleTimedWrapper() {
        final int bucketSize = 500;
        sep(c -> {
            tumble(select(Integer.class), cumSum(), Duration.millis(bucketSize)).id("cumSum");
        });
        testCumSumTime(0, 0, 0, 0);
        testCumSumTime(2, 100, 2, 0);
        testCumSumTime(2, 100, 13, 10);
        testCumSumTime(20, 100, 2, 10);
        testCumSumTime(50, 100, 2, 10);
        testCumSumTime(60, 100, 3, 200);
    }

    @Test
    public void sumTumbleTimedImpliedSelect() {
        final int bucketSize = 500;
        sep(c -> {
            tumble(Integer.class, cumSum(), Duration.millis(bucketSize)).id("cumSum");
        });
        testCumSumTime(0, 0, 0, 0);
        testCumSumTime(2, 100, 2, 0);
        testCumSumTime(2, 100, 13, 10);
        testCumSumTime(20, 100, 2, 10);
        testCumSumTime(50, 100, 2, 10);
        testCumSumTime(60, 100, 3, 200);
    }

    @Test
    public void collectionTumbleCountWrapper() {
        final int bucketSize = 5;
        sep(c -> {
            tumble(select(Integer.class).collect(), bucketSize).id("collection");
        });

        int i;
        for (i = 0; i < 2; i++) {
            onEvent(i);
        }
        WrappedList<Number> collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList()));

        for (; i < 5; i++) {
            onEvent(i);
        }
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList(0, 1, 2, 3, 4)));

        for (; i < 7; i++) {
            onEvent(i);
        }
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList(0, 1, 2, 3, 4)));
        
        
        for (; i < 23; i++) {
            onEvent(i);
        }
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList( 15, 16, 17, 18, 19)));
    }

    @Test
    public void collectionTumbleCountImpliedSelect() {
        final int bucketSize = 5;
        sep(c -> {
            tumble(Integer.class, bucketSize).id("collection");
        });

        int i;
        for (i = 0; i < 2; i++) {
            onEvent(i);
        }
        WrappedList<Number> collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList()));

        for (; i < 5; i++) {
            onEvent(i);
        }
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList(0, 1, 2, 3, 4)));

        for (; i < 7; i++) {
            onEvent(i);
        }
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList(0, 1, 2, 3, 4)));
        
        
        for (; i < 23; i++) {
            onEvent(i);
        }
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList( 15, 16, 17, 18, 19)));
    }
    
    @Test
    public void collectionTumbleTimeWrpper() {
        final int bucketSize = 500;
        GenerationContext.setupStaticContext("", "",
            new File(OutputRegistry.JAVA_TESTGEN_DIR),
            new File(OutputRegistry.RESOURCE_TEST_DIR));

        sep(c -> {
            tumble(select(Integer.class).collect(), Duration.millis(bucketSize)).id("collection");
        });
        
        setTime(0);
        setTime(100);
        onEvent(1);
        onEvent(1);
        WrappedList<Number> collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList()));
        
        tick(600);
        onEvent(2);
        tick(700);
        onEvent(4);
        tick(900);
        onEvent(9);
        onEvent(2);
        onEvent(4);
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList(1,1)));
        
        tick(1100);
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList(2, 4, 9, 2, 4)));
        
        tick(1500);
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList()));
    }

    @Test
    public void collectionTumbleImpliedSelect() {
        final int bucketSize = 500;
        GenerationContext.setupStaticContext("", "",
            new File(OutputRegistry.JAVA_TESTGEN_DIR),
            new File(OutputRegistry.RESOURCE_TEST_DIR));

        sep(c -> {
            tumble(Integer.class, Duration.millis(bucketSize)).id("collection");
        });
        
        setTime(0);
        setTime(100);
        onEvent(1);
        onEvent(1);
        WrappedList<Number> collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList()));
        
        tick(600);
        onEvent(2);
        tick(700);
        onEvent(4);
        tick(900);
        onEvent(9);
        onEvent(2);
        onEvent(4);
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList(1,1)));
        
        tick(1100);
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList(2, 4, 9, 2, 4)));
        
        tick(1500);
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList()));
    }

    public void testCumSum(int add, int loopCount, int expected) {
        for (int i = 0; i < loopCount; i++) {
            onEvent(add);
        }
        Number cumSum = getWrappedField("cumSum");
        assertThat(cumSum.intValue(), is(expected));
    }

    public void testCumSumTime(int add, int timeDelta, int loopCount, int expected) {
        for (int i = 0; i < loopCount; i++) {
            advanceTime(timeDelta);
            onEvent(add);
        }
        Number cumSum = getWrappedField("cumSum");
        assertThat(cumSum.intValue(), is(expected));
    }
}
