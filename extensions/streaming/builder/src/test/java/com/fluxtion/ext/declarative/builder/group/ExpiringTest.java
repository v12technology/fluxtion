package com.fluxtion.ext.declarative.builder.group;

import com.fluxtion.ext.declarative.builder.stream.StreamInProcessTest;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import static com.fluxtion.ext.streaming.builder.group.Group.groupBy;
import com.fluxtion.junit.Categories;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author gregp
 */
public class ExpiringTest extends StreamInProcessTest {

//    @Override
//    protected String testPackageID() {
//        return "";
//    }
    @Test
    @Category(Categories.FilterTest.class)
    public void test() {
        sep(c -> {
            //set default vaules for a group by row
            groupBy(Order::getId, OrderSummary.class)
                .init(Order::getCcyPair, OrderSummary::setCcyPair)
                .init(Order::getId, OrderSummary::setOrderId)
                .sum(Order::getSize, OrderSummary::setOrderSize)
                .join(Deal.class, Deal::getOrderId)
                .init(Deal::getDealId, OrderSummary::setFirstDealId)
                .count(OrderSummary::setDealCount)
                .set(Deal::getDealtSize, OrderSummary::setLastDealSize)
                .avg(Deal::getDealtSize, OrderSummary::setAvgDealSize)
                .sum(Deal::getDealtSize, OrderSummary::setVolumeDealt)
                .build().id("orderSummary");
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
//                .map(wrapper -> wrapper.record())
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
