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

import com.fluxtion.compiler.generation.util.Slf4jAuditLogger;
import com.fluxtion.runtime.annotations.FilterType;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.audit.EventLogControlEvent;
import com.fluxtion.runtime.event.DefaultEvent;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.node.DefaultEventHandlerNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;

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

    @Test
    public void testUnmatchedFilter() {
        sep(cfg -> {
            cfg.addPublicNode(new UnMatchedHandler(), "handler");
            cfg.addEventAudit(EventLogControlEvent.LogLevel.INFO);
        });
        UnMatchedHandler testHandler = getField("handler");

        onEvent(new EventLogControlEvent(new Slf4jAuditLogger()));
        onEvent(new WordEvent("ignored"));
        assertThat(testHandler.wordACount, is(0));
        assertThat(testHandler.anyWord, is(1));
        assertThat(testHandler.wordUnmatched, is(1));

        onEvent(new WordEvent("A"));
        assertThat(testHandler.wordACount, is(1));
        assertThat(testHandler.anyWord, is(2));
        assertThat(testHandler.wordUnmatched, is(1));
    }

    @Test
    public void defaultFilterHandlerTest() {

        sep(cfg -> {
            cfg.addPublicNode(new DefaultEventHandlerNode<>(String.class), "handler");
            cfg.addPublicNode(new DefaultEventHandlerNode<>("filter", WordEvent.class), "handlerFiltered");
        });
        DefaultEventHandlerNode<String> stringHandler = getField("handler");
        DefaultEventHandlerNode<WordEvent> filteredHandler = getField("handlerFiltered");
        onEvent("test");
        assertNull(filteredHandler.get());
        assertThat(stringHandler.get(), is("test"));

        onEvent(new WordEvent("ignore me"));
        assertNull(filteredHandler.get());
        assertThat(stringHandler.get(), is("test"));

        onEvent(new WordEvent("filter"));
        assertThat(stringHandler.get(), is("test"));
        assertThat(filteredHandler.get().filterString(), is(new WordEvent("filter").filterString()));
    }

    public static class ClassFilterEvent extends DefaultEvent {

        public ClassFilterEvent(Class clazz) {
            filterString = clazz.getCanonicalName();
        }
    }


    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class WordEvent extends DefaultEvent {
        public WordEvent(String word) {
            super();
            filterString = word;
        }
    }

    @Data
    public static class SingleFilteredHandler {
        private int wordACount;

        @OnEventHandler(filterString = "A")
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

        @OnEventHandler(filterStringFromClass = String.class)
        public void handleEvent(ClassFilterEvent event) {
            count++;
        }

        @OnEventHandler(filterVariable = "filterA")
        public void processWordA(WordEvent wordA) {
            wordACount++;
        }

        @OnEventHandler(filterVariable = "filterB")
        public void processWordB(WordEvent wordB) {
            wordBCount++;
        }
    }

    public static class UnMatchedHandler {
        public int wordACount = 0;
        public int wordUnmatched = 0;
        public int anyWord = 0;
        public transient String filterA = "A";

        @OnEventHandler(filterVariable = "filterA")
        public void processWordA(WordEvent wordA) {
            wordACount++;
        }

        @OnEventHandler
        public void processAnyWord(WordEvent anyWordEvent) {
            anyWord++;
        }

        @OnEventHandler(FilterType.defaultCase)
        public void processWordUnmatched(WordEvent wordB) {
            wordUnmatched++;
        }
    }

}
