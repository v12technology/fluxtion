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
import com.fluxtion.ext.streaming.api.util.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.util.Comparator;

import static com.fluxtion.ext.streaming.api.util.Tuple.numberValComparator;
import static com.fluxtion.ext.streaming.builder.factory.GroupFunctionsBuilder.groupByAvg;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class SortedGroupByTest extends StreamInprocessTest {

    @Test
    public void sortGroupBy() {
        sep((c) -> {
            groupByAvg(CcyPairOrder::getCcyPair, CcyPairOrder::getAmount)
                .comparator(new MyComparator3()).reverse()
                .top(3).id("topOrders");
        });

        WrappedList<Tuple<String, Number>> topOrders = getField("topOrders");
        for (int i = 0; i < 14; i++) {
            onEvent(new CcyPairOrder("EURGBP", 100));
        }
        onEvent(new CcyPairOrder("EURUSD", 50_000));
        onEvent(new CcyPairOrder("EURUSD", 100_000));
        onEvent(new CcyPairOrder("USDCHF", 200_000));
        onEvent(new CcyPairOrder("GBPUSD", 300_000));

        assertThat(topOrders.collection().get(0).getValue().intValue(), is(300_000));
        assertThat(topOrders.collection().get(1).getValue().intValue(), is(200_000));
        assertThat(topOrders.collection().get(2).getValue().intValue(), is(75_000));
        assertThat(topOrders.collection().get(0).getKey(), is("GBPUSD"));
        assertThat(topOrders.collection().get(1).getKey(), is("USDCHF"));
        assertThat(topOrders.collection().get(2).getKey(), is("EURUSD"));

        onEvent(new CcyPairOrder("EURCHF", 400_000));
        onEvent(new CcyPairOrder("GBPJPY", 500_000));

        assertThat(topOrders.collection().get(0).getValue().intValue(), is(500_000));
        assertThat(topOrders.collection().get(1).getValue().intValue(), is(400_000));
        assertThat(topOrders.collection().get(2).getValue().intValue(), is(300_000));
        assertThat(topOrders.collection().get(0).getKey(), is("GBPJPY"));
        assertThat(topOrders.collection().get(1).getKey(), is("EURCHF"));
        assertThat(topOrders.collection().get(2).getKey(), is("GBPUSD"));

    }

    @Test
    public void sortGroupByWithCompaing() {
        sep((c) -> {
            groupByAvg(CcyPairOrder::getCcyPair, CcyPairOrder::getAmount)
                .comparator(numberValComparator()).reverse()
                .top(3).id("topOrders");
        });

        WrappedList<Tuple<String, Number>> topOrders = getField("topOrders");
        for (int i = 0; i < 14; i++) {
            onEvent(new CcyPairOrder("EURGBP", 100));
        }
        onEvent(new CcyPairOrder("EURUSD", 50_000));
        onEvent(new CcyPairOrder("EURUSD", 100_000));
        onEvent(new CcyPairOrder("USDCHF", 200_000));
        onEvent(new CcyPairOrder("GBPUSD", 300_000));

        assertThat(topOrders.collection().get(0).getValue().intValue(), is(300_000));
        assertThat(topOrders.collection().get(1).getValue().intValue(), is(200_000));
        assertThat(topOrders.collection().get(2).getValue().intValue(), is(75_000));
        assertThat(topOrders.collection().get(0).getKey(), is("GBPUSD"));
        assertThat(topOrders.collection().get(1).getKey(), is("USDCHF"));
        assertThat(topOrders.collection().get(2).getKey(), is("EURUSD"));

        onEvent(new CcyPairOrder("EURCHF", 400_000));
        onEvent(new CcyPairOrder("GBPJPY", 500_000));

        assertThat(topOrders.collection().get(0).getValue().intValue(), is(500_000));
        assertThat(topOrders.collection().get(1).getValue().intValue(), is(400_000));
        assertThat(topOrders.collection().get(2).getValue().intValue(), is(300_000));
        assertThat(topOrders.collection().get(0).getKey(), is("GBPJPY"));
        assertThat(topOrders.collection().get(1).getKey(), is("EURCHF"));
        assertThat(topOrders.collection().get(2).getKey(), is("GBPUSD"));

    }

    public static class MyComparator3 implements Comparator<Tuple<?, Number>> {

        @Override
        public int compare(Tuple<?, Number> o1, Tuple<?, Number> o2) {
            return (int) (o1.getValue().doubleValue() - o2.getValue().doubleValue());
        }

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CcyPairOrder {

        String ccyPair;
        double amount;
    }
}
