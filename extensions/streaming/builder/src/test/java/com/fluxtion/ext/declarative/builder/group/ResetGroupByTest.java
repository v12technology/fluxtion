/*
 * Copyright (C) 2019 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.declarative.builder.group;

import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.gt;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.group.Group.groupBy;
import com.fluxtion.ext.streaming.builder.group.GroupByBuilder;
import java.util.HashMap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class ResetGroupByTest extends StreamInprocessTest{

    @Test
    public void resetGroup(){
        sep((c) ->{
            fixedPkg = true;
            Wrapper<Order> largeOrders = select(Order.class).filter( Order::getSize, gt(200));
            GroupByBuilder<Order, OrderSummary> largeOrdersByCcy = groupBy(largeOrders, Order::getCcyPair, OrderSummary.class);
            largeOrdersByCcy.init(Order::getCcyPair, OrderSummary::setCcyPair);
            largeOrdersByCcy.count( OrderSummary::setDealCount);
            largeOrdersByCcy.sum(Order::getSize, OrderSummary::setOrderSize);
            GroupBy<Order, OrderSummary> orderSummary = c.addPublicNode(largeOrdersByCcy.build(), "orderSummary");
            //add reset
            orderSummary.resetNotifier(select(Deal.class));
        });

        GroupBy<Order, OrderSummary> summaryMap = getField("orderSummary");

        sep.onEvent(new Order(1, "EURUSD", 100));
        sep.onEvent(new Order(2, "EURJPY", 250));
        sep.onEvent(new Order(3, "EURJPY", 56));
        sep.onEvent(new Order(4, "EURJPY", 350));
        sep.onEvent(new Order(5, "EURUSD", 250));
        sep.onEvent(new Order(6, "GBPUSD", 150));


        assertThat(summaryMap.getMap().size(), is(2));
        HashMap<String, OrderSummary> orderMap = new HashMap<>();
        summaryMap.getMap().values().stream()
                .map(wrapper -> wrapper.event())
                .forEach(os -> orderMap.put(os.getCcyPair(), os));


        assertThat(2, is(orderMap.get("EURJPY").getDealCount()));
        assertThat(600, is(orderMap.get("EURJPY").getOrderSize()));
        assertThat(1, is(orderMap.get("EURUSD").getDealCount()));
        assertThat(250, is(orderMap.get("EURUSD").getOrderSize()));
        assertThat(true, is(orderMap.get("EURUSD").isEvent()));
        assertThat(true, is(orderMap.get("EURUSD").isEventComplete()));
        assertNull(orderMap.get("GBPUSD"));

        //reset
        sep.onEvent(new Deal());
        assertThat(summaryMap.getMap().size(), is(0));

    }
}
