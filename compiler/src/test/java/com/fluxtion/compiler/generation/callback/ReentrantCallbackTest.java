/*
 * SPDX-FileCopyrightText: © 2024 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package com.fluxtion.compiler.generation.callback;

import com.fluxtion.compiler.builder.dataflow.DataFlow;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.EventProcessorContextListener;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.callback.Callback;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import com.fluxtion.runtime.event.Signal;
import lombok.Data;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.*;

public class ReentrantCallbackTest extends MultipleSepTargetInProcessTest {

    public static final String END_OF_BATCH = "stringEndOfBatch";

    public ReentrantCallbackTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void callbackWithReentrantEventTest() {
        sep(new ReentrantCallBack());
        onEvent("callback");
    }

    @Test
    public void callbackWithReentrantTriggerTest() {
        HashMap<String, String> resultMap = new HashMap<>();
        sep(c -> {
            c.addNode(new TriggerRentrantNode());

            DataFlow.subscribe(StringBatch.class)
                    .flatMap(StringBatch::getStrings)
//                    .console("pre trigger:{}")
                    .groupBy(Objects::toString)
                    .publishTriggerOverride(DataFlow.subscribeToSignal(END_OF_BATCH).console("eobSignal:{}"))
                    .map(GroupBy::toMap)
                    .sink("results")
//                    .console("post trigger:{}")
            ;

        });

        addSink("results", resultMap::putAll);
        onEvent("callback");

        MatcherAssert.assertThat(
                Arrays.asList("1.callback", "2.callback", "3.callback"),
                Matchers.containsInAnyOrder(resultMap.keySet().toArray()));
    }

    @Data
    public static class StringBatch {
        private List<String> strings = new ArrayList<>();
    }

    public static class TriggerRentrantNode implements EventProcessorContextListener {
        @Inject
        public Callback<Integer> callback;
        private EventProcessorContext currentContext;


        @Override
        public void currentContext(EventProcessorContext currentContext) {
            this.currentContext = currentContext;
        }

        @OnEventHandler
        public boolean stringEvent(String in) {
            StringBatch stringBatch = new StringBatch();
            stringBatch.strings.add("1." + in);
            stringBatch.strings.add("2." + in);
            stringBatch.strings.add("3." + in);
            currentContext.getStaticEventProcessor().onEvent(stringBatch);
            currentContext.getStaticEventProcessor().publishSignal(END_OF_BATCH);
            return true;
        }
    }

    public static class ReentrantCallBack implements EventProcessorContextListener {
        @Inject
        public Callback<Integer> callback;
        private EventProcessorContext currentContext;


        @Override
        public void currentContext(EventProcessorContext currentContext) {
            this.currentContext = currentContext;
        }


        @OnEventHandler
        public boolean stringEvent(String in) {
            boolean fireSignal = in.equalsIgnoreCase("callback");
            if (fireSignal) {
//                System.out.println("1. onEvent: " + in);
                currentContext.getStaticEventProcessor().publishSignal("triggerCallback", "flatMap");
            }
            return fireSignal;
        }

        @OnEventHandler(filterString = "triggerCallback")
        public boolean handleSignal(Signal<String> intSignal) {
//            System.out.println("2. re-entrant triggerCallback: " + intSignal);
            callback.fireCallback(100);
            currentContext.getStaticEventProcessor().publishSignal("signal", 200);
            return true;
        }

        @OnEventHandler(filterString = "signal")
        public boolean handleSignal(Signal.IntSignal intSignal) {
//            System.out.println("4. onSignal: " + intSignal);
            return true;
        }

        @OnTrigger
        public boolean triggered() {
//            System.out.println("3. triggered callback:" + callback.get());
            return true;
        }
    }
}
