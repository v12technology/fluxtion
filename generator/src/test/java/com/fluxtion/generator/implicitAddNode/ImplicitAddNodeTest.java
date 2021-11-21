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
package com.fluxtion.generator.implicitAddNode;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.ExcludeNode;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import lombok.Value;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class ImplicitAddNodeTest extends BaseSepInprocessTest {
    
    @Test
    public void testAddNodeByAnnotation() {
        sep(cfg -> {
            Counter counter = new Counter(new Stringhandler());
            counter.intermediateNode = new IntermediateNode(new DoubleHandler());
            counter.dateHandler = new DateHandler();
            cfg.addPublicNode(counter, "counter");
        });
        Counter processor = getField("counter");
        onEvent("test");
        onEvent("test");
        assertThat(processor.count, is(2));
        //send some doubles
        onEvent(1.08);
        onEvent(1.08);
        onEvent(1.08);
        assertThat(processor.count, is(5));

        onEvent(new Date());
        onEvent(new Date());
        onEvent(new Date());
        assertThat(processor.count, is(5));
    }

    public static class Stringhandler{
    
        @EventHandler
        public void stringUpdate(String s){
        
        }
    }
    
      public static class DoubleHandler{
    
        @EventHandler
        public void doubleUpdate(Double s) {
        }
    }

    public static class DateHandler{

        @EventHandler
        public void doubleUpdate(Date s){

        }

    }
      
    public static class Counter{

        private int count;
        private final Stringhandler myHandler;
        IntermediateNode intermediateNode;
        @ExcludeNode
        DateHandler dateHandler;

        public Counter(Stringhandler myHandler) {
            this.myHandler = myHandler;
        }
        
        @OnEvent
        public void increment(){
            count++;
        }
    
    }
    
    @Value
    public static class IntermediateNode{
        Object parent;
        
        @OnEvent
        public void onEvent(){
        }
    }
    
}
