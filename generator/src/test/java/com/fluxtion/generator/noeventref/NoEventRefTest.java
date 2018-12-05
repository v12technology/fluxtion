/* 
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.generator.noeventref;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.runtime.event.Event;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class NoEventRefTest extends BaseSepTest {

    @Test
    public void dirtyNoReferenceTest() {
        buildAndInitSep(NoEventrefBuilder.class);
        PricePublisher testHandler = getField("pricePublisher");
        RulesProcessor rulesProcessor = getField("rulesProcessor");
        PriceFormer priceFormer = getField("priceFormer");
        onEvent(new Config());
        onEvent(new Config());
        Assert.assertEquals(0, testHandler.invokeCount);
        Assert.assertEquals(2, rulesProcessor.invokeCount);
        Assert.assertEquals(2, priceFormer.invokeCount);
    }

    public static class NoEventrefBuilder extends SEPConfig {

        @Override
        public void buildConfig() {
            ConfigCache cfgCache = addNode(new ConfigCache());
            PriceFormer priceFormer = addPublicNode(new PriceFormer(cfgCache), "priceFormer");
            RulesProcessor rulesProcessor = addPublicNode(new RulesProcessor(cfgCache), "rulesProcessor");
            addPublicNode(new PricePublisher(priceFormer, rulesProcessor), "pricePublisher");
        }

    }

    public static class Config extends Event {
    }

    public static class Price extends Event {
    }

    public static class ConfigCache {

        @EventHandler
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

        @OnEvent
        public boolean onEvent() {
            invokeCount++;
            return true;
        }

        @EventHandler
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

        @OnEvent
        public boolean onEvent() {
            invokeCount++;
            return false;
        }
    }

    public static class PricePublisher {

        @NoEventReference
        public final PriceFormer priceFormer;

        public final RulesProcessor rulesProcessor;
        public int invokeCount;

        public PricePublisher(PriceFormer priceFormer, RulesProcessor rulesProcessor) {
            this.priceFormer = priceFormer;
            this.rulesProcessor = rulesProcessor;
        }

        @OnEvent
        public void onEvent() {
            invokeCount++;
        }
    }

}
