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
package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.ext.streaming.api.Duration;
import com.fluxtion.ext.streaming.api.WrappedList;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.avg;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.count;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.cumSum;
import static com.fluxtion.ext.streaming.builder.factory.WindowBuilder.sliding;
import java.util.Arrays;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class StreamWindow extends StreamInprocessTest {

    @Test
    public void sumSlidingCountWrapper() {
        final int bucketSize = 5;
        final int bucketsPerPublish = 3;
        sep(c -> {
            select(Integer.class)
                .sliding(cumSum(), bucketSize, bucketsPerPublish).id("cumSum");
        });
        testCumSum(0, 0, 0);
        testCumSum(2, 11, 0);
        testCumSum(2, 4, 30);
        testCumSum(10, 1, 30);
        testCumSum(10, 4, 70);
    }

    @Test
    public void sumSlidingCountImpliedSelect() {
        final int bucketSize = 5;
        final int bucketsPerPublish = 3;
        sep(c -> {
            select(Integer.class).
                sliding(cumSum(), bucketSize, bucketsPerPublish).id("cumSum");
        });
        testCumSum(0, 0, 0);
        testCumSum(2, 15, 30);
        testCumSum(10, 1, 30);
        testCumSum(10, 4, 70);
    }

    @Test
    public void sumSlidingTimedWrapper() {
        final int bucketSize = 500;
        final int bucketsPerPublish = 3;
        sep(c -> {
            select(Integer.class).
                sliding(cumSum(), Duration.millis(bucketSize), bucketsPerPublish).id("cumSum");
        });//, "sumSlidingTimedWrapper.SumSlidingTimedWrapper");
        testCumSumTime(0, 0, 0, 0);
        testCumSumTime(2, 100, 15, 30);
        testCumSumTime(2, 100, 2, 30);
        testCumSumTime(10, 100, 3, 54);
    }

    @Test
    public void sumSlidingTimedImpliedSelect() {
        final int bucketSize = 500;
        final int bucketsPerPublish = 3;
        sep(c -> {
            select(Integer.class).
                sliding(cumSum(), Duration.millis(bucketSize), bucketsPerPublish).id("cumSum");
        });
        testCumSumTime(0, 0, 0, 0);
        testCumSumTime(2, 100, 15, 30);
        testCumSumTime(2, 100, 2, 30);
        testCumSumTime(10, 100, 3, 54);
    }

    @Test
    public void collectionSlidingCountWrapper() {
        final int bucketSize = 5;
        final int bucketsPerPublish = 3;
        sep(c -> {
            select(Integer.class).
                sliding(bucketSize, bucketsPerPublish).id("collection");
        });

        int i;
        for (i = 0; i < 2 * bucketSize; i++) {
            onEvent(i);
        }
        WrappedList<Number> collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList()));

        for (; i < 3 * bucketSize; i++) {
            onEvent(i);
        }
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)));

        for (; i < 6 * bucketSize; i++) {
            onEvent(i);
        }
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList(15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29)));
    }

    @Test
    public void collectionSlidingCountImpliedSelect() {
        final int bucketSize = 5;
        final int bucketsPerPublish = 3;
        sep(c -> {
            select(Integer.class).
                sliding(bucketSize, bucketsPerPublish).id("collection");
        });

        int i;
        for (i = 0; i < 2 * bucketSize; i++) {
            onEvent(i);
        }
        WrappedList<Number> collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList()));

        for (; i < 3 * bucketSize; i++) {
            onEvent(i);
        }
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)));

        for (; i < 6 * bucketSize; i++) {
            onEvent(i);
        }
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList(15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29)));
    }

    @Test
    public void publishCount() {
        final int bucketSize = 5;
        final int bucketsPerPublish = 3;
        final int messageCount = 28;
        final int expectedPublishCount = 3;
        sep(c -> {
            select(Double.class)
                .sliding(avg(), bucketSize, bucketsPerPublish)
                .map(count()).id("updatesAvg");

            select(Double.class)
                .sliding(cumSum(), bucketSize, bucketsPerPublish)
                .map(count()).id("updatesCumSum");
        });
        for (int i = 1; i < messageCount; i++) {
            onEvent((double) i);
        }
        Number updatesAvg = getWrappedField("updatesAvg");
        assertThat(updatesAvg.intValue(), is(expectedPublishCount));
        Number updatesCumSum = getWrappedField("updatesCumSum");
        assertThat(updatesCumSum.intValue(), is(expectedPublishCount));
    }

    @Test
    public void collectionSlidingImpliedSelect() {
        final int bucketSize = 500;
        final int bucketsPerPublish = 3;

        sep(c -> {
            select(Integer.class)
                .sliding(Duration.millis(bucketSize), bucketsPerPublish).id("collection");
        });

        setTime(0);
        setTime(100);
        onEvent(1);
        onEvent(1);
        setTime(600);
        onEvent(2);
        setTime(1100);
        onEvent(3);
        onEvent(3);
        setTime(1200);
        WrappedList<Number> collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList()));

        tick(1200);
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList()));

        tick(1600);
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList(1, 1, 2, 3, 3)));

        tick(2000);
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList(2, 3, 3)));

        tick(2500);
        collection = getField("collection");
        assertThat(collection.collection(), is(Arrays.asList(3, 3)));
    }

    public void testCumSum(int add, int loopCount, int expected) {
        for (int i = 0; i < loopCount; i++) {
            onEvent(add);
        }
        Number cumSum = getWrappedField("cumSum");
        assertThat(cumSum.intValue(), is(expected));
    }

    public void testCumSumTime(int add, int timeDelta, int loopCount, int expected) {
        if (loopCount == 0) {
            advanceTime(timeDelta);
        }
        for (int i = 0; i < loopCount; i++) {
            advanceTime(timeDelta);
            onEvent(add);
        }
        Number cumSum = getWrappedField("cumSum");
        assertThat(cumSum.intValue(), is(expected));
    }
}
