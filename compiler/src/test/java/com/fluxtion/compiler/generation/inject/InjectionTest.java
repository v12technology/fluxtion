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
package com.fluxtion.compiler.generation.inject;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.FilterId;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Config;
import com.fluxtion.runtime.annotations.builder.ConfigVariable;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.event.DefaultEvent;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.time.Clock;
import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class InjectionTest extends MultipleSepTargetInProcessTest {

    public InjectionTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testInjectionTree() {
        sep(cfg -> cfg.addPublicNode(new WordProcessor(), "wordProcessor"));
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
        sep(cfg -> cfg.addPublicNode(new WordProcessorNoFactory(), "wordProcessor"));
        WordProcessorNoFactory processor = getField("wordProcessor");
        Assert.assertEquals(34, processor.handler.intVal);
        Assert.assertEquals("someName", processor.handler.stringVal);
    }

    @Test
    public void testInjectionNoFactoryVariablConfigTree() {
        sep(cfg -> cfg.addPublicNode(new WordProcessorNoFactoryVariableConfig(), "wordProcessor"));
        WordProcessorNoFactoryVariableConfig processor = getField("wordProcessor");
        Assert.assertEquals(10, processor.handler.intVal);
        Assert.assertEquals("variable val", processor.handler.stringVal);
    }

    @Test
    public void injectFinalField() {
        sep(cfg -> cfg.addPublicNode(new InjectClockWithSetter(), "injectedClock"));
        InjectClockWithSetter inj = getField("injectedClock");
        Assert.assertNotNull(inj.getClock());
    }

    @Test
    public void injectSingleton() {
        sep(cfg -> {
            cfg.addPublicNode(new InjectingSingletonHolder(), "instance1");
            cfg.addPublicNode(new InjectingSingletonHolder(), "instance2");
        });
        InjectingSingletonHolder instance1 = getField("instance1");
        InjectingSingletonHolder instance2 = getField("instance2");
        Assert.assertNotEquals(instance1, instance2);
        Assert.assertEquals(instance1.singleton, instance2.singleton);
    }

    @Getter
    @Setter
    public static class InjectClockWithSetter {

        @Inject
        private Clock clock;

    }

    public static class MySingleton {

    }

    public static class InjectingSingletonHolder {

        @Inject(singleton = true, singletonName = "mySingleton")
        public MySingleton singleton;

        @OnEventHandler
        public boolean onChar(CharEvent charEvent) {
            return true;
        }
    }

    public static class NoFactoryCharHandler {

        private char receivedChar;

        public String stringVal;
        public int intVal;

        @OnEventHandler
        public boolean onChar(CharEvent charEvent) {
            receivedChar = (char) charEvent.filterId();
            return true;
        }
    }

    public static class WordProcessorNoFactoryVariableConfig {

        public int parentIntVal = 10;
        public String parentStringVal = "variable val";

        @Inject
        @ConfigVariable(key = "intVal", field = "parentIntVal")
        @ConfigVariable(key = "stringVal", field = "parentStringVal")
        public NoFactoryCharHandler handler;

        @OnTrigger
        public boolean update() {
            return true;
        }
    }

    public static class WordProcessorNoFactory {

        @Inject
        @Config(key = "intVal", value = "34")
        @Config(key = "stringVal", value = "someName")
        public NoFactoryCharHandler handler;

        @OnTrigger
        public boolean update() {
            return true;
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

        @OnTrigger
        public boolean onEvent() {
            return true;
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

        @OnTrigger
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

    public static class CharEvent extends DefaultEvent {

        public CharEvent(char filterChar) {
            filterId = filterChar;
        }
    }

    public static class CharHandler {

        @FilterId
        public int filterChar = Event.NO_INT_FILTER;

        public char receivedChar;

        public CharHandler(char receivedChar) {
            this.filterChar = receivedChar;
            this.receivedChar = receivedChar;
        }

        public CharHandler() {
        }

        @OnEventHandler
        public boolean onChar(CharEvent charEvent) {
            receivedChar = (char) charEvent.filterId();
            return true;
//            //System.out.println("received char:"+ charEvent.filterId());
        }
    }

    public static class Char2IntFactory implements NodeFactory<Char2Int> {

        @Override
        public Char2Int createNode(Map<String, ? super Object> config, NodeRegistry registry) {
            return new Char2Int();
        }
    }

    public static class CharHandlerFactory implements NodeFactory<CharHandler> {

        @Override
        public CharHandler createNode(Map<String, ? super Object> arg0, NodeRegistry arg1) {
            if (arg0.containsKey("char")) {
                return new CharHandler(((String) arg0.get("char")).charAt(0));
            }
            return new CharHandler();
        }

    }

}
