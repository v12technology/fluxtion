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
package com.fluxtion.ext.declarative.builder.group;

import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.WrappedList;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.util.Tuple;
import static com.fluxtion.ext.streaming.builder.factory.GroupFunctionsBuilder.groupByAvg;
import static com.fluxtion.ext.streaming.builder.factory.WindowBuilder.sliding;
import static com.fluxtion.ext.streaming.builder.group.Group.groupBy;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import lombok.Value;
import org.junit.Test;
import static com.fluxtion.ext.streaming.builder.group.Group.groupBy;
import static com.fluxtion.ext.streaming.builder.group.Group.groupBy;
import static com.fluxtion.ext.streaming.builder.group.Group.groupBy;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class SlidingGroupByTest extends StreamInprocessTest {

    @Test
    public void slideTest() throws Exception {
        sep((c) -> {
            c.setGenerateLogging(true);
            GroupBy<OrderSummary> orderSummary = groupBy(Order::getCcyPair, OrderSummary.class)
                .init(Order::getCcyPair, OrderSummary::setCcyPair)
                .initCopy(OrderSummary::initCopy)
                .avg(Order::getSize, OrderSummary::setAvgDealSize)
                .build().id("sourceGroup");
            //sliding window
            WrappedList<OrderSummary> sortedOrders = sliding(orderSummary, 5, 3)
                .id("targetSliding")
                .comparator(new MyComparator()).reverse();

            sortedOrders.top(4)
                .map(SlidingGroupByTest::x);
//                .log("\n----START TOP ----\n{}\n----END TOP----\n");
            sortedOrders.last(3)
                .map(SlidingGroupByTest::x);
//                .log("\n----START LAST----\n{}\n----END LAST----\n");
        }
        );
        sendSampleData();

    }

    @Test
    public void simpleSliding() {
        sep((c) -> {
            c.setGenerateLogging(true);
            sliding(groupByAvg(Order::getCcyPair, Order::getSizeDouble), 5, 3)
                .comparator(new MyComparator3()).reverse()
                .top(4)
                .map(new MapTupleToString("ccyPair:", " avgDealSize:")::collAsString)
                .log()
                    ;
        });
        sendSampleData();
    }

    @Test
    public void failingAvg() {
        sep((c) -> {
            c.setGenerateLogging(true);
            sliding(groupByAvg(Order::getCcyPair, Order::getSize), 5, 3)
                .comparator(new MyComparator3()).reverse()
                .top(4)
                .map(new MapTupleToString("ccyPair:", " avgDealSize:")::collAsString);
        });
        sendSampleData();
    }

    @Test
    public void sortGroupBy() {
        sep((c) -> {
            c.setGenerateLogging(true);
            groupByAvg(Order::getCcyPair, Order::getSize)
                .comparator(new MyComparator3()).reverse()
                .top(5);
//                .map(new MapTupleToString("ccyPair:", " avgDealSize:")::collAsString);
        });
        sendSampleData();
        onEvent(new Order(5, "EURUSD-5" , 50_000));
    }

    private void sendSampleData() {
        int i = 0;
        for (; i < 15; i++) {
            onEvent(new Order(i % 5, "EURUSD-" + (i % 3), (i % 5) * 10));
        }
        for (; i < 50; i++) {
            onEvent(new Order(i % 5, "EURJPY-" + (i % 20), (i % 5) * 10));
        }
    }

    public static String x(Collection<OrderSummary> orders) {
        return orders.stream()
            .map(o -> "ccyPair:" + o.getCcyPair() + " avgDealSize:" + o.getAvgDealSize())
            .collect(Collectors.joining("\n"));
    }

    public static class MyComparator implements Comparator<OrderSummary> {

        @Override
        public int compare(OrderSummary o1, OrderSummary o2) {
            if (o1 == null || o2 == null) {
                return 0;
            }
            return o1.getAvgDealSize() - o2.getAvgDealSize();
        }

    }

    @Value
    public static class MapTupleToString {

        String keyPrefix;
        String valuePrefix;

        public <K, V> String collAsString(Collection<Tuple<K, V>> tuples) {
            return (tuples.stream()
                .map(this::asString)
                .collect(Collectors.joining("\n")));
        }

        public <K, V> String asString(Tuple<K, V> tuple) {
            return keyPrefix + tuple.getKey() + valuePrefix + tuple.getValue();
        }

    }

    public static class MyComparator3 implements Comparator<Tuple<?, Number>> {

        @Override
        public int compare(Tuple<?, Number> o1, Tuple<?, Number> o2) {
            return (int) (o1.getValue().doubleValue() - o2.getValue().doubleValue());
        }

    }

}
