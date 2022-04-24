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
package com.fluxtion.compiler.generation.sepnode;

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.SepNode;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class AddSepNodeTest extends MultipleSepTargetInProcessTest {

    public AddSepNodeTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testAddNodeByAnnotation() {
        sep(cfg -> {
            cfg.addPublicNode(new Counter(new Stringhandler()), "counter");
        });
        Counter processor = getField("counter");
        onEvent("test");
        onEvent("test");
        assertThat(processor.count, is(2));
    }
    
    public static class Stringhandler{
    
        @OnEventHandler
        public void stringUpdate(String s){
        
        }
    }
    
    
    public static class Counter{

        private int count;
        @SepNode
        private final Stringhandler myHandler;

        public Counter(Stringhandler myHandler) {
            this.myHandler = myHandler;
        }
        
        @OnTrigger
        public void increment(){
            count++;
        }
    
    }
    
}
