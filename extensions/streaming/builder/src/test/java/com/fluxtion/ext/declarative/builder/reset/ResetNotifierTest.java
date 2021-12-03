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
package com.fluxtion.ext.declarative.builder.reset;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.declarative.builder.stream.MapFunctions;
import com.fluxtion.ext.declarative.builder.stream.StreamData;
import com.fluxtion.ext.declarative.builder.stream.StreamInProcessTest;
import com.fluxtion.ext.streaming.api.Wrapper;
import lombok.Value;
import org.junit.Test;

import static com.fluxtion.ext.streaming.builder.factory.DefaultNumberBuilder.defaultVal;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.FilterBuilder.filter;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsBuilder.cumSum;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class ResetNotifierTest extends StreamInProcessTest {
    
    @Test
    //to be reinstated
    public void resetModes() {
        sep(c -> {
            Wrapper<Number> cumSum = cumSum(Sale::getAmountSold)
                    .id("soldCumSum")
                    //                .log("amountSold:", Number::intValue)
                    .resetNoPublish(filter("reset_signal"::equalsIgnoreCase).id("resetSignal"))
                    .publishAndReset(filter("publishReset_signal"::equalsIgnoreCase).id("publishReset_signal"))
                    .resetAndPublish(filter("resetPublish_signal"::equalsIgnoreCase).id("resetPublishSignal"));
            c.addPublicNode(new Receiver(cumSum), "target");
            cumSum.map(ResetNotifierTest::x10).id("x10");
        });//, "com.fluxtion.ext.declarative.builder.reset.generated_nodefault.ResetProcessor");

        Number soldCumSum = getWrappedField("soldCumSum");
        Number x10 = getWrappedField("x10");
        Receiver target = getField("target");
        onEvent(new Sale(10));
        onEvent(new Sale(10));
        assertThat(soldCumSum.intValue(), is(20));
        assertThat(x10.intValue(), is(200));
        assertThat(target.updatedCount, is(2));
        
        onEvent("BAD_reset_signal");
        onEvent(new Sale(10));
        assertThat(soldCumSum.intValue(), is(30));
        assertThat(x10.intValue(), is(300));
        assertThat(target.updatedCount, is(3));
        
        onEvent("resetPublish_signal");
        assertThat(soldCumSum.intValue(), is(0));
        assertThat(x10.intValue(), is(0));
        assertThat(target.updatedCount, is(4));
        
        onEvent(new Sale(40));
        assertThat(soldCumSum.intValue(), is(40));
        assertThat(x10.intValue(), is(400));
        assertThat(target.updatedCount, is(5));
        
        onEvent("reset_signal");
        assertThat(soldCumSum.intValue(), is(0));
        assertThat(x10.intValue(), is(400));
        assertThat(target.updatedCount, is(5));
        
        onEvent(new Sale(5));
        assertThat(soldCumSum.intValue(), is(5));
        assertThat(x10.intValue(), is(50));
        assertThat(target.updatedCount, is(6));
        
        onEvent("publishReset_signal");
        assertThat(soldCumSum.intValue(), is(0));
        assertThat(x10.intValue(), is(50));
        assertThat(target.updatedCount, is(7));
        
        onEvent(new Sale(40));
        assertThat(soldCumSum.intValue(), is(40));
        assertThat(x10.intValue(), is(400));
        assertThat(target.updatedCount, is(8));
    }
    
    @Test
    public void resetModesDefaultNumber() {
        sep(c -> {
            Wrapper<Number> cumSum = defaultVal(0, cumSum(Sale::getAmountSold))
                    .id("soldCumSum")
                    //                .log("amountSold:", Number::intValue)
                    .resetNoPublish(filter("reset_signal"::equalsIgnoreCase).id("resetSignal"))
                    .publishAndReset(filter("publishReset_signal"::equalsIgnoreCase).id("publishReset_signal"))
                    .resetAndPublish(filter("resetPublish_signal"::equalsIgnoreCase).id("resetPublishSignal"));
            c.addPublicNode(new Receiver(cumSum), "target");
            cumSum.map(ResetNotifierTest::x10).id("x10");
        });//, "com.fluxtion.ext.declarative.builder.reset.generated_default.ResetProcessor");

        Number soldCumSum = getWrappedField("soldCumSum");
        Number x10 = getWrappedField("x10");
        Receiver target = getField("target");
        onEvent(new Sale(10));
        onEvent(new Sale(10));
        assertThat(soldCumSum.intValue(), is(20));
        assertThat(x10.intValue(), is(200));
        assertThat(target.updatedCount, is(2));
        
        onEvent("BAD_reset_signal");
        onEvent(new Sale(10));
        assertThat(soldCumSum.intValue(), is(30));
        assertThat(x10.intValue(), is(300));
        assertThat(target.updatedCount, is(3));
        
        onEvent("resetPublish_signal");
        assertThat(soldCumSum.intValue(), is(0));
        assertThat(x10.intValue(), is(0));
        assertThat(target.updatedCount, is(4));
        
        onEvent(new Sale(40));
        assertThat(soldCumSum.intValue(), is(40));
        assertThat(x10.intValue(), is(400));
        assertThat(target.updatedCount, is(5));
        
        onEvent("reset_signal");
        assertThat(soldCumSum.intValue(), is(0));
        assertThat(x10.intValue(), is(400));
        assertThat(target.updatedCount, is(5));
        
        onEvent(new Sale(5));
        assertThat(soldCumSum.intValue(), is(5));
        assertThat(x10.intValue(), is(50));
        assertThat(target.updatedCount, is(6));
        
        onEvent("publishReset_signal");
        assertThat(soldCumSum.intValue(), is(0));
        assertThat(x10.intValue(), is(50));
        assertThat(target.updatedCount, is(7));
        
        onEvent(new Sale(40));
        assertThat(soldCumSum.intValue(), is(40));
        assertThat(x10.intValue(), is(400));
        assertThat(target.updatedCount, is(8));
    }
    
    @Test
    public void resetStatefulRef2Ref() {
        sep((c) -> {
            Wrapper<StreamData> in = select(StreamData.class);
            in.map(new MapFunctions.MapStringCount()::stringCount, StreamData::getStringValue)
                    .resetAndPublish(select(String.class))
                    .id("result");
        });
        onEvent(new StreamData("1"));
        onEvent(new StreamData("1"));
        onEvent(new StreamData("1"));
        assertThat(getWrappedField("result"), is("3"));
        onEvent("reset");
        onEvent(new StreamData("1"));
        assertThat(getWrappedField("result"), is("1"));
    }
    
    @Test
    public void resetBooleanLatch() {
        sep((c) -> {
            Wrapper<String> in = select(String.class);
            in.map(new MapFunctions.MapBooleanSwitch()::stringLatch)
                    .resetAndPublish(filter("reset"::equalsIgnoreCase))
                    .id("result");
        });
        assertThat(getWrappedField("result"), is(false));
        onEvent("hi");
        assertThat(getWrappedField("result"), is(false));
        onEvent("on");
        assertThat(getWrappedField("result"), is(true));
        onEvent("hi");
        assertThat(getWrappedField("result"), is(true));
        onEvent("reset");
        assertThat(getWrappedField("result"), is(false));
    }
    
//    @Test
    public void resetBooleanLatchTriggerOverride() {
        fixedPkg = true;
        sep((c) -> {
            Wrapper<String> in = select(String.class);
            in.map(new MapFunctions.MapBooleanSwitch()::stringLatch)
                    .resetAndPublish(filter("reset"::equalsIgnoreCase))
                    .triggerOverride(filter("calculate"::equalsIgnoreCase))
                    .id("result");
        });
        assertThat(getWrappedField("result"), is(false));
        onEvent("hi");
        assertThat(getWrappedField("result"), is(false));
        onEvent("on");
        assertThat(getWrappedField("result"), is(false));
        //trigger
        onEvent("calculate");
        assertThat(getWrappedField("result"), is(true));
//        assertThat(getWrappedField("result"), is(false));
//        
//        
//        
//        onEvent("hi");
//        assertThat(getWrappedField("result"), is(true));
//        onEvent("reset");
//        assertThat(getWrappedField("result"), is(false));
    }
    
    @Value
    public static class Sale {

        int amountSold;
    }
    
    @Value
    public static class Delivery {

        int amountDelivered;
    }
    
    @Value
    public static class Price {

        int customerPrice;
    }
    
    @Value
    public static class ItemCost {

        int amount;
    }
    
    public static class Receiver {
        
        final Wrapper<Number> cumSum;
        int updatedCount;
        
        public Receiver(Wrapper<Number> cumSum) {
            this.cumSum = cumSum;
        }
        
        @OnEvent
        public void updated() {
            updatedCount++;
        }
    }
    
    public static int x10(int number) {
        return 10 * number;
    }
}
