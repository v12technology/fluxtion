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

import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.WrappedList;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.util.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.util.Comparator;

import static com.fluxtion.ext.streaming.builder.factory.GroupFunctionsBuilder.groupByAvg;
import static com.fluxtion.ext.streaming.builder.factory.WindowBuilder.sliding;
import static com.fluxtion.ext.streaming.builder.group.Group.groupBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class SlidingCountGroupByTest extends StreamInprocessTest {

    @Test
    public void groupByFunctionSliding() {
        Class<Tuple<String, Number>> tupleClass = Tuple.generify();
        sep((c) -> {
            GroupBy<Tuple<String, Number>> orderSummary = groupBy(CcyPairOrder::getCcyPair, tupleClass)
                .init(CcyPairOrder::getCcyPair, Tuple::setKey)
                .initCopy(Tuple::copyKey)
                .avg(CcyPairOrder::getAmount, Tuple::setValue)
                .build();
            sliding(orderSummary, 5, 3)
                .comparator(new MyComparator3()).reverse()
                .top(4).id("topOrders");          
        });
        validateSlidingCalc();
    }    
    
    @Test
    public void groupByAvgSliding() {
        sep((c) -> {
            sliding(groupByAvg(CcyPairOrder::getCcyPair, CcyPairOrder::getAmount), 5, 3)
                .comparator(new MyComparator3()).reverse()
                .top(4).id("topOrders");
        });
        validateSlidingCalc();
    }
    
    @Test
    public void groupByAvgPostSliding() {
//        reuseSep = true;
//        fixedPkg = true;
        sep((c) -> {
            groupByAvg(CcyPairOrder::getCcyPair, CcyPairOrder::getAmount)
                .sliding( 5, 3)
                .comparator(Tuple.numberValComparator()).reverse()
                .top(4).id("topOrders");
        });
        validateSlidingCalc();
    }

    private void validateSlidingCalc() {
        WrappedList<Tuple<String, Number>> topOrders = getField("topOrders");
        assertThat(topOrders.size(), is(0));
        for (int i = 0; i < 14; i++) {
            onEvent(new CcyPairOrder("EURUSD", 100));
        }
        onEvent(new CcyPairOrder("EURUSD", 100));
        assertThat(topOrders.size(), is(1));
        for (int i = 0; i < 5; i++) {
            onEvent(new CcyPairOrder("EURUSD", 400));
            assertThat(topOrders.size(), is(1));
        }
        assertThat(topOrders.size(), is(1));
        assertThat(topOrders.collection().get(0).getValue().intValue(), is(200));

        for (int i = 0; i < 5; i++) {
            onEvent(new CcyPairOrder("EURJPY", 5_000));
        }
        assertThat(topOrders.size(), is(2));
        assertThat(topOrders.collection().get(0).getValue().intValue(), is(5_000));
        assertThat(topOrders.collection().get(1).getValue().intValue(), is(250));

        for (int i = 0; i < 10; i++) {
            onEvent(new CcyPairOrder("EURJPY", 5_000));
        }
        assertThat(topOrders.size(), is(1));
        assertThat(topOrders.collection().get(0).getValue().intValue(), is(5_000));

        onEvent(new CcyPairOrder("EURUSD", 100_000));
        onEvent(new CcyPairOrder("USDCHF", 200_000));
        onEvent(new CcyPairOrder("GBPUSD", 300_000));
        onEvent(new CcyPairOrder("EURCHF", 400_000));
        onEvent(new CcyPairOrder("GBPJPY", 500_000));

        assertThat(topOrders.size(), is(4));
        assertThat(topOrders.collection().get(0).getValue().intValue(), is(500_000));
        assertThat(topOrders.collection().get(1).getValue().intValue(), is(400_000));
        assertThat(topOrders.collection().get(2).getValue().intValue(), is(300_000));
        assertThat(topOrders.collection().get(3).getValue().intValue(), is(200_000));

        assertThat(topOrders.collection().get(0).getKey(), is("GBPJPY"));
        assertThat(topOrders.collection().get(1).getKey(), is("EURCHF"));
        assertThat(topOrders.collection().get(2).getKey(), is("GBPUSD"));
        assertThat(topOrders.collection().get(3).getKey(), is("USDCHF"));
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
//    @Value
    public static class CcyPairOrder {

        String ccyPair;
        double amount;
    }

}
