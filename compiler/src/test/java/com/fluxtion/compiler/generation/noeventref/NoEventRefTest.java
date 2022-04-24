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
package com.fluxtion.compiler.generation.noeventref;

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class NoEventRefTest extends MultipleSepTargetInProcessTest {

    public NoEventRefTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void dirtyNoReferenceTest() {
        sep((c)->{
            ConfigCache cfgCache = c.addNode(new ConfigCache());
            PriceFormer priceFormer = c.addPublicNode(new PriceFormer(cfgCache), "priceFormer");
            RulesProcessor rulesProcessor = c.addPublicNode(new RulesProcessor(cfgCache), "rulesProcessor");
            c.addPublicNode(new PricePublisher(priceFormer, rulesProcessor), "pricePublisher");
        });
        PricePublisher testHandler = getField("pricePublisher");
        RulesProcessor rulesProcessor = getField("rulesProcessor");
        PriceFormer priceFormer = getField("priceFormer");
        onEvent(new Config());
        onEvent(new Config());
        Assert.assertEquals(0, testHandler.invokeCount);
        Assert.assertEquals(2, rulesProcessor.invokeCount);
        Assert.assertEquals(2, priceFormer.invokeCount);
    }

    public static class Config implements Event {
    }

    public static class Price implements Event {
    }

    public static class ConfigCache {

        @OnEventHandler
        public boolean configUpdate(Config cfg) {
            return true;
        }

    }

    public static class PriceFormer {

        public final ConfigCache configCache;
        private int invokeCount;

        public PriceFormer(ConfigCache configCache) {
            this.configCache = configCache;
        }

        @OnTrigger
        public boolean onEvent() {
            invokeCount++;
            return true;
        }

        @OnEventHandler
        public boolean priceUpdate(Price cfg) {
            return true;
        }

    }

    public static class RulesProcessor {

        public final ConfigCache configCache;
        private int invokeCount;

        public RulesProcessor(ConfigCache configCache) {
            this.configCache = configCache;
        }

        @OnTrigger
        public boolean onEvent() {
            invokeCount++;
            return false;
        }
    }

    public static class PricePublisher {

        @NoTriggerReference
        public final PriceFormer priceFormer;

        public final RulesProcessor rulesProcessor;
        public int invokeCount;

        public PricePublisher(PriceFormer priceFormer, RulesProcessor rulesProcessor) {
            this.priceFormer = priceFormer;
            this.rulesProcessor = rulesProcessor;
        }

        @OnTrigger
        public void onEvent() {
            invokeCount++;
        }
    }

}
