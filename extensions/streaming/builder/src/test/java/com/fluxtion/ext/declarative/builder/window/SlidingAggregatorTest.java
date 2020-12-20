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

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.time.Clock;
import com.fluxtion.api.time.ClockStrategy;
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import com.fluxtion.ext.streaming.api.window.SlidingAggregator;
import com.fluxtion.ext.streaming.api.window.TimeReset;
import java.util.ArrayList;
import java.util.Comparator;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class SlidingAggregatorTest {

    private Clock clock;
    private TimeResetTest.StatefulTest stateMock;
    private MutableNumber time;
    private TimeReset timer;

    @Before
    public void init() {
        time = new MutableNumber();
        clock = new Clock();
        clock.setClockStrategy(new ClockStrategy.ClockStrategyEvent(time::longValue));
        time.set(1);
        stateMock = new TimeResetTest.StatefulTest();
        timer = new TimeReset(stateMock, 100, clock);

    }

    @Test
    public void testCombine() {
        Object notifier = "";
        LongestWord sum = new LongestWord();
        int size = 10;

        SlidingAggregator<LongestWord> aggregator = new SlidingAggregator(notifier, LongestWord.class, sum, size);
        aggregator.setTimeReset(timer);
        aggregator.init();

        sum.addValue("one");
        validateValue(101, "one", aggregator);
        
        
        sum.addValue("XXXXXX");
        validateValue(101, "one", aggregator);
        validateValue(201, "XXXXXX", aggregator);
        
        sum.addValue("three");
        validateValue(801, "XXXXXX", aggregator);
        validateValue(1_101, "XXXXXX", aggregator);
        validateValue(1_501, "three", aggregator);
        validateValue(11_501, "", aggregator);
    }

    private void validateValue(int time, String value, SlidingAggregator<LongestWord> aggregator) {
        setTime(time);
        aggregator.aggregate();
        assertThat(aggregator.event().getWord(), is(value));
        timer.resetIfNecessary();
    }

    private void setTime(long newTime) {
        time.set(newTime);
        timer.anyEvent("");
    }

    public static class LongestWord implements Stateful<LongestWord> {

        private String word;
        private ArrayList<String> combined = new ArrayList();

        public String addValue(String val) {
            if(word==null || val.length() > word.length()){
                word = val;
            }
            return word;
        }

        public String getWord() {
            return word;
        }

        @Override
        @Initialise
        public void reset() {
            word = "";
            combined.clear();
        }


        @Override
        public void combine(LongestWord other) {
            String otherWord = ((LongestWord)other).word;
            combined.add(otherWord);
            addValue(otherWord);
        }

        @Override
        public void deduct(LongestWord other) {
            String otherWord = ((LongestWord)other).word;
            combined.remove(otherWord);
            if(combined.isEmpty()){
                word = "";
            }else{
                combined.sort(Comparator.comparingInt(String::length).reversed());
                word = combined.get(0);
            }
        }

    }
}
