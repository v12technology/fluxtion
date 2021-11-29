/*
 * Copyright (C) 2016 Greg Higgins (greg.higgins@v12technology.com)
 *
 * This file is part of Fluxtion.
 *
 * Fluxtion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxtion.generator.targets;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.api.event.DefaultEvent;
import com.fluxtion.api.event.Event;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.compiler.SepCompilerConfig;
import com.fluxtion.test.event.AnnotatedHandlerNoPropogate;
import com.fluxtion.test.event.RootCB;
import com.fluxtion.test.event.TestEvent;
import com.fluxtion.test.event.TimeEvent;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class NoPropogationTest {

    @Test
    public void testComplexNoPropogate() throws Exception {
        //System.out.println("comple no propogation");
        SepCompilerConfig compileCfg = JavaTestGeneratorHelper.getTestSepCompileConfig(
                "com.fluxtion.generator.test.generated.complexnopropogation", "NoPropogationComplexProcessor");
        compileCfg.setConfigClass(EventFilteringLogBuilder.class.getName());
        compileCfg.setSupportDirtyFiltering(true);
        StaticEventProcessor sep = JavaTestGeneratorHelper.generateAndInstantiate(compileCfg);
        ConsolePrinter root = (ConsolePrinter) sep.getClass().getField("root").get(sep);
        MsgBuilder msgBuilder = (MsgBuilder) sep.getClass().getField("msgBuilder").get(sep);
        ((Lifecycle) sep).init();
        assertFalse(root.invoked);
        assertFalse(msgBuilder.timeprocessorUpdated);
        sep.onEvent(new TimeEvent());
        assertFalse(root.invoked);
        assertTrue(msgBuilder.timeprocessorUpdated);
        sep.onEvent(new LogControlEvent("filter"));
        assertFalse(root.invoked);
        sep.onEvent(new LogControlEvent("level"));
        assertFalse(root.invoked);
        sep.onEvent(new LogToConsole());
        assertTrue(root.invoked);
    }

    @Test
    public void testComplexNoPropogateWIthParentUpdate() throws Exception {
        //System.out.println("complex no propogation and parent update");
        SepCompilerConfig compileCfg = JavaTestGeneratorHelper.getTestSepCompileConfig(
                "com.fluxtion.generator.test.generated.complexnopropogation2", "NoPropogationComplexProcessor");
        compileCfg.setConfigClass(EventFilteringLogBuilder.class.getName());
        compileCfg.setSupportDirtyFiltering(true);
        StaticEventProcessor sep = JavaTestGeneratorHelper.generateAndInstantiate(compileCfg);
        ConsolePrinter root = (ConsolePrinter) sep.getClass().getField("root").get(sep);
        MsgBuilder msgBuilder = (MsgBuilder) sep.getClass().getField("msgBuilder").get(sep);
        NoEventFilterMsg msgBuilder2 = (NoEventFilterMsg) sep.getClass().getField("msgBuilder2").get(sep);
        ((Lifecycle) sep).init();
        assertFalse(root.invoked);
        assertFalse(msgBuilder.timeprocessorUpdated);
        assertFalse(msgBuilder2.timeprocessorUpdated);
        sep.onEvent(new TimeEvent());
        assertFalse(root.invoked);
        assertTrue(msgBuilder.timeprocessorUpdated);
        assertTrue(msgBuilder2.timeprocessorUpdated);
        sep.onEvent(new LogControlEvent("filter"));
        assertFalse(root.invoked);
        sep.onEvent(new LogControlEvent("level"));
        assertFalse(root.invoked);
        sep.onEvent(new LogToConsole());
        assertTrue(root.invoked);
    }

    @Test
    public void testNoPropogate() throws Exception {
        //System.out.println("function");
        SepCompilerConfig compileCfg = JavaTestGeneratorHelper.getTestSepCompileConfig(
                "com.fluxtion.generator.test.generated.nopropogation", "NoPropogationProcessor");
        compileCfg.setConfigClass(LogBuilder1.class.getName());
        compileCfg.setSupportDirtyFiltering(true);
        StaticEventProcessor sep = JavaTestGeneratorHelper.generateAndInstantiate(compileCfg);
        RootCB root = (RootCB) sep.getClass().getField("root").get(sep);
        ((Lifecycle) sep).init();
        assertFalse(root.onEventCalled);
        sep.onEvent(new TimeEvent());
        assertFalse(root.onEventCalled);
        sep.onEvent(new TestEvent());
        assertTrue(root.onEventCalled);
    }

    public static class LogBuilder1 extends SEPConfig {

        public LogBuilder1() {
//
            AnnotatedHandlerNoPropogate noPropHandler = addNode(new AnnotatedHandlerNoPropogate());
            addPublicNode(new RootCB("root", noPropHandler), "root");

        }
    }

    public static class EventFilteringLogBuilder extends SEPConfig {

        {
            TimeProcessor timeNode = addNode(new TimeProcessor());
            LogNotifier logNotifer = addNode(new LogNotifier());
            MsgBuilder msgBuilder = addPublicNode(new MsgBuilder(timeNode, logNotifer), "msgBuilder");
            NoEventFilterMsg msgBuilder2 = addPublicNode(new NoEventFilterMsg(timeNode, logNotifer), "msgBuilder2");
            addPublicNode(new ConsolePrinter(msgBuilder), "root");
        }
    }

    public static class LogControlEvent extends DefaultEvent {

        public LogControlEvent(String filter) {
            super();
            filterString = filter;
        }

    }

    public static class LogToConsole implements Event {
    }

    public static class TimeProcessor {

        @com.fluxtion.api.annotations.EventHandler
        public boolean handleTimeUpdate(TimeEvent event) {
            return true;
        }
    }

    public static class LogNotifier {

        @com.fluxtion.api.annotations.EventHandler
        public void updateLog(LogToConsole notify) {
//            return true;
        }
    }

    public static class MsgBuilderBase {

        @com.fluxtion.api.annotations.EventHandler(filterString = "level", propagate = false)
        public boolean controlLevel(LogControlEvent lc) {
            return false;
        }

        @com.fluxtion.api.annotations.EventHandler(filterString = "filter", propagate = false)
        public boolean controlFilter(LogControlEvent lc) {
            return false;
        }

    }

    public static class MsgBuilder extends MsgBuilderBase {

        private boolean timeprocessorUpdated;

        public MsgBuilder(TimeProcessor timeProcessor, LogNotifier notifier) {
            this.timeProcessor = timeProcessor;
            this.notifier = notifier;
        }

        public MsgBuilder() {
        }

        @NoEventReference
        public TimeProcessor timeProcessor;

        public LogNotifier notifier;
        
        @OnParentUpdate
        public void timeUpdated(TimeProcessor processor){
            this.timeprocessorUpdated = true;
        }

        @OnEvent
        public boolean buildMessage() {
            return true;
        }

    }
    
    public static class NoEventFilterMsg{

        private boolean timeprocessorUpdated;

        public NoEventFilterMsg(TimeProcessor timeProcessor, LogNotifier notifier) {
            this.timeProcessor = timeProcessor;
            this.notifier = notifier;
        }

        public NoEventFilterMsg() {
        }

        @NoEventReference
        public TimeProcessor timeProcessor;

        public LogNotifier notifier;
        
        @OnParentUpdate("timeProcessor")
        public void timeUpdated(TimeProcessor processor){
            this.timeprocessorUpdated = true;
        }

        @OnEvent
        public boolean buildMessage() {
            return true;
        }
    }

    public static class ConsolePrinter {
        public boolean invoked = false;
        public MsgBuilder msgBuilder;

        public ConsolePrinter() {
        }

        private ConsolePrinter(MsgBuilder msgBuilder) {
            this.msgBuilder = msgBuilder;
        }
//        
//        @OnParentUpdate
//        public void publishLog(MsgBuilder builder) {
        @OnEvent
        public void publishLog() {
            invoked = true;
        }
    }

}
