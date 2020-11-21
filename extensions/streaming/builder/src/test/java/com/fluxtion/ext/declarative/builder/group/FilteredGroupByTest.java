/* 
 *  Copyright (C) [2016]-[2017] V12 Technology Limited
 *  
 *  This software is subject to the terms and conditions of its EULA, defined in the
 *  file "LICENCE.txt" and distributed with this software. All information contained
 *  herein is, and remains the property of V12 Technology Limited and its licensors, 
 *  if any. This source code may be protected by patents and patents pending and is 
 *  also protected by trade secret and copyright law. Dissemination or reproduction 
 *  of this material is strictly forbidden unless prior written permission is 
 *  obtained from V12 Technology Limited.  
 */
package com.fluxtion.ext.declarative.builder.group;

import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.gt;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.positive;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.group.Group.groupBy;
import com.fluxtion.ext.streaming.builder.group.GroupByBuilder;
import com.fluxtion.junit.Categories;
import java.util.HashMap;
import java.util.Optional;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 *
 * @author Greg Higgins
 */
public class FilteredGroupByTest extends StreamInprocessTest {

    @Test
    @Category(Categories.FilterTest.class)
    public void testFilteredGroupBy() {
        sep(c -> {
            Wrapper<Order> largeOrders = select(Order.class).filter(Order::getSize, gt(200));
            GroupByBuilder<Order, OrderSummary> largeOrdersByCcy = groupBy(largeOrders, Order::getCcyPair, OrderSummary.class);
            largeOrdersByCcy.init(Order::getCcyPair, OrderSummary::setCcyPair);
            largeOrdersByCcy.count(OrderSummary::setDealCount);
            largeOrdersByCcy.sum(Order::getSize, OrderSummary::setOrderSize)
                    .build().id("orderSummary");
        });
        //events
        sep.onEvent(new Order(1, "EURUSD", 100));
        sep.onEvent(new Order(2, "EURJPY", 250));
        sep.onEvent(new Order(3, "EURJPY", 56));
        sep.onEvent(new Order(4, "EURJPY", 350));
        sep.onEvent(new Order(5, "EURUSD", 250));
        sep.onEvent(new Order(6, "GBPUSD", 150));
        //tests
        GroupBy<OrderSummary> summaryMap = getField("orderSummary");
        assertThat(summaryMap.getMap().size(), is(2));
        HashMap<String, OrderSummary> orderMap = new HashMap<>();
        summaryMap.stream().forEach(os -> orderMap.put(os.getCcyPair(), os));
        assertThat(2, is(orderMap.get("EURJPY").getDealCount()));
        assertThat(600, is(orderMap.get("EURJPY").getOrderSize()));
        assertThat(1, is(orderMap.get("EURUSD").getDealCount()));
        assertThat(250, is(orderMap.get("EURUSD").getOrderSize()));
        assertThat(true, is(orderMap.get("EURUSD").isEvent()));
        assertThat(true, is(orderMap.get("EURUSD").isEventComplete()));
        assertNull(orderMap.get("GBPUSD"));
    }

    @Test
    @Category(Categories.FilterTest.class)
    public void test() {
        sep(c -> {
            Wrapper<Deal> validDeal = select(Deal.class).filter(Deal::getDealtSize, positive());
            GroupByBuilder<Order, OrderSummary> orders = groupBy(Order.class, Order::getId, OrderSummary.class);
            GroupByBuilder<Deal, OrderSummary> deals = orders.join(validDeal, Deal::getOrderId);
            //set default vaules for a group by row
            orders.init(Order::getCcyPair, OrderSummary::setCcyPair);
            orders.init(Order::getId, OrderSummary::setOrderId);
            //aggregate function values
            orders.sum(Order::getSize, OrderSummary::setOrderSize);
            deals.count(OrderSummary::setDealCount);
            deals.avg(Deal::getDealtSize, OrderSummary::setAvgDealSize);
            deals.sum(Deal::getDealtSize, OrderSummary::setVolumeDealt);
            orders.build().id("orderSummary");
        });
        //events
        sep.onEvent(new Order(1, "EURUSD", 2_000_000));
        sep.onEvent(new Order(2, "EURJPY", 100_000_000));
        sep.onEvent(new Deal(1001, 2, 4_000_000));
        sep.onEvent(new Deal(1002, 2, 17_000_000));
        sep.onEvent(new Deal(1003, 1, 1_500_000));
        sep.onEvent(new Deal(13, 1, -23));
        sep.onEvent(new Deal(14, 1, 0));
        sep.onEvent(new Deal(14, 1, -50_000_000));
        sep.onEvent(new Deal(1004, 1, 100_000));
        //tests
        GroupBy<OrderSummary> summaryMap = getField("orderSummary");
        assertThat(summaryMap.size(), is(2));
        Optional<OrderSummary> euOrders = summaryMap.stream()
                .filter(summary -> summary.getCcyPair().equalsIgnoreCase("EURUSD"))
                .findFirst();
        assertThat(1_600_000, is((int) euOrders.get().getVolumeDealt()));
        assertThat(800_000, is((int) euOrders.get().getAvgDealSize()));
        assertThat(2, is((int) euOrders.get().getDealCount()));
        assertThat(1, is((int) euOrders.get().getOrderId()));
        assertThat(2_000_000, is((int) euOrders.get().getOrderSize()));
        assertThat("EURUSD", is(euOrders.get().getCcyPair()));
    }

    public static class GreaterThan implements com.fluxtion.ext.streaming.api.Test {

        public boolean greaterThan(double op1, double op2) {
            return op1 > op2;
        }
    }

}
