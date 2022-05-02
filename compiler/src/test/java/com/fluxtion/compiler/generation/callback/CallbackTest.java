package com.fluxtion.compiler.generation.callback;

import com.fluxtion.compiler.builder.stream.EventFlow;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.callback.Callback;
import com.fluxtion.runtime.stream.helpers.Mappers;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CallbackTest extends MultipleSepTargetInProcessTest {
    public CallbackTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void callbackNoDataSingleItemTest() {
        sep(c -> {
            c.addNode(new MyCallbackDependency(), "cb_1");
        });
        MyCallbackDependency cb = getField("cb_1");
        assertThat(cb.count, is(0));
        onEvent("no callback");
        assertThat(cb.count, is(0));
        onEvent("callback");
        assertThat(cb.count, is(1));
    }

    @Test
    public void callbackWithDataSingleItemTest() {
        sep(c -> {
            c.addNode(new MyCallbackDependencyWithData(), "cb_1");
        });
        MyCallbackDependencyWithData cb = getField("cb_1");
        assertNull(cb.data);
        onEvent("no callback");
        assertNull(cb.data);
        onEvent("callback");
        assertThat(cb.data, is("call back data"));
    }

    @Test
    public void callbackWithIteratorTest() {
        sep(c -> {
            c.addNode(new MyCallbackDependencyWithIterator(), "cb_1");
            EventFlow.subscribeToNode(new MyCallbackDependencyWithIterator())
                    .mapToInt(Mappers.count()).id("count");
        });
        onEvent("no callback");
        assertThat(getStreamed("count"), is(0));
        onEvent("callback");
        assertThat(getStreamed("count"), is(8));
    }

    @Test
    public void callbackWithIteratorOfIteratorTest() {
        sep(c -> {
            EventFlow.subscribeToNode(new SplitChars())
                    .mapToInt(SplitChars::getValue)
                    .map(Mappers.cumSumInt()).id("sum");
        });
        onEvent("123,45678,9");
        assertThat(getStreamed("sum"), is(45));
    }

    public static class MyCallbackDependency {
        @Inject
        public Callback callback;
        int count;

        @OnEventHandler
        public void stringEvent(String in) {
            if (in.equalsIgnoreCase("callback")) {
                callback.fireCallback();
            }
        }

        @OnTrigger
        public void triggered() {
            count++;
        }
    }

    public static class MyCallbackDependencyWithData {
        @Inject
        public Callback<String> callback;
        public String data;

        @OnEventHandler
        public void stringEvent(String in) {
            if (in.equalsIgnoreCase("callback")) {
                callback.fireCallback("call back data");
            }
        }

        @OnTrigger
        public void triggered() {
            data = callback.get();
        }
    }

    public static class MyCallbackDependencyWithIterator {
        @Inject
        public Callback<String> callback;

        @OnEventHandler(propagate = false)
        public void stringEvent(String in) {
            if (in.equalsIgnoreCase("callback")) {
                callback.fireCallback(in.chars().mapToObj(i -> "" + (char)i).iterator());
            }
        }

        @OnTrigger
        public void triggered() {}
    }

    public static class SplitCsvString {
        @Inject
        public Callback<String> callback;
        public String element;

        @OnEventHandler(propagate = false)
        public void stringEvent(String in) {
            callback.fireCallback(Arrays.asList(in.split(",")).iterator());
        }

        @OnTrigger
        public void triggered() {
            element = callback.get();
        }
    }

    public static class SplitChars {
        @Inject
        public Callback<Integer> callback;
        @NoTriggerReference
        public SplitCsvString parent = new SplitCsvString();

        private int value;

        @OnParentUpdate
        public void stringSplit(SplitCsvString parent){
            callback.fireCallback(
                parent.element.chars().mapToObj(i -> "" + (char)i).map(Integer::parseInt).collect(Collectors.toList()).iterator()
            );
        }

        @OnTrigger
        public void triggered() {
            value = callback.get();
        }

        public int getValue() {
            return value;
        }
    }
}
