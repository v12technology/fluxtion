package com.fluxtion.compiler.generation.afterevent;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.AfterEvent;
import com.fluxtion.runtime.annotations.AfterTrigger;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
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

    public PostEventTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    private final static List<String> postInvocationTrace = new ArrayList<>();
    private final static AtomicInteger counter = new AtomicInteger();

    @Before
    public void beforeTest() {
        super.beforeTest();
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
                        "Child::afterEvent",
                        "Parent::afterEvent",
                        "Parent::newEvent",
                        "Child::onEvent",
                        "Child::eventComplete",
                        "Parent::eventComplete",
                        "Child::afterEvent",
                        "Parent::afterEvent"
                )
        ));
    }

    @Test
//    @Ignore("broken for in memory test - multiple calls are made to the afterTrigger child")
    public void singleOnEventComplete() {
        sep(c -> {
            c.addNode(new ChildWithEventHandler(new Parent()));
        });
        onEvent("helloWorld");
        //TODO FIXME for in memory
        //handler and trigger in same class with parent of same event type breaks
        assertThat(counter.intValue(), is(1));
    }


    @Data
    public static class Parent {
        @OnEventHandler
        public boolean newEvent(String in) {
            postInvocationTrace.add("Parent::newEvent");
            return true;
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
        public boolean onEvent() {
            postInvocationTrace.add("Child::onEvent");
            return true;
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
        public boolean newEvent(String in) {
            return true;
        }

        @OnTrigger
        public boolean onEvent() {
            postInvocationTrace.add("Child::onEvent");
            return true;
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
