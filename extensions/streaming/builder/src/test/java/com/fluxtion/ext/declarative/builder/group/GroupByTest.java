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

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.declarative.builder.helpers.DealEvent;
import com.fluxtion.ext.declarative.builder.helpers.TradeEvent;
import com.fluxtion.ext.declarative.builder.helpers.TradeSummary;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.negative;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsBuilder.count;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsBuilder.cumSum;
import static com.fluxtion.ext.streaming.builder.group.Group.groupBy;
import com.fluxtion.ext.streaming.builder.group.GroupByBuilder;
import static com.fluxtion.ext.streaming.builder.stream.StreamOperatorService.stream;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class GroupByTest extends StreamInprocessTest {

    /**
     * Test an enriched data node
     */
    @Test
    public void testGroupByNonEvent() {
        sep((c) -> {
            EnrichedDeliveryItem enrichedDeliveryItem = c.addNode(new EnrichedDeliveryItem());
//            GroupByBuilder<EnrichedDeliveryItem, DeliverySummary> deliverySummary;
            GroupByBuilder<EnrichedDeliveryItem, DeliverySummary> deliverySummary = groupBy(enrichedDeliveryItem::getCustomerId, DeliverySummary.class);
            //init
            deliverySummary.init(EnrichedDeliveryItem::getCustomerId, DeliverySummary::setCustomerId);
            //calc
            deliverySummary.sum(EnrichedDeliveryItem::getValueInDollars, DeliverySummary::setValueInDollars);
            deliverySummary.sum(EnrichedDeliveryItem::getValueInLocalCcy, DeliverySummary::setValueInLocalCcy);
            deliverySummary.build().id("deliverySummary");
        });
        //events
        sep.onEvent(new FxRate("GBPUSD", 1.5));
        sep.onEvent(new FxRate("EURUSD", 1.2));
        sep.onEvent(new DeliveryItem("EU-xxxx-01", "9900787", 1000.0));
        sep.onEvent(new DeliveryItem("EU-xxxx-01", "9900787", 1000.0));
        sep.onEvent(new DeliveryItem("GB-ddf-45", "9900", 750.0));
        sep.onEvent(new DeliveryItem("GB-ddf-45", "9900", 750.0));
        sep.onEvent(new DeliveryItem("GB-ccw-67", "99e5400", 75440.0));
        sep.onEvent(new DeliveryItem("GB-ddf-45", "9900", 750.0));
        sep.onEvent(new DeliveryItem("EU-xxxx-01", "9900787", 6000.0));
        //tests
        GroupBy<DeliverySummary> summary = getField("deliverySummary");
        DeliverySummary euCustomer = summary.stream()
                .filter(delivery -> delivery.getCustomerId().equals("EU-xxxx-01"))
                .findFirst().get();

        DeliverySummary gb_ddfCustomer = summary.stream()
                .filter(delivery -> delivery.getCustomerId().equals("GB-ddf-45"))
                .findFirst().get();

        assertThat(summary.size(), is(3));
        assertThat(euCustomer.getValueInLocalCcy(), is(8000.0));
        assertThat(euCustomer.getValueInDollars(), is(9600.0));
        assertThat(gb_ddfCustomer.getValueInDollars(), is(3375.0));
    }

    @Test
    public void testGroupByFunction() {
        sep((c) -> {
            groupBy(TradeEvent::getTradeId, TradeSummary.class)
                    .mapPrimitive(TradeEvent::getTradeVolume, TradeSummary::setTotalVolume, cumSum())
                    .build()
                    .id("tradeSum");
        });

        sep.onEvent(new TradeEvent(14, 1000));
        sep.onEvent(new TradeEvent(14, 2000));
        sep.onEvent(new TradeEvent(2, 300));
        GroupBy<TradeSummary> summary = getField("tradeSum");
        assertThat(summary.value(14).getOutstandingVoulme(), is(3000));
        assertThat(summary.value(2).getOutstandingVoulme(), is(300));
    }

    @Test
    public void testGroupByRefFunction() {
        sep((c) -> {
            groupBy(TradeEvent::getTradeId, TradeSummary.class)
                    .map(TradeEvent::getTradeId, TradeSummary::setTraderIdString, GroupByTest::numberToString)
                    .mapPrimitive(TradeEvent::getTradeVolume, TradeSummary::setTotalVolume, cumSum())
                    .build()
                    .id("tradeSum");
        });

        sep.onEvent(new TradeEvent(14, 1000));
        sep.onEvent(new TradeEvent(14, 2000));
        sep.onEvent(new TradeEvent(2, 300));
        GroupBy<TradeSummary> summary = getField("tradeSum");
        assertThat(summary.value(14).getTraderIdString(), is("Number-val-" + 14));
        assertThat(summary.value(2).getTraderIdString(), is("Number-val-" + 2));
        assertThat(summary.value(14).getOutstandingVoulme(), is(3000));
        assertThat(summary.value(2).getOutstandingVoulme(), is(300));
    }

    public static String numberToString(int in, String oldVal) {
        return "Number-val-" + in;
    }

    @Test
    public void testGroupBy() {
        sep((c) -> {
            GroupByBuilder<TradeEvent, TradeSummary> trades = groupBy(TradeEvent::getTradeId, TradeSummary.class);
            GroupByBuilder<DealEvent, TradeSummary> deals = trades.join(DealEvent.class, DealEvent::getParentTradeId);
            //vars
            SerializableFunction<TradeEvent, ? extends Number> tradeVol = TradeEvent::getTradeVolume;
            SerializableFunction<DealEvent, ? extends Number> dealVol = DealEvent::getTradeVolume;
            //aggregate calcualtions
            trades.mapPrimitive(tradeVol, TradeSummary::setTotalVolume, GroupByTest::calcSum)
                    .mapPrimitive(tradeVol, TradeSummary::setAveragOrderSize, GroupByTest.AggregateAverage::calcAverage)
                    .mapPrimitive(tradeVol, TradeSummary::setTradeCount, GroupByTest::countLocal);
            deals.mapPrimitive(dealVol, TradeSummary::setTotalConfirmedVolume, GroupByTest::calcSum)
                    .mapPrimitive(dealVol, TradeSummary::setDealCount, GroupByTest::countLocal);
            stream(trades.build()::record)
                    .filter(TradeSummary::getOutstandingVoulme, negative())
                    .map(count()).id("badDealCount");

        });
        //events
        sep.onEvent(new TradeEvent(14, 1000));
        sep.onEvent(new TradeEvent(2, 300));
        sep.onEvent(new TradeEvent(2, 60));
        sep.onEvent(new DealEvent(2, 6));
        sep.onEvent(new TradeEvent(1, 100));
        sep.onEvent(new DealEvent(1, 25));
        sep.onEvent(new DealEvent(1, 25));
        sep.onEvent(new DealEvent(1, 25));
        sep.onEvent(new DealEvent(1, 25));
        sep.onEvent(new DealEvent(1, 25));
        sep.onEvent(new TradeEvent(9, 2780));
        //tests
        Number badDealCount = getWrappedField("badDealCount");
        assertThat(badDealCount.intValue(), is(1));
    }

    public static double calcSum(double newValue, double oldSum) {
        return newValue + oldSum;
    }

    public static double set(double newValue, double oldSum) {
        return newValue;
    }

    public static int countLocal(Object newValue, int oldValue) {
        oldValue++;
        return oldValue;
    }

    public static double minimum(double newValue, double oldValue) {
        return Math.min(newValue, oldValue);
    }

    public static double maximum(double newValue, double oldValue) {
        return Math.max(newValue, oldValue);
    }

    public static class AggregateAverage implements Stateful.StatefulNumber<AggregateAverage> {

        private int count;
        private double sum;
        private double currentValue;

        public double calcAverage(double newValue, double oldAverage) {
            count++;
            sum += newValue;
            currentValue = sum / count;
            return currentValue;
        }

        @Override
        public void reset() {
            count = 0;
            sum = 0;
            currentValue = Double.NaN;
        }

        @Override
        public Number combine(AggregateAverage other, MutableNumber result) {
            count += other.count;
            sum += other.sum;
            currentValue = sum / count;
            result.set(currentValue);
            return result;
        }

        @Override
        public Number deduct(AggregateAverage other, MutableNumber result) {
            count -= other.count;
            sum -= other.sum;
            currentValue = sum / count;
            result.set(currentValue);
            return result;
        }

        @Override
        public Number currentValue(MutableNumber result) {
            result.set(currentValue);
            return result;
        }
    }

    public static class AggregateSum implements Stateful.StatefulNumber<AggregateSum> {

        private double sum;

        public double calcCumSum(double newValue, double oldAverage) {
            sum += newValue;
            return sum;
        }

        @Override
        public void reset() {
            sum = 0;
        }

        @Override
        public Number combine(AggregateSum other, MutableNumber result) {
            sum += other.sum;
            result.set(sum);
            return result;
        }

        @Override
        public Number deduct(AggregateSum other, MutableNumber result) {
            sum -= other.sum;
            result.set(sum);
            return result;
        }

        @Override
        public Number currentValue(MutableNumber result) {
            result.set(sum);
            return result;
        }

    }

}
