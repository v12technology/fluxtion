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
package com.fluxtion.extension.functional.group;

import com.fluxtion.extension.declarative.builder.group.GroupByBuilder;
import com.fluxtion.ext.declarative.api.group.GroupBy;
import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.api.EventWrapper;
import com.fluxtion.extension.functional.helpers.TradeEvent;
import com.fluxtion.extension.functional.helpers.DealEvent;
import com.fluxtion.extension.functional.helpers.TradeSummary;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.extension.functional.helpers.Tests.Negative;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.ext.declarative.api.Test;
import java.util.function.Function;
import static com.fluxtion.extension.functional.group.AggregateFunctions.Avg;
import static com.fluxtion.extension.functional.group.AggregateFunctions.Count;
import static com.fluxtion.extension.functional.group.AggregateFunctions.Sum;
import static com.fluxtion.extension.declarative.builder.event.EventSelect.select;
import static com.fluxtion.extension.declarative.builder.group.Group.groupBy;
import static com.fluxtion.extension.declarative.builder.log.LogBuilder.Log;
import static com.fluxtion.extension.declarative.builder.log.LogBuilder.LogOnNotify;
import static com.fluxtion.extension.declarative.builder.test.TestBuilder.buildTest;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Ignore;

/**
 *
 * @author Greg Higgins
 */
public class GroupByTest extends BaseSepTest {
 
    /**
     * Test an enriched data node
     */
    @org.junit.Test
    public void testGroupByNonEvent() {
        EventHandler sep = buildAndInitSep(BuilderEnriched.class);
        sep.onEvent(new FxRate("GBPUSD", 1.5));
        sep.onEvent(new FxRate("EURUSD", 1.2));
        sep.onEvent(new DeliveryItem("EU-xxxx-01", "9900787", 1000.0));
        sep.onEvent(new DeliveryItem("EU-xxxx-01", "9900787", 1000.0));
        sep.onEvent(new DeliveryItem("GB-ddf-45", "9900", 750.0));
        sep.onEvent(new DeliveryItem("GB-ddf-45", "9900", 750.0));
        sep.onEvent(new DeliveryItem("GB-ccw-67", "99e5400", 75440.0));
        sep.onEvent(new DeliveryItem("GB-ddf-45", "9900", 750.0));
        sep.onEvent(new DeliveryItem("EU-xxxx-01", "9900787", 6000.0));
        //
        GroupBy<DeliverySummary> summary = getField("deliverySummary");
        DeliverySummary euCustomer = summary.getMap().values().stream()
                .map(wrapper -> wrapper.event())
                .filter(sum -> sum.getCustomerId().equals("EU-xxxx-01"))
                .findFirst().get();    
        
        DeliverySummary gb_ddfCustomer = summary.getMap().values().stream()
                .map(wrapper -> wrapper.event())
                .filter(sum -> sum.getCustomerId().equals("GB-ddf-45"))
                .findFirst().get();    
        
        assertThat(3, is(summary.getMap().values().size()));
        assertThat(8000, is((int)euCustomer.getValueInLocalCcy()));
        assertThat(9600, is((int)euCustomer.getValueInDollars()));
        assertThat(3375, is((int)gb_ddfCustomer.getValueInDollars()));
    }
    
    
    @org.junit.Test
    @Ignore
    public void testGroupBy() {
        EventHandler sep = buildAndInitSep(Builder1.class);
        
        sep.onEvent(new TradeEvent(14, 1000));
        sep.onEvent(new TradeEvent(2, 300));
        sep.onEvent(new TradeEvent(2, 60));
//        sep.onEvent(LogControlEvent.);
        sep.onEvent(new DealEvent(2, 6));
        sep.onEvent(new DealEvent(1, 25));
        sep.onEvent(new DealEvent(1, 25));
        sep.onEvent(new DealEvent(1, 25));
        sep.onEvent(new DealEvent(1, 25));
        sep.onEvent(new DealEvent(1, 25));
        sep.onEvent(new TradeEvent(9, 2780));
        //TODO use assert to test the calculations.
    }

    public static class Builder1 extends SEPConfig {

        {
            GroupByBuilder<TradeEvent, TradeSummary> trades = groupBy(TradeEvent.class, TradeEvent::getTradeId, TradeSummary.class);
            GroupByBuilder<DealEvent, TradeSummary> deals = trades.join(DealEvent.class, DealEvent::getParentTradeId);
            //vars
            Function<TradeEvent, ? super Number> tradeVol = TradeEvent::getTradeVolume;
            Function<DealEvent, ? super Number> dealVol = DealEvent::getTradeVolume;
            //aggregate calcualtions
            trades.function(Sum, tradeVol, TradeSummary::setTotalVolume);
            trades.function(Avg, tradeVol, TradeSummary::setAveragOrderSize);
            trades.function(Count, tradeVol, TradeSummary::setTradeCount);
            deals.function(Sum, dealVol, TradeSummary::setTotalConfirmedVolume);
            deals.function(Count, dealVol, TradeSummary::setDealCount);
            Wrapper<TradeSummary> summary = trades.build();

            //debug logging
            final EventWrapper<DealEvent> dealEvents = select(DealEvent.class);
            Log(" -> trade  : {}", select(TradeEvent.class)).logLevel = 4;
            Log(" -> deal  : {}", dealEvents).logLevel = 4;
            Log(" <- summary: {}", summary).logLevel = 4;
            //warning log for unconfirmed trades
            Test unconfirmedDeal = buildTest(
                    Negative.class, summary, TradeSummary::getOutstandingVoulme)
                    .notifyOnChange(true).build();

            LogOnNotify("<- WARNING deal volume greater than order volume dealId:",
                    unconfirmedDeal, dealEvents, DealEvent::getParentTradeId
            ).logLevel = 2;
        }
    }

    public static class BuilderEnriched extends SEPConfig {{
            EnrichedDeliveryItem enrichedDeliveryItem = addNode(new EnrichedDeliveryItem());
            GroupByBuilder<EnrichedDeliveryItem, DeliverySummary> deliverySummary;
            deliverySummary = groupBy(enrichedDeliveryItem, EnrichedDeliveryItem::getCustomerId, DeliverySummary.class);
            //init
            deliverySummary.init(EnrichedDeliveryItem::getCustomerId, DeliverySummary::setCustomerId);
            //calc
            deliverySummary.sum(EnrichedDeliveryItem::getValueInDollars, DeliverySummary::setValueInDollars);
            deliverySummary.sum(EnrichedDeliveryItem::getValueInLocalCcy, DeliverySummary::setValueInLocalCcy);
            GroupBy<DeliverySummary> summary = deliverySummary.build();
            addPublicNode(summary, "deliverySummary");
//            Log(summary);
            //logging, ordered by using logNotify referring to parent
//            MsgBuilder firstLog = Log("<- {}", DeliveryItem.class);
//            MsgBuilder secondLog = LogOnNotify("<- {}", firstLog, enrichedDeliveryItem, enrichedDeliveryItem::toString);
//            LogOnNotify("-> {}", secondLog, summary, DeliverySummary::toString);
//            //log levels
//            firstLog.logLevel = 6;
//            secondLog.logLevel = 6;
    }}

}
