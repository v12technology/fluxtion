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
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.util.Tuple;
import org.junit.Test;

import static com.fluxtion.ext.streaming.builder.factory.GroupFunctionsBuilder.*;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsBuilder.cumSum;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class GroupByFunctionBuilderTest extends StreamInprocessTest {

    @Test
    public void groupBySumTest() {
        sep((c) -> {
            groupBySum(Deal::getCcyPair, Deal::getDealtSize).id("dealsByCcyPair");//.log("{}");
        });

        Deal deal = new Deal();
        deal.setCcyPair("EURUSD");
        deal.setDealtSize(100);
        onEvent(deal);
        onEvent(deal);
        onEvent(deal);
        deal.setCcyPair("EURJPY");
        deal.setDealtSize(50_000);
        onEvent(deal);
        deal.setDealtSize(150_000);
        onEvent(deal);

        GroupBy<Tuple<String, Double>> dealsByCcyPair = getField("dealsByCcyPair");
        assertThat(dealsByCcyPair.value("EURUSD").getValue(), is(300d));
        assertThat(dealsByCcyPair.value("EURJPY").getValue(), is(200_000d));

    }

    @Test
    public void groupByCountTest() {
        fixedPkg = true;
        sep((c) -> {
            groupByCount(Deal::getCcyPair).id("dealsByCcyPair");//.log("{}");
        });
        Deal deal = new Deal();
        deal.setCcyPair("EURUSD");
        deal.setDealtSize(100);
        onEvent(deal);
        onEvent(deal);
        onEvent(deal);
        deal.setCcyPair("EURJPY");
        deal.setDealtSize(50_000);
        onEvent(deal);
        deal.setDealtSize(150_000);
        onEvent(deal);

        GroupBy<Tuple<String, Integer>> dealsByCcyPair = getField("dealsByCcyPair");
        assertThat(dealsByCcyPair.value("EURUSD").getValue(), is(3));
        assertThat(dealsByCcyPair.value("EURJPY").getValue(), is(2));
    }

    @Test
    public void groupBySingleArgFunction() {
        //TODO fill in test
        fixedPkg = true;
        sep((c) -> {
            groupByCalc(Deal::getCcyPair, Deal::getDealtSize, cumSum()).id("dealsByCcyPair");
        });

        Deal deal = new Deal();
        deal.setCcyPair("EURUSD");
        deal.setDealtSize(100);
        onEvent(deal);
        onEvent(deal);
        onEvent(deal);
        deal.setCcyPair("EURJPY");
        deal.setDealtSize(50_000);
        onEvent(deal);
        deal.setDealtSize(150_000);
        onEvent(deal);

        GroupBy<Tuple<String, Number>> dealsByCcyPair = getField("dealsByCcyPair");
        assertThat(dealsByCcyPair.value("EURUSD").getValue().intValue(), is(300));
        assertThat(dealsByCcyPair.value("EURJPY").getValue().intValue(), is(200_000));

    }
}
