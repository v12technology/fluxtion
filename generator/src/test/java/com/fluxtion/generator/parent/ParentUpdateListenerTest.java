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
package com.fluxtion.generator.parent;

import com.fluxtion.api.annotations.*;
import com.fluxtion.api.event.DefaultEvent;
import com.fluxtion.api.event.Event;
import com.fluxtion.generator.util.BaseSepInProcessTest;
import com.fluxtion.generator.util.MultipleSepTargetInProcessTest;
import lombok.Value;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class ParentUpdateListenerTest extends BaseSepInProcessTest {

    @Test
    public void testClassFilter() {
        sep(cfg -> {
            TestHandler handler = cfg.addPublicNode(new TestHandler(), "handler");
            cfg.addPublicNode(new TestChild(), "child").parent = handler;
        });

        TestHandler testHandler = getField("handler");
        TestChild child = getField("child");
        onEvent(new ClassFilterEvent(String.class));
        onEvent(new ClassFilterEvent(Double.class));
        assertThat(testHandler.count, is(1));
        assertThat(child.eventCount, is(1));
        assertThat(child.parentCount, is(1));
        onEvent(new NoUpdateEvent());
        assertThat(testHandler.count, is(2));
        assertThat(child.eventCount, is(1));
        assertThat(child.parentCount, is(1));
    }

    @Test
    public void testClassFilterPrivateParent() {
        sep(cfg -> {
            TestHandler handler = cfg.addPublicNode(new TestHandler(), "handler");
            cfg.addPublicNode(new TestChildPrivateParent(handler), "child");
        });

        TestHandler testHandler = getField("handler");
        TestChildPrivateParent child = getField("child");
        onEvent(new ClassFilterEvent(String.class));
        onEvent(new ClassFilterEvent(Double.class));
        assertThat(testHandler.count, is(1));
        assertThat(child.eventCount, is(1));
        assertThat(child.parentCount, is(1));
        onEvent(new NoUpdateEvent());
        assertThat(testHandler.count, is(2));
        assertThat(child.eventCount, is(1));
        assertThat(child.parentCount, is(1));
    }

    @Test
    public void testMultipleRules() {
        sep(cfg -> {
            RuleValidator validator = cfg.addPublicNode(new RuleValidator(), "validator");
            OrderCache cache = cfg.addNode(new OrderCache());
            validator.rules.add(cfg.addPublicNode(new Rule1(cache), "rule1"));
            validator.rules.add(cfg.addPublicNode(new Rule2(cache), "rule2"));
        });
        RuleValidator validator = getField("validator");
        Rule2 rule2 = getField("rule2");
        onEvent(new NewOrderEvent());
        assertThat(validator.validationFailedCount, is(1));
        assertThat(validator.ruleCount, is(2));
        assertThat(rule2.configCount, is(0));
        onEvent(new ConfigEvent());
        assertThat(validator.validationFailedCount, is(1));
        assertThat(validator.ruleCount, is(2));
        assertThat(rule2.configCount, is(1));
    }

    @Test
    public void dirtyFiltering() {
        sep(cfg -> {
            MarketHandler tickHandler = cfg.addPublicNode(new MarketHandler(), "marketHandler");
            PricerFormer pricerFormer = cfg.addPublicNode(new PricerFormer(), "priceFormer");
            TickCounter tickCounter = cfg.addPublicNode(new TickCounter(), "tickCounter");
            ThrottledPublisher throttledPublisher = cfg.addPublicNode(new ThrottledPublisher(), "throttledPublisher");
            PositionCalculator positionCalc = cfg.addPublicNode(new PositionCalculator(), "positionCalc");
            pricerFormer.marketHandler = tickHandler;
            tickCounter.marketHandler = tickHandler;
            throttledPublisher.pricerFormer = pricerFormer;
            throttledPublisher.positionCalc = positionCalc;
        });

        TickCounter tickCounter = getField("tickCounter");
        PricerFormer priceFormer = getField("priceFormer");
        assertThat(tickCounter.eventCount, is(0));
        assertThat(tickCounter.parentCount, is(0));
        assertThat(priceFormer.eventCount, is(0));
        assertThat(priceFormer.parentCount, is(0));
        onEvent(new MarketTickEvent());
        onEvent(new MarketTickEvent());
        assertThat(tickCounter.eventCount, is(0));
        assertThat(tickCounter.parentCount, is(2));
        assertThat(priceFormer.eventCount, is(0));
        assertThat(priceFormer.parentCount, is(0));

    }
    
    @Test
    public void noEventGuardedParent() {
        String matchKey = "match_me";
        String matchKey2 = "match_2";
        sep(cfg -> {
            cfg.addPublicNode(new NoEventHandler(new FilterHandler(matchKey), new FilterHandler(matchKey2)), "test");
        }, "com.test.noEventGuardedParent.GuardForNoEventReference");
        NoEventHandler handler = getField("test");
//      
        onEvent(matchKey2);
        assertTrue(handler.parent2Updated);
        assertTrue(handler.onEvent);
        assertFalse(handler.parentUpdated);
//        
        handler.reset();
        onEvent(matchKey);
        assertTrue(handler.parentUpdated);
        assertFalse(handler.parent2Updated);
        assertFalse(handler.onEvent);
//        
        handler.reset();
        onEvent("hello");
        assertFalse(handler.parentUpdated);
        assertFalse(handler.parent2Updated);
        assertFalse(handler.onEvent);
    }

    public static class ClassFilterEvent extends DefaultEvent {

        public ClassFilterEvent(Class clazz) {
            filterString = clazz.getCanonicalName();
        }
    }

    @Value
    public static class FilterHandler {

        String filter;

        @EventHandler
        public boolean checkString(String s) {
            return filter.equalsIgnoreCase(s);
        }
    }
    
    
    public static class NoEventHandler{
        @NoEventReference @SepNode
        final FilterHandler handler;
        @SepNode
        final FilterHandler handler2;
        transient boolean parentUpdated;
        transient boolean parent2Updated;
        transient boolean onEvent;

        public NoEventHandler(FilterHandler handler) {
            this(handler, null);
        }
        
        public NoEventHandler(FilterHandler handler, FilterHandler handler2) {
            this.handler = handler;
            this.handler2 = handler2;
            reset();
        }

        @OnParentUpdate(value = "handler", guarded = true)
        public void handlerUpdated(FilterHandler handler){
            parentUpdated = true;
        }

        @OnParentUpdate(value = "handler2", guarded = true)
        public void handler2Updated(FilterHandler handler){
            parent2Updated = true;
        }
        
        @OnEvent
        public void onEvent(){
            onEvent = true;
        }
        
        public void reset(){
            parentUpdated = false;
            parent2Updated = false;
            onEvent = false;
        }
        
    }

    public static class NoUpdateEvent implements Event {
    }

    public static class TestHandler {

        public int count = 0;

        @EventHandler(filterStringFromClass = String.class)
        public void handleEvent(ClassFilterEvent event) {
            count++;
        }

        @EventHandler(propagate = false)
        public void noParentPropagation(NoUpdateEvent event) {
            count++;
        }

    }

    public static class TestChild {

        public int parentCount;
        public int eventCount;

        public TestHandler parent;

        @OnEvent
        public void onEvent() {
            eventCount++;
        }

        @OnParentUpdate
        public void parentUpdated(TestHandler handler) {
            parentCount++;
        }
    }

    public static class TestChildPrivateParent {

        public int parentCount;
        public int eventCount;

        private final TestHandler parent;

        public TestChildPrivateParent(TestHandler parent) {
            this.parent = parent;
        }

        @OnEvent
        public void onEvent() {
            eventCount++;
        }

        @OnParentUpdate
        public void parentUpdated(TestHandler handler) {
            parentCount++;
        }
    }

    public static class NoParentUpdate {
    }

    public static final class ConfigEvent implements Event {
    }

    public static final class NewOrderEvent implements Event {
    }

    public static class OrderCache {

        @EventHandler
        public boolean onNewOrder(NewOrderEvent event) {
            return true;
        }
    }

    public static class DefaultRule {

        public OrderCache cache;

        public DefaultRule(OrderCache cache) {
            this.cache = cache;
        }

        public DefaultRule() {
        }

    }

    public static class Rule1 extends DefaultRule {

        public Rule1(OrderCache cache) {
            super(cache);
        }

        public Rule1() {
        }

        @OnEvent
        public boolean isOrderRejected() {
            return true;
        }
    }

    public static class Rule2 extends DefaultRule {

        public int configCount = 0;

        public Rule2(OrderCache cache) {
            super(cache);
        }

        public Rule2() {
        }

        @EventHandler(propagate = false)
        public boolean configUpdate(ConfigEvent event) {
            configCount++;
            return false;
        }

        @OnEvent
        public boolean isOrderRejected() {
            return true;
        }
    }

    public static class RuleValidator {

        public ArrayList<DefaultRule> rules = new ArrayList<>();

        public int ruleCount = 0;
        public int validationFailedCount = 0;

        @OnParentUpdate
        public void ruleFailed(DefaultRule failedRule) {
            ruleCount++;
        }

        @OnEvent
        public void validationFailed() {
            validationFailedCount++;
        }

    }

    public static class MarketTickEvent implements Event {
    }

    public static class PositionEvent implements Event {
    }

    public static class MarketHandler {

        int eventCount;

        @EventHandler
        public boolean newTick(MarketTickEvent tick) {
            eventCount++;
            return false;
        }
    }

    public static class PricerFormer {

        public MarketHandler marketHandler;
        int eventCount;
        int parentCount;

        @OnParentUpdate
        public void tickUpdated(MarketHandler marketHandler) {
            parentCount++;
        }

        @OnEvent
        public void formPrice() {
            eventCount++;
        }

    }

    public static class TickCounter {

        public MarketHandler marketHandler;
        int eventCount;
        int parentCount;

        @OnParentUpdate(guarded = false)
        public void tickUpdated(MarketHandler marketHandler) {
            parentCount++;
        }

        @OnEvent
        public void formPrice() {
            eventCount++;
        }
    }

    public static class PositionCalculator {

        @EventHandler
        public boolean postionUpdate(PositionEvent orderEvent) {
            return true;
        }

        @OnEvent
        public boolean recalcPosition() {
            return false;
        }

    }

    public static class ThrottledPublisher {

        public PricerFormer pricerFormer;
        public PositionCalculator positionCalc;
        int eventCount;

        @OnEvent
        public void publish() {
            eventCount++;
        }

        @EventHandler
        public void newOrder(NewOrderEvent orderEvent) {

        }

        @OnParentUpdate
        public boolean positionChanged(PositionCalculator positionCalc) {
            return true;
        }

        @OnParentUpdate
        public void priceChanged(PricerFormer pricerFormer) {

        }
    }

}
