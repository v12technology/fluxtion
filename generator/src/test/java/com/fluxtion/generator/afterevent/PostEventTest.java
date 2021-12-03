package com.fluxtion.generator.afterevent;

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.generator.util.InMemoryOnlySepTest;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PostEventTest extends InMemoryOnlySepTest {

    public PostEventTest(boolean compiledSep) {
        super(compiledSep);
    }

    private final List<String> postInvocationTrace = new ArrayList<>();

    @Before
    public void beforeTest() {
        postInvocationTrace.clear();
    }

    @Test
    public void afterEventTest() {
        sep(c -> {
            c.addNode(new Child(new Parent()));
        });
        onEvent("helloWorld");
        assertThat(postInvocationTrace, is(
                Arrays.asList(
                        "Parent::newEvent",
                        "Child::onEvent",
                        "Parent::eventComplete",
                        "Child::eventComplete",
                        "Child::afterEvent",
                        "Parent::afterEvent"
                )
        ));
    }


    @Data
    class Parent {
        @EventHandler
        public void newEvent(String in) {
            postInvocationTrace.add("Parent::newEvent");
        }

        @OnEventComplete
        public void eventComplete() {
            postInvocationTrace.add("Parent::eventComplete");
        }

        @AfterEvent
        public void afterEvent() {
            postInvocationTrace.add("Parent::afterEvent");
        }
    }

    @Data
    class Child {
        final Parent parent;

        @OnEvent
        public void onEvent() {
            postInvocationTrace.add("Child::onEvent");
        }

        @OnEventComplete
        public void eventComplete() {
            postInvocationTrace.add("Child::eventComplete");
        }

        @AfterEvent
        public void afterEvent() {
            postInvocationTrace.add("Child::afterEvent");
        }
    }

}
