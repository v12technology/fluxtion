/* 
 * Copyright (c) 2019, V12 Technology Ltd.
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
package com.fluxtion.generator.nopropagate;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.api.event.Event;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class NoPropagateTest extends BaseSepTest {

    @Test
    public void testPush() {
        buildAndInitSep(PriceBuilder.class);
        PricePublisher pricerPublisher = getField("pricerPublisher");
        PricerFormer priceFormer = getField("priceFormer");
        MarketHandler tickHandler = getField("marketHandler");
        //
        onEvent(new MarketTickEvent("EURUSD"));
        Assert.assertEquals(0, pricerPublisher.eventCount);
        Assert.assertEquals(1, priceFormer.eventCount);
        Assert.assertEquals(1, tickHandler.eventCount);
        //
        onEvent(new MarketTickEvent("EURUSD"));
        Assert.assertEquals(0, pricerPublisher.eventCount);
        Assert.assertEquals(2, priceFormer.eventCount);
        Assert.assertEquals(2, tickHandler.eventCount);
    }

    public static class PriceBuilder extends SEPConfig {

        @Override
        public void buildConfig() {
            MarketHandler tickHandler = addPublicNode(new MarketHandler(), "marketHandler");
            PricerFormer pricerFormer = addPublicNode(new PricerFormer(tickHandler), "priceFormer");
            PricePublisher pricerPublisher = addPublicNode(new PricePublisher(pricerFormer), "pricerPublisher");

        }

    }

    public static class MarketTickEvent implements Event {

        public String ccyPair;

        public MarketTickEvent(String ccyPair) {
            this.ccyPair = ccyPair;
        }

    }


    public static class MarketHandler {

        int eventCount;
        String ccyPair;

        @EventHandler
        public void newTick(MarketTickEvent tick) {
            eventCount++;
            ccyPair = tick.ccyPair;
        }
    }

    public static class PricerFormer {
        int eventCount;

        @NoEventReference
        public final MarketHandler marketHanlder;

        public PricerFormer(MarketHandler marketHanlder) {
            this.marketHanlder = marketHanlder;
        }
        
        @OnParentUpdate
        public void MarketUpdate(MarketHandler update){
            eventCount++;
        }

    }
    
    public static class PricePublisher{
        public final PricerFormer former;
        int eventCount;

        public PricePublisher(PricerFormer former) {
            this.former = former;
        }
        
        @OnEvent
        public void formPrice() {
            eventCount++;
        }
        
        
    }

}
