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
package com.fluxtion.compiler.generation.propagation;

import com.fluxtion.compiler.EventProcessorConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.event.DefaultEvent;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.test.event.AnnotatedHandlerNoPropogate;
import com.fluxtion.test.event.RootCB;
import com.fluxtion.test.event.TestEvent;
import com.fluxtion.test.event.TimeEvent;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Greg Higgins
 */
public class NoPropagationTest extends MultipleSepTargetInProcessTest {

    public NoPropagationTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void noPropagationTest() {
        sep(c -> c.addPublicNode(new RootCB("root", new AnnotatedHandlerNoPropogate()), "root"));
        RootCB root = getField("root");
        assertFalse(root.onEventCalled);
        sep.onEvent(new TimeEvent());
        assertFalse(root.onEventCalled);
        sep.onEvent(new TestEvent());
        assertTrue(root.onEventCalled);
    }

    @Test
    public void testComplexNoPropagate() {
        sep(NoPropagationTest::buildFilteringLog);
        ConsolePrinter root = getField("root");
        MsgBuilder msgBuilder = getField("msgBuilder");
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
    public void testComplexNoPropagateWithParentUpdate() {
        sep(NoPropagationTest::buildFilteringLog);
        ConsolePrinter root = getField("root");
        MsgBuilder msgBuilder = getField("msgBuilder");
        NoEventFilterMsg msgBuilder2 = getField("msgBuilder2");
        assertFalse(root.invoked);
        assertFalse(msgBuilder.timeprocessorUpdated);
        assertFalse(msgBuilder2.timeProcessorUpdated);
        sep.onEvent(new TimeEvent());
        assertFalse(root.invoked);
        assertTrue(msgBuilder.timeprocessorUpdated);
        assertTrue(msgBuilder2.timeProcessorUpdated);
        sep.onEvent(new LogControlEvent("filter"));
        assertFalse(root.invoked);
        sep.onEvent(new LogControlEvent("level"));
        assertFalse(root.invoked);
        sep.onEvent(new LogToConsole());
        assertTrue(root.invoked);

    }

    public static void buildFilteringLog(EventProcessorConfig cfg) {
        TimeProcessor timeNode = new TimeProcessor();
        LogNotifier logNotifier = new LogNotifier();
        MsgBuilder msgBuilder = cfg.addPublicNode(new MsgBuilder(timeNode, logNotifier), "msgBuilder");
        NoEventFilterMsg msgBuilder2 = cfg.addPublicNode(new NoEventFilterMsg(timeNode, logNotifier), "msgBuilder2");
        cfg.addPublicNode(new ConsolePrinter(msgBuilder), "root");
    }

    public static class EventFilteringLogBuilder extends EventProcessorConfig {

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

        @OnEventHandler
        public boolean handleTimeUpdate(TimeEvent event) {
            return true;
        }
    }

    public static class LogNotifier {

        @OnEventHandler
        public boolean updateLog(LogToConsole notify) {
            return true;
        }
    }

    public static class MsgBuilderBase {

        @OnEventHandler(filterString = "level", propagate = false)
        public boolean controlLevel(LogControlEvent lc) {
            return false;
        }

        @OnEventHandler(filterString = "filter", propagate = false)
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

        @NoTriggerReference
        public TimeProcessor timeProcessor;

        public LogNotifier notifier;

        @OnParentUpdate
        public void timeUpdated(TimeProcessor processor) {
            this.timeprocessorUpdated = true;
        }

        @OnTrigger
        public boolean buildMessage() {
            return true;
        }

    }

    public static class NoEventFilterMsg {

        private boolean timeProcessorUpdated;

        public NoEventFilterMsg(TimeProcessor timeProcessor, LogNotifier notifier) {
            this.timeProcessor = timeProcessor;
            this.notifier = notifier;
        }

        public NoEventFilterMsg() {
        }

        @NoTriggerReference
        public TimeProcessor timeProcessor;

        public LogNotifier notifier;

        @OnParentUpdate("timeProcessor")
        public void timeUpdated(TimeProcessor processor) {
            this.timeProcessorUpdated = true;
        }

        @OnTrigger
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
        @OnTrigger
        public boolean publishLog() {
            invoked = true;
            return true;
        }
    }

}
