/*
 * Copyright (C) 2019 V12 Technology Ltd.
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
package com.fluxtion.compiler.generation.subclass;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.event.Event;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author V12 Technology Ltd.
 */
public class EventSubclassTest extends MultipleSepTargetInProcessTest {

    public EventSubclassTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void subclass1() {
//        Class<TimeEvent> timeEventClass = TimeEvent.class;
        sep(d -> d.addPublicNode(new MyHandler(), "handler"));
        MyHandler handler = getField("handler");
        onEvent(new ImplEvent());
        assertThat(handler.anyEvent, is(1));
        assertThat(handler.baseEvent, is(0));
        assertThat(handler.implEvent, is(1));
        assertThat(handler.timeEvent, is(1));

        onEvent(new ExtendTimeEvent());
        assertThat(handler.anyEvent, is(1));
        assertThat(handler.baseEvent, is(0));
        assertThat(handler.implEvent, is(1));
        assertThat(handler.timeEvent, is(1));

        onEvent(new TimeEvent());
        assertThat(handler.anyEvent, is(2));
        assertThat(handler.baseEvent, is(0));
        assertThat(handler.implEvent, is(1));
        assertThat(handler.timeEvent, is(2));

        onEvent(new BaseEvent());
        assertThat(handler.anyEvent, is(3));
        assertThat(handler.baseEvent, is(1));
        assertThat(handler.implEvent, is(1));
        assertThat(handler.timeEvent, is(2));
    }

    @Test
    public void graphTest() {
        sep(c -> {
            TimeHandler timeHandler = c.addPublicNode(new TimeHandler(), "timeHandler");
            AnyEventHandler anyEventHandler = c.addPublicNode(new AnyEventHandler(), "anyEventHandler");
            ImplEventHandler implEventHandler = c.addPublicNode(new ImplEventHandler(), "implEventHandler");
            c.addPublicNode(new UpdateListener(timeHandler), "timeListener");
            c.addPublicNode(new UpdateListener(anyEventHandler), "anyListener");
            c.addPublicNode(new UpdateListener(implEventHandler), "implListener");
        });
        TimeHandler timeHandler = getField("timeHandler");
        AnyEventHandler anyEventHandler = getField( "anyEventHandler");
        ImplEventHandler implEventHandler = getField( "implEventHandler");
        UpdateListener timeListener = getField("timeListener");
        UpdateListener anyListener = getField("anyListener");
        UpdateListener implListener = getField("implListener");
        //
        onEvent(new ImplEvent());
        assertThat(timeHandler.eventCount, is(1));
        assertThat(anyEventHandler.eventCount, is(1));
        assertThat(implEventHandler.eventCount, is(1));
        assertThat(timeListener.eventCount, is(1));
        assertThat(anyListener.eventCount, is(0));
        assertThat(implListener.eventCount, is(1));
        
        onEvent(new TimeEvent());
        assertThat(timeHandler.eventCount, is(2));
        assertThat(anyEventHandler.eventCount, is(2));
        assertThat(implEventHandler.eventCount, is(1));
        assertThat(timeListener.eventCount, is(2));
        assertThat(anyListener.eventCount, is(0));
        assertThat(implListener.eventCount, is(1));
    }

    public static class TimeEvent implements Event {
    }

    public static class ImplEvent extends TimeEvent {

    }

    public static class ExtendTimeEvent extends TimeEvent {

    }

    public static class BaseEvent implements Event {

    }

    public static class MyHandler {

        int anyEvent;
        int baseEvent;
        int implEvent;
        int timeEvent;

        @OnEventHandler
        public void anyEvent(Event e) {
            anyEvent++;
        }

        @OnEventHandler
        public void anyTimeEvent(TimeEvent e) {
            timeEvent++;
        }

        @OnEventHandler
        public void baseEvent(BaseEvent e) {
            baseEvent++;
        }

        @OnEventHandler
        public void implEvent(ImplEvent e) {
            implEvent++;
        }

    }

    public static class TimeHandler {

        int eventCount;

        @OnEventHandler
        public void anyTimeEvent(TimeEvent e) {
            eventCount++;
        }
    }

    public static class AnyEventHandler {

        int eventCount;

        @OnEventHandler
        public boolean anyEvent(Event e) {
            eventCount++;
            return false;
        }
    }

    public static class ImplEventHandler {

        int eventCount;

        @OnEventHandler
        public void implEvent(ImplEvent e) {
            eventCount++;
        }
    }

    public static class UpdateListener {

        private final Object parent;
        int eventCount;

        public UpdateListener(Object parent) {
            this.parent = parent;
        }

        @OnTrigger
        public boolean update() {
            eventCount++;
            return true;
        }
    }

}
