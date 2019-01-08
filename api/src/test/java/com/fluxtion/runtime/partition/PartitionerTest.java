/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.runtime.partition;

import com.fluxtion.api.partition.Partitioner;
import com.fluxtion.api.event.Event;
import com.fluxtion.api.lifecycle.EventHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class PartitionerTest {

    @Before
    public void beforeTEst() {
        MyHandler.clear();
    }

    @Test
    public void singleFilter() {
        System.out.println("single filter");
        Partitioner<MyHandler> partitioner = new Partitioner(MyHandler::new);
        partitioner.partition(MyEvent::getDay);
        //
        MyEvent monday = new MyEvent("monday");
        MyEvent tuesday = new MyEvent("tuesday");
        //
        partitioner.onEvent(monday);
        partitioner.onEvent(tuesday);
        partitioner.onEvent(tuesday);
        partitioner.onEvent(monday);
        assertThat(MyHandler.instanceCount, is(2));
        assertThat(MyHandler.invokeCount, is(4));
        partitioner.onEvent(new DummyEvent());
        assertThat(MyHandler.instanceCount, is(2));
        assertThat(MyHandler.invokeCount, is(6));
        System.out.println(MyHandler.dayMonthCounts);

    }

    @Test
    public void testMultiKey() {
        System.out.println("testMultiKey");
        Partitioner<MyHandler> partitioner = new Partitioner(MyHandler::new);
        partitioner.partition(MyEvent::getDay, MyEvent::getMonth);
        //
        MyEvent monday = new MyEvent("monday");
        MyEvent tuesday = new MyEvent("tuesday");
        MyEvent monday_march = new MyEvent("monday");
        monday_march.setMonth("march");
        //
        partitioner.onEvent(monday);
        partitioner.onEvent(tuesday);
        partitioner.onEvent(tuesday);
        partitioner.onEvent(monday_march);
        partitioner.onEvent(monday_march);
        //
        System.out.println(MyHandler.dayMonthCounts);
        assertThat(MyHandler.dayMonthCounts.get(monday.toString()), is(1));
        assertThat(MyHandler.dayMonthCounts.get(tuesday.toString()), is(2));
        assertThat(MyHandler.dayMonthCounts.get(monday_march.toString()), is(2));

    }

    @Test
    public void testSingleAndMultiKey() {
        System.out.println("testSingleAndMultiKey");
        Partitioner<MyHandler> partitioner = new Partitioner(MyHandler::new);
        partitioner.partition(MyEvent::getDay, MyEvent::getMonth);
        partitioner.partition(MyEvent::getDay);
        //
        MyEvent monday = new MyEvent("monday");
        MyEvent tuesday = new MyEvent("tuesday");
        MyEvent monday_march = new MyEvent("monday");
        monday_march.setMonth("march");
        //
        partitioner.onEvent(monday);
        partitioner.onEvent(tuesday);
        partitioner.onEvent(tuesday);
        partitioner.onEvent(monday_march);
        partitioner.onEvent(monday_march);
        //
        System.out.println(MyHandler.dayMonthCounts);
        System.out.println(MyHandler.dayCounts);
        assertThat(MyHandler.dayMonthCounts.get(monday.toString()), is(2));
        assertThat(MyHandler.dayMonthCounts.get(tuesday.toString()), is(4));
        assertThat(MyHandler.dayMonthCounts.get(monday_march.toString()), is(4));
        //
        assertThat(MyHandler.dayCounts.get(monday.getDay()), is(6));
        

    }
    
    @Test
    public void testInitialiser(){
        System.out.println("testInitialiser");
        LongAdder adder = new LongAdder();
        Partitioner<MyHandler> partitioner = new Partitioner(MyHandler::new, (e) -> {
            adder.increment();
        });
        partitioner.partition(MyEvent::getDay, MyEvent::getMonth);
        partitioner.partition(MyEvent::getDay);
        //
        MyEvent monday = new MyEvent("monday");
        MyEvent tuesday = new MyEvent("tuesday");
        MyEvent monday_march = new MyEvent("monday");
        monday_march.setMonth("march");
        //
        partitioner.onEvent(monday);
        partitioner.onEvent(tuesday);
        partitioner.onEvent(tuesday);
        partitioner.onEvent(tuesday);
        partitioner.onEvent(tuesday);
        partitioner.onEvent(monday_march);
        partitioner.onEvent(monday_march);
        //
        assertThat(adder.intValue(), is(5));
        
    }

    public static class MyHandler implements EventHandler {

        static int instanceCount;
        static int invokeCount;
        static Map<String, Integer> dayMonthCounts = new HashMap<>();
        static Map<String, Integer> dayCounts = new HashMap<>();

        public static void clear() {
            instanceCount = 0;
            invokeCount = 0;
            dayMonthCounts.clear();
            dayCounts.clear();
        }

        public MyHandler() {
            instanceCount++;
        }

        @Override
        public void onEvent(Event e) {
            dayMonthCounts.compute(e.toString(), (t, u) -> u == null ? 1 : u + 1);
            if (e instanceof MyEvent) {
                System.out.println("processing :" + e);
                dayCounts.compute(((MyEvent) e).day, (t, u) -> u == null ? 1 : u + 1);
            }
            invokeCount++;
        }

    }

    public static class DummyEvent extends Event {
    }

    public static class MyEvent extends Event {

        private String day;
        private String month = "january";

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public MyEvent(String day) {
            this.day = day;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        @Override
        public String toString() {
            return "MyEvent{" + "day=" + day + ", month=" + month + '}';
        }

    }
}
