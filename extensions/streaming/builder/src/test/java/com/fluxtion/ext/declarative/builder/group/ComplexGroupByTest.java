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
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsLibrary.cumSum;
import static com.fluxtion.ext.streaming.builder.group.Group.groupBy;
import com.fluxtion.ext.streaming.builder.group.GroupByBuilder;
import com.fluxtion.junit.Categories;
import java.util.Optional;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 *
 * @author Greg Higgins
 */
public class ComplexGroupByTest extends StreamInprocessTest {

    @Test
    @Category(Categories.FilterTest.class)
    public void test() {
        sep(c -> {
            GroupByBuilder<Order, OrderSummary> orders = groupBy(Order::getId, OrderSummary.class);
            GroupByBuilder<Deal, OrderSummary> deals = orders.join(Deal.class, Deal::getOrderId);
            //set default vaules for a group by row
            orders.init(Order::getCcyPair, OrderSummary::setCcyPair);
            orders.init(Order::getId, OrderSummary::setOrderId);
            //set last deal id
            deals.init(Deal::getDealId, OrderSummary::setFirstDealId);
            //aggregate mapPrimitive values
            orders.sum(Order::getSize, OrderSummary::setOrderSize);
            deals.count(OrderSummary::setDealCount);
            deals.set(Deal::getDealtSize, OrderSummary::setLastDealSize);
            deals.avg(Deal::getDealtSize, OrderSummary::setAvgDealSize);
            deals.sum(Deal::getDealtSize, OrderSummary::setVolumeDealt);
            orders.build().id("orderSummary");
        });
        //events
        sep.onEvent(new Order(2, "EURJPY", 100_000_000));
        sep.onEvent(new Deal(1001, 2, 4_000_000));
        sep.onEvent(new Order(1, "EURUSD", 2_000_000));
        sep.onEvent(new Deal(1002, 2, 17_000_000));
        sep.onEvent(new Deal(1003, 1, 1_500_000));
        sep.onEvent(new Deal(1004, 1, 100_000));
        //tests
        GroupBy<OrderSummary> summaryMap = getField("orderSummary");
        assertThat(summaryMap.size(), is(2));
        Optional<OrderSummary> euOrders = summaryMap.stream()
                .filter(summary -> summary.getCcyPair().equalsIgnoreCase("EURUSD"))
                .findFirst();
        assertThat(1_600_000, is((int) euOrders.get().getVolumeDealt()));
        assertThat(100_000, is((int) euOrders.get().getLastDealSize()));
        assertThat(800_000, is((int) euOrders.get().getAvgDealSize()));
        assertThat(2, is((int) euOrders.get().getDealCount()));
        assertThat(1003, is((int) euOrders.get().getFirstDealId()));
        assertThat(1, is((int) euOrders.get().getOrderId()));
        assertThat(2_000_000, is((int) euOrders.get().getOrderSize()));
        assertThat("EURUSD", is(euOrders.get().getCcyPair()));
    }

    @Test
//    @Ignore
    //TODO correct test
    public void singleArgStatefulFunction() {
        fixedPkg = true;
        sep(c -> {
            GroupBy<OrderSummary> orders = groupBy(Order::getId, OrderSummary.class)
                    .map(Order::getSize, OrderSummary::setDealCount, new StreamFunctions.Sum()::addInt)
                    .mapPrimitive(Order::getSize, OrderSummary::setVolumeDealt, cumSum())
                    .build().id("orderSummary");
        });
        onEvent(new Order(2, "EURJPY", 100_000_000));
        onEvent(new Order(2, "EURJPY", 100_000_000));
        onEvent(new Order(3, "EURJPY", 100_000_000));
        onEvent(new Order(3, "EURJPY", 100_000_000));
        onEvent(new Order(3, "EURJPY", 100_000_000));
        onEvent(new Order(1, "EURJPY", 100_000_000));

        GroupBy<OrderSummary> summaryMap = getField("orderSummary");
        assertThat(summaryMap.size(), is(3));

        OrderSummary value = summaryMap.value(2);
        assertThat(summaryMap.value(1).getDealCount(), is(100_000_000));
        assertThat(summaryMap.value(1).getVolumeDealt(), is(100_000_000d));
        assertThat(summaryMap.value(2).getDealCount(), is(200_000_000));
        assertThat(summaryMap.value(2).getVolumeDealt(), is(200_000_000d));
        assertThat(summaryMap.value(3).getDealCount(), is(300_000_000));
        assertThat(summaryMap.value(3).getVolumeDealt(), is(300_000_000d));
    }

    public static int randomCalc(int in) {
        return in;
    }

    public int randomCalc2(int in) {
        return in;
    }
}
