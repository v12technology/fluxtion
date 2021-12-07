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
package com.fluxtion.compiler.generation.filter;

import com.fluxtion.runtim.annotations.EventHandler;
import com.fluxtion.runtim.event.DefaultEvent;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import lombok.Data;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class FilteringTest extends MultipleSepTargetInProcessTest {

    public FilteringTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void singleFilterTest() {
        sep(cfg -> {
            cfg.addPublicNode(new SingleFilteredHandler(), "handler");
        });
        SingleFilteredHandler handler = getField("handler");
        assertThat(handler.getWordACount(), is(0));
        onEvent(new WordEvent("A"));
        assertThat(handler.getWordACount(), is(1));
    }


    @Test
    public void testClassFilter() {
        sep(cfg -> {
            cfg.addPublicNode(new TestHandler(), "handler");
        });
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

    public static class ClassFilterEvent extends DefaultEvent {

        public ClassFilterEvent(Class clazz) {
            filterString = clazz.getCanonicalName();
        }
    }


    public static class WordEvent extends DefaultEvent {
        public WordEvent(String word) {
            super();
            filterString = word;
        }
    }

    @Data
    public static class SingleFilteredHandler {
        private int wordACount;

        @EventHandler(filterString = "A")
        public void processWordA(WordEvent wordA) {
            wordACount++;
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
        public void processWordA(WordEvent wordA) {
            wordACount++;
        }

        @EventHandler(filterVariable = "filterB")
        public void processWordB(WordEvent wordB) {
            wordBCount++;
        }
    }

}
