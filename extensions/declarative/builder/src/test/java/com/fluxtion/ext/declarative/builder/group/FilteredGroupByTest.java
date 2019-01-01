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

import com.fluxtion.ext.declarative.builder.group.GroupByBuilder;
import com.fluxtion.ext.declarative.api.group.GroupBy;
import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.ext.declarative.api.Wrapper;
import static com.fluxtion.ext.declarative.builder.group.Group.groupBy;
import com.fluxtion.generator.util.BaseSepTest;
import static com.fluxtion.ext.declarative.builder.test.FilterHelper.filter;
import com.fluxtion.junit.Categories;
import com.fluxtion.runtime.lifecycle.EventHandler;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 *
 * @author Greg Higgins
 */
public class FilteredGroupByTest extends BaseSepTest {

    @Test
    @Category(Categories.FilterTest.class)
    public void testFilteredGroupBy() {
        EventHandler sep = buildAndInitSep(Builder.class);
        GroupBy<OrderSummary> summaryMap = getField("orderSummary");
        
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
    }

    @Test
    @Category(Categories.FilterTest.class)
    public void test() {
        EventHandler sep = buildAndInitSep(Builder1.class);
        
        GroupBy<OrderSummary> summaryMap = getField("orderSummary");
        
        sep.onEvent(new Order(1, "EURUSD", 2_000_000));
        sep.onEvent(new Order(2, "EURJPY", 100_000_000));
        sep.onEvent(new Deal(1001, 2, 4_000_000));
        sep.onEvent(new Deal(1002, 2, 17_000_000));
        sep.onEvent(new Deal(1003, 1, 1_500_000));
        sep.onEvent(new Deal(13, 1, -23));
        sep.onEvent(new Deal(14, 1, 0));
        sep.onEvent(new Deal(14, 1, -50_000_000));
        sep.onEvent(new Deal(1004, 1, 100_000));
        
        assertThat(summaryMap.getMap().size(), is(2));
        Optional<OrderSummary> euOrders = summaryMap.getMap().values().stream()
                .map(wrapper -> wrapper.event())
                .filter(summary -> summary.getCcyPair().equalsIgnoreCase("EURUSD"))
                .findFirst();
        assertThat(1_600_000, is((int)euOrders.get().getVolumeDealt()));
        assertThat(800_000, is((int)euOrders.get().getAvgDealSize()));
        assertThat(2, is((int)euOrders.get().getDealCount()));
        assertThat(1, is((int)euOrders.get().getOrderId()));
        assertThat(2_000_000, is((int)euOrders.get().getOrderSize()));
        assertThat("EURUSD", is(euOrders.get().getCcyPair()));
        System.out.println(euOrders);
    }
    
    
    public static class GreaterThan implements com.fluxtion.ext.declarative.api.Test {

        public boolean greaterThan(double op1, double op2) {
            return op1 > op2;
        }
    }

    public static class Builder extends SEPConfig {

        {
            Wrapper<Order> largeOrders = filter(Order.class, Order::getSize, GreaterThan.class, 200);
            GroupByBuilder<Order, OrderSummary> largeOrdersByCcy = groupBy(largeOrders, Order::getCcyPair, OrderSummary.class);
            largeOrdersByCcy.init(Order::getCcyPair, OrderSummary::setCcyPair);
            largeOrdersByCcy.count( OrderSummary::setDealCount);
            largeOrdersByCcy.sum(Order::getSize, OrderSummary::setOrderSize);
            addPublicNode(largeOrdersByCcy.build(), "orderSummary");
        }
    }
    
    public static class Builder1 extends SEPConfig {

        {
            
            Wrapper<Deal> validDeal = filter(Deal.class, Deal::getDealtSize, GreaterThan.class, 0);
            
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
            GroupBy<OrderSummary> orderSummary = orders.build();
            //add public node for testing
            addPublicNode(orderSummary, "orderSummary");
            //logging
//            Log(DEAL);
//            Log(ORDER);
//            Log(orderSummary);
        }
    }
    
    
    
}
