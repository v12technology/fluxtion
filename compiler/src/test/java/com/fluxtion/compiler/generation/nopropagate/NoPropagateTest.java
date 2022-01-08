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
package com.fluxtion.compiler.generation.nopropagate;

import com.fluxtion.runtime.annotations.EventHandler;
import com.fluxtion.runtime.annotations.NoEventReference;
import com.fluxtion.runtime.annotations.OnEvent;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class NoPropagateTest extends MultipleSepTargetInProcessTest {

    public NoPropagateTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testPush() {
        sep((c) ->{
            MarketHandler tickHandler = c.addPublicNode(new MarketHandler(), "marketHandler");
            PricerFormer pricerFormer = c.addPublicNode(new PricerFormer(tickHandler), "priceFormer");
            PricePublisher pricerPublisher = c.addPublicNode(new PricePublisher(pricerFormer), "pricerPublisher");
        });
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
