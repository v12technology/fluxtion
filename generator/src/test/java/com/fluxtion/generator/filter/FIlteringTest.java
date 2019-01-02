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
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.generator.filter;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.api.event.Event;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class FIlteringTest extends BaseSepTest {

    @Test
    public void testClassFilter() {
        buildAndInitSep(FilterBuilder.class);
        TestHandler testHandler = getField("handler");
        onEvent(new ClassFilterEvent(String.class));
        onEvent(new ClassFilterEvent(Double.class));
        assertThat(testHandler.count, is(1));
        //test word processing using filter variables
        onEvent(new WordEvent("A"));
        onEvent(new WordEvent("A"));
        onEvent(new WordEvent("B"));
        onEvent(new WordEvent("A"));
        onEvent(new WordEvent("B"));
        onEvent(new WordEvent("ignored"));
        onEvent(new WordEvent("disregard"));
        assertThat(testHandler.wordACount, is(3));
        assertThat(testHandler.wordBCount, is(2));
    }

    public static class ClassFilterEvent extends Event {

        public ClassFilterEvent(Class clazz) {
            filterString = clazz.getCanonicalName(); 
        }
    }
    

    public static class WordEvent extends Event{
        public WordEvent(String word){
            super();
            filterString = word;
            
        }
    }
    
    public static class TestHandler {

        public int count = 0;
        public int wordACount = 0;
        public int wordBCount = 0;
        public transient String filterA = "A";
        public transient String filterB = "B";
        
        
        @EventHandler(filterStringFromClass = String.class)
        public void handleEvent(ClassFilterEvent event) {
            count++;
        }
        
        @EventHandler(filterVariable = "filterA")
        public void processWordA(WordEvent wordA){
            wordACount++;
        }
        
        @EventHandler(filterVariable = "filterB")
        public void processWordB(WordEvent wordB){
            wordBCount++;
        }
        

    }

    public static class FilterBuilder extends SEPConfig {

        {
            addPublicNode(new TestHandler(), "handler");
        }
    }

}
