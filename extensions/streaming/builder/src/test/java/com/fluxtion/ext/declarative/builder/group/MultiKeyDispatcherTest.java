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

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.Lifecycle;
import static com.fluxtion.ext.declarative.builder.group.Deal.DEAL;
import static com.fluxtion.ext.declarative.builder.group.TraderPosition.TRADER_POSITION;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.group.MultiKeyDispatcher;
import static com.fluxtion.ext.streaming.builder.group.Group.groupBy;
import com.fluxtion.ext.streaming.builder.group.GroupByBuilder;
import net.vidageek.mirror.dsl.Mirror;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class MultiKeyDispatcherTest extends StreamInprocessTest {

    private Deal eu_dave;
    private Deal eu_gustav;

    private Deal eu_john;
    private Deal eu_linda;

    @Test
    public void validateDispatch() throws Exception {
        sep(c -> {
            GroupByBuilder<Deal, TraderPosition> traderPos
                    = groupBy(DEAL, TRADER_POSITION, Deal::getCcyPair, Deal::getTraderId);
            traderPos.init(Deal::getTraderName, TraderPosition::setName);
            traderPos.init(Deal::getCcyPair, TraderPosition::setCcyPair);
            traderPos.sum(Deal::getDealtSize, TraderPosition::setDealtVolume);
            traderPos.sum(Deal::getContraSize, TraderPosition::setContraVolume);
            traderPos.build().id("traderPositions");
        });

        //setup two seps one shared key eu_john
        sep.onEvent(eu_john);
        sep.onEvent(eu_john);
        sep.onEvent(eu_dave);
        GroupBy<TraderPosition> primaryGroup = getField("traderPositions");
        TraderPosition primaryPositionJohn = primaryGroup.value(eu_john);
        TraderPosition primaryPositionDave = primaryGroup.value(eu_dave);
        
        assertThat(primaryPositionJohn.getDealtVolume(), is(400_000d));
        assertThat(primaryPositionDave.getDealtVolume(), is(20_000d));
        
        StaticEventProcessor secondary = sep.getClass().newInstance();
        ((Lifecycle)secondary).init();
        secondary.onEvent(eu_john);
        secondary.onEvent(eu_linda);
        GroupBy<TraderPosition> secondaryyGroup = (GroupBy<TraderPosition>) new Mirror().on(secondary).get().field("traderPositions");
        TraderPosition secondaryPositionJohn = secondaryyGroup.value(eu_john);
        TraderPosition secondaryPositionLinda = secondaryyGroup.value(eu_linda);
        
        assertThat(secondaryPositionJohn.getDealtVolume(), is(200_000d));
        assertThat(secondaryPositionLinda.getDealtVolume(), is(1_000d));
        
        
        MultiKeyDispatcher dispatcher = new MultiKeyDispatcher((t) -> {
            return (GroupBy<TraderPosition>)new Mirror().on(secondary).get().field("traderPositions");
        });
        dispatcher.addToIndex(sep);
        dispatcher.addToIndex(secondary);
        dispatcher.index();
        dispatcher.onEvent(eu_john);
        dispatcher.onEvent(eu_linda);
        
        assertThat(primaryPositionJohn.getDealtVolume(), is(600_000d));
        assertThat(primaryPositionDave.getDealtVolume(), is(20_000d));
        assertThat(secondaryPositionJohn.getDealtVolume(), is(400_000d));
        assertThat(secondaryPositionLinda.getDealtVolume(), is(2_000d));
    }
    
    
    @Before
    public void setup(){
        eu_john = new Deal();
        eu_john.traderId = 1;
        eu_john.traderName = "John";
        eu_john.setCcyPair("EURUSD");
        eu_john.setOrderId(1001);
        eu_john.setDealtSize(200_000);
        eu_john.setContraSize(-224_000);
        eu_john.setDealId(909);
        
        eu_dave = new Deal();
        eu_dave.traderId = 2;
        eu_dave.traderName = "Dave";
        eu_dave.setCcyPair("EURUSD");
        eu_dave.setOrderId(1001);
        eu_dave.setDealtSize(20_000);
        eu_dave.setContraSize(-22_000);
        eu_dave.setDealId(909);
        
        eu_linda = new Deal();
        eu_linda.traderId = 3;
        eu_linda.traderName = "Linda";
        eu_linda.setCcyPair("EURUSD");
        eu_linda.setOrderId(1001);
        eu_linda.setDealtSize(1_000);
        eu_linda.setContraSize(-2_000);
        eu_linda.setDealId(909);
        
        eu_gustav = new Deal();
        eu_gustav.traderId = 4;
        eu_gustav.traderName = "Gustav";
        eu_gustav.setCcyPair("EURUSD");
        eu_gustav.setOrderId(1001);
        eu_gustav.setDealtSize(5_000);
        eu_gustav.setContraSize(-6_000);
        eu_gustav.setDealId(909);
    }

}
