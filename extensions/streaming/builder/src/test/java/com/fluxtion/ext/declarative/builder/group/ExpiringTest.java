package com.fluxtion.ext.declarative.builder.group;

import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import static com.fluxtion.ext.streaming.builder.group.Group.groupBy;
import com.fluxtion.ext.streaming.builder.group.GroupByBuilder;
import com.fluxtion.junit.Categories;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author gregp
 */
public class ExpiringTest extends StreamInprocessTest {

//    @Override
//    protected String testPackageID() {
//        return "";
//    }

    @Test
    @Category(Categories.FilterTest.class)
    public void test() {
        sep(c ->{
            GroupByBuilder<Order, OrderSummary> orders = groupBy(Order.class, Order::getId, OrderSummary.class);
            GroupByBuilder<Deal, OrderSummary> deals = orders.join(Deal.class, Deal::getOrderId);
            //set default vaules for a group by row
            orders.init(Order::getCcyPair, OrderSummary::setCcyPair);
            orders.init(Order::getId, OrderSummary::setOrderId);
            //set last deal id
            deals.init(Deal::getDealId, OrderSummary::setFirstDealId);
            //aggregate function values
            orders.sum(Order::getSize, OrderSummary::setOrderSize);
            deals.count(OrderSummary::setDealCount);
            deals.set(Deal::getDealtSize, OrderSummary::setLastDealSize);
            deals.avg(Deal::getDealtSize, OrderSummary::setAvgDealSize);
            deals.sum(Deal::getDealtSize, OrderSummary::setVolumeDealt);
            orders.build().id("orderSummary");
        });
        GroupBy<OrderSummary> summaryMap = getField("orderSummary");
//        

//        sep.onEvent(new Order(2, "EURJPY", 100_000_000));
//        sep.onEvent(new Deal(1001, 2, 4_000_000));
//        sep.onEvent(new Deal(1002, 2, 17_000_000));
//        sep.onEvent(new Deal(1003, 1, 1_500_000));
//        sep.onEvent(new Order(1, "EURUSD", 2_000_000));
//        sep.onEvent(new Deal(1004, 1, 100_000));
//        
//        assertThat(summaryMap.getMap().size(), is(2));
//        Optional<OrderSummary> euOrders = summaryMap.getMap().values().stream()
//                .map(wrapper -> wrapper.event())
//                .filter(summary -> summary.getCcyPair().equalsIgnoreCase("EURUSD"))
//                .findFirst();
//        assertThat(1_600_000, is((int)euOrders.get().getVolumeDealt()));
//        assertThat(100_000, is((int)euOrders.get().getLastDealSize()));
//        assertThat(800_000, is((int)euOrders.get().getAvgDealSize()));
//        assertThat(2, is((int)euOrders.get().getDealCount()));
//        assertThat(1003, is((int)euOrders.get().getFirstDealId()));
//        assertThat(1, is((int)euOrders.get().getOrderId()));
//        assertThat(2_000_000, is((int)euOrders.get().getOrderSize()));
//        assertThat("EURUSD", is(euOrders.get().getCcyPair()));
//        System.out.println(euOrders);
    }

}
