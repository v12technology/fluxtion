package com.fluxtion.compiler.generation.afterevent;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.AfterEvent;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.AfterTrigger;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PostEventTest extends MultipleSepTargetInProcessTest {

    public PostEventTest(boolean compiledSep) {
        super(compiledSep);
    }

    private final static List<String> postInvocationTrace = new ArrayList<>();
    private final static AtomicInteger counter = new AtomicInteger();

    @Before
    public void beforeTest() {
        postInvocationTrace.clear();
        counter.set(0);
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

    @Test
    public void singleOnEventComplete(){
        sep(c -> {
            c.addNode(new ChildWithEventHandler(new Parent()));
        });
        onEvent("helloWorld");
        assertThat(counter.intValue(), is(1));
    }


    @Data
    public static class Parent {
        @OnEventHandler
        public void newEvent(String in) {
            postInvocationTrace.add("Parent::newEvent");
        }

        @AfterTrigger
        public void eventComplete() {
            postInvocationTrace.add("Parent::eventComplete");
        }

        @AfterEvent
        public void afterEvent() {
            postInvocationTrace.add("Parent::afterEvent");
        }
    }

    @Data
   public static class Child {
        final Parent parent;

        @OnTrigger
        public void onEvent() {
            postInvocationTrace.add("Child::onEvent");
        }

        @AfterTrigger
        public void eventComplete() {
            postInvocationTrace.add("Child::eventComplete");
            counter.incrementAndGet();
        }

        @AfterEvent
        public void afterEvent() {
            postInvocationTrace.add("Child::afterEvent");
        }
    }

    @Data
    public static class ChildWithEventHandler {
        final Parent parent;

        @OnEventHandler
        public void newEvent(String in) {
        }

        @OnTrigger
        public void onEvent() {
            postInvocationTrace.add("Child::onEvent");
        }

        @AfterTrigger
        public void eventComplete() {
            postInvocationTrace.add("Child::eventComplete");
            counter.incrementAndGet();
        }

        @AfterEvent
        public void afterEvent() {
            postInvocationTrace.add("Child::afterEvent");
        }
    }

}
