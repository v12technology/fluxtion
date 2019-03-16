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

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.api.event.Event;
import java.util.ArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class ParentUpdateListenerTest extends BaseSepTest {

    @Test
    public void testClassFilter() {
        buildAndInitSep(FilterBuilder.class);
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
        buildAndInitSep(FilterBuilderPrivate.class);
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
        buildAndInitSep(ValidationBuilder.class);
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
        buildAndInitSep(PriceBuilder.class);
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

    public static class ClassFilterEvent extends Event {

        public ClassFilterEvent(Class clazz) {
            filterString = clazz.getCanonicalName();
        }
    }

    public static class NoUpdateEvent extends Event {

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

    public static class FilterBuilder extends SEPConfig {

        {
            TestHandler handler = addPublicNode(new TestHandler(), "handler");
            addPublicNode(new TestChild(), "child").parent = handler;
        }
    }

    public static class FilterBuilderPrivate extends SEPConfig {

        {
            TestHandler handler = addPublicNode(new TestHandler(), "handler");
            addPublicNode(new TestChildPrivateParent(handler), "child");
        }
    }

    public static class ValidationBuilder extends SEPConfig {

        {
            RuleValidator validator = addPublicNode(new RuleValidator(), "validator");
            OrderCache cache = addNode(new OrderCache());
            validator.rules.add(addPublicNode(new Rule1(cache), "rule1"));
            validator.rules.add(addPublicNode(new Rule2(cache), "rule2"));
        }
    }

    public static final class ConfigEvent extends Event {
    }

    public static final class NewOrderEvent extends Event {
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

    //*********** DIRTY TEST *******************
    public static class PriceBuilder extends SEPConfig {

        @Override
        public void buildConfig() {
            MarketHandler tickHandler = addPublicNode(new MarketHandler(), "marketHandler");
            PricerFormer pricerFormer = addPublicNode(new PricerFormer(), "priceFormer");
            TickCounter tickCounter = addPublicNode(new TickCounter(), "tickCounter");
            ThrottledPublisher throttledPublisher = addPublicNode(new ThrottledPublisher(), "throttledPublisher");
            PositionCalculator positionCalc = addPublicNode(new PositionCalculator(), "positionCalc");
            pricerFormer.marketHandler = tickHandler;
            tickCounter.marketHandler = tickHandler;
            throttledPublisher.pricerFormer = pricerFormer;
            throttledPublisher.positionCalc = positionCalc;
        }

    }

    public static class MarketTickEvent extends Event {
    }

    public static class PositionEvent extends Event {
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
        public void tickUpdated( MarketHandler marketHandler){
            parentCount++;
        }
        
        @OnEvent
        public void formPrice() {
            eventCount++;
        }

    }
    
    public static class TickCounter{
        public MarketHandler marketHandler;
        int eventCount;
        int parentCount;
        
        @OnParentUpdate(guarded = false)
        public void tickUpdated( MarketHandler marketHandler){
            parentCount++;
        }
        
        @OnEvent
        public void formPrice() {
            eventCount++;
        }
    }
    
    public static class PositionCalculator{
        
        @EventHandler
        public boolean postionUpdate(PositionEvent orderEvent){
            return true;
        }
        
        @OnEvent
        public boolean recalcPosition(){
            return false;
        }
        
    }

    public static class ThrottledPublisher {
        
        public PricerFormer pricerFormer;
        public PositionCalculator positionCalc;
        int eventCount;
        
        @OnEvent
        public void publish(){
            eventCount++;
        }
        
        @EventHandler
        public void newOrder(NewOrderEvent orderEvent){
            
        }

        
        @OnParentUpdate
        public boolean positionChanged(PositionCalculator positionCalc){
            return true;
        }
        
        @OnParentUpdate
        public void priceChanged(PricerFormer pricerFormer){
            
        }
    }

}
