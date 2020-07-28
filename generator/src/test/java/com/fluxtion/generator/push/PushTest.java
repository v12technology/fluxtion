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
package com.fluxtion.generator.push;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.PushReference;
import com.fluxtion.api.event.Event;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class PushTest extends BaseSepInprocessTest {

    @Test
    public void testPush() {
        sep(cfg -> {
            MarketHandler tickHandler = cfg.addPublicNode(new MarketHandler(), "marketHandler");
            PricerFormer pricerFormer = cfg.addPublicNode(new PricerFormer(), "priceFormer");
            Push push = cfg.addPublicNode(new Push(), "pusher");
            push.marketHanlder = tickHandler;
            push.priceFormer = pricerFormer;
        });
        
        PricerFormer priceFormer = getField("priceFormer");
        MarketHandler tickHandler = getField("marketHandler");
        //
        Assert.assertEquals(0, priceFormer.eventCount);
        //
        onEvent(new MarketTickEvent("EURUSD"));
        Assert.assertEquals(1, priceFormer.eventCount);
        onEvent(new MarketTickEvent("EURUSD"));
        Assert.assertEquals(2, priceFormer.eventCount);
        onEvent(new MarketTickEvent("USDJPY"));
        Assert.assertEquals(2, priceFormer.eventCount);
    }
    
    
      @Test
    public void testPushCollection() {
        sep(cfg -> {
            MarketHandler tickHandler = cfg.addPublicNode(new MarketHandler(), "marketHandler");
            PricerFormer pricerFormer1 = cfg.addPublicNode(new PricerFormer(), "priceFormer1");
            PricerFormer pricerFormer2 = cfg.addPublicNode(new PricerFormer(), "priceFormer2");
            PushCollection push = cfg.addPublicNode(new PushCollection(), "pusher");
            push.marketHanlder = tickHandler;
            push.priceFormer = Arrays.asList(pricerFormer1, pricerFormer2);
        });
        
        
        PricerFormer priceFormer1 = getField("priceFormer1");
        PricerFormer priceFormer2 = getField("priceFormer2");
        MarketHandler tickHandler = getField("marketHandler");
        //
        Assert.assertEquals(0, priceFormer1.eventCount);
        //
        onEvent(new MarketTickEvent("EURUSD"));
        Assert.assertEquals(1, priceFormer1.eventCount);
        Assert.assertEquals(1, priceFormer2.eventCount);
        onEvent(new MarketTickEvent("EURUSD"));
        Assert.assertEquals(2, priceFormer1.eventCount);
        Assert.assertEquals(2, priceFormer2.eventCount);
        onEvent(new MarketTickEvent("USDJPY"));
        Assert.assertEquals(2, priceFormer1.eventCount);
        Assert.assertEquals(2, priceFormer2.eventCount);
    }  
    

    public static class MarketTickEvent implements Event {

        public String ccyPair;

        public MarketTickEvent(String ccyPair) {
            this.ccyPair = ccyPair;
        }

    }

    public static class Push {

        @PushReference
        public PricerFormer priceFormer;

        public MarketHandler marketHanlder;

        @OnEvent
        public boolean pushData() {
            if (marketHanlder.ccyPair.equals("EURUSD")) {
                return true;
            }
            return false;
        }
    }

    public static class PushCollection {

        @PushReference
        public List<PricerFormer> priceFormer = new ArrayList<>();

        public MarketHandler marketHanlder;

        @OnEvent
        public boolean pushData() {
            if (marketHanlder.ccyPair.equals("EURUSD")) {
                return true;
            }
            return false;
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

        public MarketHandler marketHanlder;
        int eventCount;

        @OnEvent
        public void formPrice() {
            eventCount++;
        }

    }

}
