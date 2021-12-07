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
package com.fluxtion.compiler.generation.implicitnodeadd;

import com.fluxtion.runtim.annotations.EventHandler;
import com.fluxtion.runtim.annotations.builder.ExcludeNode;
import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import lombok.Value;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class ImplicitAddNodeTest extends MultipleSepTargetInProcessTest {

    public ImplicitAddNodeTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testScalarImplicitAdd() {
        sep(cfg -> {
            Counter counter = new Counter(new StringHandler());
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

    @Test
    public void testCollectionImplicitAdd(){
        sep(cfg ->{
            VectorCounter vectorCounter = new VectorCounter();
            vectorCounter.parents.add(new StringHandler());
            vectorCounter.parents.add(new DoubleHandler());
            cfg.addNode(vectorCounter, "vectorCounter");
        });
        VectorCounter vectorCounter = getField("vectorCounter");
        onEvent("hello");
        assertThat(vectorCounter.counter, is(1));
        onEvent(25.7);
        assertThat(vectorCounter.counter, is(2));
        onEvent(25);
        assertThat(vectorCounter.counter, is(2));
    }

    public static class StringHandler {
    
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
        private final StringHandler myHandler;
        IntermediateNode intermediateNode;
        @ExcludeNode
        DateHandler dateHandler;

        public Counter(StringHandler myHandler) {
            this.myHandler = myHandler;
        }
        
        @OnEvent
        public void increment(){
            count++;
        }
    
    }

    public static class VectorCounter{
        List<Object> parents = new ArrayList<>();
        int counter;

        @OnEvent
        public void onEvent(){
            counter++;
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
