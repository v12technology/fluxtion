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
package com.fluxtion.generator.subclass;

import com.fluxtion.api.annotations.*;
import com.fluxtion.generator.util.BaseSepInProcessTest;
import com.fluxtion.generator.util.MultipleSepTargetInProcessTest;
import com.fluxtion.test.event.DefaultFilteredEventHandler;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class EventHandlerSubClassTest extends MultipleSepTargetInProcessTest {

    public EventHandlerSubClassTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testSubClassOnEvent() {

        sep((c) -> {
            c.addPublicNode(new SubclassHandler(new DefaultFilteredEventHandler<>(String.class)), "node");
        });
        SubclassHandler node = getField("node");
        //init
        assertThat(node.eventCount, is(0));
        assertThat(node.initCount, is(1));
        assertThat(node.afterEvent, is(0));
        assertThat(node.batchEnd, is(0));
        assertThat(node.batchPause, is(0));
        assertThat(node.tearDownCount, is(0));
        //event + after event
        onEvent("test string");
        assertThat(node.eventCount, is(1));
        assertThat(node.afterEvent, is(1));
        //batch pause
        batchPause();
        assertThat(node.eventCount, is(1));
        assertThat(node.initCount, is(1));
        assertThat(node.afterEvent, is(1));
        assertThat(node.batchEnd, is(0));
        assertThat(node.batchPause, is(1));
        assertThat(node.tearDownCount, is(0));
        //batch end
        batchEnd();
        assertThat(node.eventCount, is(1));
        assertThat(node.initCount, is(1));
        assertThat(node.afterEvent, is(1));
        assertThat(node.batchEnd, is(1));
        assertThat(node.batchPause, is(1));
        assertThat(node.tearDownCount, is(0));
        //teardown
        teardDown();
        assertThat(node.eventCount, is(1));
        assertThat(node.initCount, is(1));
        assertThat(node.afterEvent, is(1));
        assertThat(node.batchEnd, is(1));
        assertThat(node.batchPause, is(1));
        assertThat(node.tearDownCount, is(1));
    }

    public abstract static class ParentHandler {

        @SepNode
        final DefaultFilteredEventHandler source;

        public ParentHandler(DefaultFilteredEventHandler source) {
            this.source = source;
        }

        @OnEvent
        public boolean onEvent() {
            return true;
        }
        
        @AfterEvent
        public void afterEvent(){}

        @Initialise
        public void init() {}

        @TearDown
        public void tearDown() {}
        
        @OnBatchEnd
        public void batchEnd(){}
        
        @OnBatchPause
        public void batchPause(){}
        
    }

    public static class SubclassHandler extends ParentHandler {

        int afterEvent = 0;
        int batchEnd = 0;
        int batchPause = 0;
        int eventCount = 0;
        int initCount = 0;
        int tearDownCount = 0;

        public SubclassHandler(DefaultFilteredEventHandler source) {
            super(source);
        }

        @Override
        public boolean onEvent() {
            eventCount++;
            return true;
        }

        public void reset() {
            eventCount = 0;
            initCount = 0;
            afterEvent = 0;
            batchEnd = 0;
            batchPause = 0;
            tearDownCount = 0; 
        }

        @Override
        public void init() {
            initCount++;
        }

        @Override
        public void tearDown() {
            tearDownCount++;
        }

        @Override
        public void batchPause() {
            batchPause++;
        }

        @Override
        public void batchEnd() {
            batchEnd++;
        }

        @Override
        public void afterEvent() {
            afterEvent++;
        }
        
    }
}
