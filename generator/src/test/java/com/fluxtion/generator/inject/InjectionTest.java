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
package com.fluxtion.generator.inject;

import com.fluxtion.api.annotations.Config;
import com.fluxtion.api.annotations.ConfigVariable;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.FilterId;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.api.node.NodeFactory;
import com.fluxtion.api.node.NodeRegistry;
import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.runtime.event.Event;
import java.util.Map;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class InjectionTest extends BaseSepTest {

    @Test
    public void testInjectionTree() {
        com.fluxtion.runtime.lifecycle.EventHandler sep = buildAndInitSep(WordProcessorSep.class);
        WordProcessor processor = getField("wordProcessor");
        sep.onEvent(new CharEvent('c'));
        assertTrue(processor.testAndClear(0, 'c'));
        sep.onEvent(new CharEvent('1'));
        assertTrue(processor.testAndClear(1, '0'));
        sep.onEvent(new CharEvent('h'));
        assertTrue(processor.testAndClear(0, '0'));
        sep.onEvent(new CharEvent('b'));
        assertTrue(processor.testAndClear(0, 'b'));
        sep.onEvent(new CharEvent('4'));
        assertTrue(processor.testAndClear(4, '0'));
    }

    @Test
    public void testInjectionNoFactoryTree() {
        com.fluxtion.runtime.lifecycle.EventHandler sep = buildAndInitSep(WordProcessorSepNoFactory.class);
        WordProcessorNoFactory processor = getField("wordProcessor");
        Assert.assertEquals(34, processor.handler.intVal);
        Assert.assertEquals("someName", processor.handler.stringVal);
    }

    @Test
    public void testInjectionNoFactoryVariablConfigTree() {
        com.fluxtion.runtime.lifecycle.EventHandler sep = buildAndInitSep(WordProcessorSepNoFactoryConfigVariable.class);
        WordProcessorNoFactoryVariableConfig processor = getField("wordProcessor");
        Assert.assertEquals(10, processor.handler.intVal);
        Assert.assertEquals("variable val", processor.handler.stringVal);
    }
    
    public static class WordProcessorSep extends SEPConfig {

        {
            addPublicNode(new WordProcessor(), "wordProcessor");
        }
    }

    public static class WordProcessorSepNoFactory extends SEPConfig {

        {
            addPublicNode(new WordProcessorNoFactory(), "wordProcessor");
        }
    }

    public static class WordProcessorSepNoFactoryConfigVariable extends SEPConfig {

        {
            addPublicNode(new WordProcessorNoFactoryVariableConfig(), "wordProcessor");
        }
    }
    
    

    public static class NoFactoryCharHandler {

        private char receivedChar;
        
        public String stringVal;
        public int intVal;

        @EventHandler
        public void onChar(CharEvent charEvent) {
            receivedChar = (char) charEvent.filterId();
//            //System.out.println("received char:"+ charEvent.filterId());
        }
    }
    
    
    public static class WordProcessorNoFactoryVariableConfig {
        
        public int parentIntVal = 10;
        public String parentStringVal = "variable val";
        
        @Inject
        @ConfigVariable(key = "intVal", field = "parentIntVal")
        @ConfigVariable(key = "stringVal", field = "parentStringVal")
        public NoFactoryCharHandler handler;
        
        @OnEvent
        public void update(){
            
        }
    }
    
    public static class WordProcessorNoFactory {
        @Inject
        @Config(key = "intVal", value = "34")
        @Config(key = "stringVal", value = "someName")
        public NoFactoryCharHandler handler;
        
        @OnEvent
        public void update(){
            
        }
    }
    
    public static class WordProcessor {

        @Inject
        @Config(key = "char", value = "c")
        public CharHandler handler_c;

        @Inject
        @Config(key = "char", value = "b")
        public CharHandler handler_b;

        @Inject
        public Char2Int char2Int;

        private Integer receivedInt = 0;
        private Character receivedChar = '0';

        @OnParentUpdate
        public void onDigit(Char2Int char2Int) {
            //System.out.println("received digit:" + char2Int.asInt);
            receivedInt = char2Int.asInt;
        }

        @OnParentUpdate
        public void charUpdated(CharHandler charHandler) {
            //System.out.println("received char:" + (char) charHandler.receivedChar);
            receivedChar = charHandler.receivedChar;
        }

        @OnEvent
        public void onEvent() {
            //System.out.println("finished processing\n");
        }

        public boolean testAndClear(Integer i, Character c) {
            boolean match = i.equals(receivedInt) & c.equals(receivedChar);
            receivedInt = 0;
            receivedChar = '0';
            return match;
        }

    }

    public static class Char2Int {

        @Inject
        public CharHandler handler;

        public int asInt;

        @OnEvent
        public boolean convert() {
//            //System.out.println("digit converison for char:" + handler.receivedChar);
            if (Character.isDigit(handler.receivedChar)) {
                asInt = handler.receivedChar - '0';
//                //System.out.println("converted to:" + asInt);
                return true;
            }
            asInt = Integer.MAX_VALUE;
            return false;
        }
    }

    public static class CharEvent extends Event {

        public CharEvent(char filterChar) {
            filterId = filterChar;
        }
    }

    public static class CharHandler {

        @FilterId
        public int filterChar = Event.NO_ID;

        public char receivedChar;

        public CharHandler(char receivedChar) {
            this.filterChar = receivedChar;
            this.receivedChar = receivedChar;
        }

        public CharHandler() {
        }

        @EventHandler
        public void onChar(CharEvent charEvent) {
            receivedChar = (char) charEvent.filterId();
//            //System.out.println("received char:"+ charEvent.filterId());
        }
    }

    public static class Char2IntFactory implements NodeFactory<Char2Int> {

        @Override
        public Char2Int createNode(Map arg0, NodeRegistry arg1) {
            return new Char2Int();
        }

    }

    public static class CharHandlerFactory implements NodeFactory<CharHandler> {

        @Override
        public CharHandler createNode(Map arg0, NodeRegistry arg1) {
            if (arg0.containsKey("char")) {
                return new CharHandler(((String) arg0.get("char")).charAt(0));
            }
            return new CharHandler();
        }

    }

}
