package com.fluxtion.compiler.generation.bufferevent;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.*;
import com.fluxtion.runtime.node.NamedNode;
import lombok.Data;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.Date;

public class BufferEventGeneratedTest extends MultipleSepTargetInProcessTest {

    public BufferEventGeneratedTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void triggeredOnlyCbListTest() {
        sep(c -> {
            c.addNode(new Child(new EventHolder()));
        });
        Child child = getField("child");
        EventHolder eventHolder = getField("eventHolder");
        bufferEvent("test");
        bufferEvent("test");
        bufferEvent("test");
        //child
        MatcherAssert.assertThat(child.parentCount, CoreMatchers.is(3));
        MatcherAssert.assertThat(child.eventCount, CoreMatchers.is(0));
        MatcherAssert.assertThat(child.triggerCount, CoreMatchers.is(0));
        //parent
        MatcherAssert.assertThat(eventHolder.eventCount, CoreMatchers.is(3));
        MatcherAssert.assertThat(eventHolder.afterEventCount, CoreMatchers.is(0));
        MatcherAssert.assertThat(eventHolder.afterTriggerCount, CoreMatchers.is(0));


        bufferEvent(new Date());
        bufferEvent(new Date());
        //child
        MatcherAssert.assertThat(child.parentCount, CoreMatchers.is(3));
        MatcherAssert.assertThat(child.eventCount, CoreMatchers.is(2));
        MatcherAssert.assertThat(child.triggerCount, CoreMatchers.is(0));
        //parent
        MatcherAssert.assertThat(eventHolder.eventCount, CoreMatchers.is(3));
        MatcherAssert.assertThat(eventHolder.afterEventCount, CoreMatchers.is(0));
        MatcherAssert.assertThat(eventHolder.afterTriggerCount, CoreMatchers.is(0));


        triggerCalculation();
        //child
        MatcherAssert.assertThat(child.parentCount, CoreMatchers.is(3));
        MatcherAssert.assertThat(child.eventCount, CoreMatchers.is(2));
        MatcherAssert.assertThat(child.triggerCount, CoreMatchers.is(1));
        //parent
        MatcherAssert.assertThat(eventHolder.eventCount, CoreMatchers.is(3));
        MatcherAssert.assertThat(eventHolder.afterEventCount, CoreMatchers.is(1));
        MatcherAssert.assertThat(eventHolder.afterTriggerCount, CoreMatchers.is(1));
    }

    @Test
    public void noTriggerClassTest() {
        sep(c -> {
            c.addNode(new DateHandler());
        });
    }

    @Test
    public void noTriggerClassWithAfterTest() {
        sep(c -> {
            c.addNode(new EventHolder());
        });
        bufferEvent("test");
        bufferEvent("test");
        bufferEvent("test");
        EventHolder eventHolder = getField("eventHolder");
        MatcherAssert.assertThat(eventHolder.eventCount, CoreMatchers.is(3));
        MatcherAssert.assertThat(eventHolder.afterEventCount, CoreMatchers.is(0));
        MatcherAssert.assertThat(eventHolder.afterTriggerCount, CoreMatchers.is(0));

        triggerCalculation();
        MatcherAssert.assertThat(eventHolder.eventCount, CoreMatchers.is(3));
        MatcherAssert.assertThat(eventHolder.afterEventCount, CoreMatchers.is(1));
        MatcherAssert.assertThat(eventHolder.afterTriggerCount, CoreMatchers.is(1));
    }

    @Test
    public void testBufferedDispatch() {
        sep(c -> {
            c.addNode(new Combiner(), "combiner");
        });

        MatcherAssert.assertThat(getField("combiner", Combiner.class).triggerCount, CoreMatchers.is(0));
        bufferEvent("test");
        bufferEvent("dfsf");
        bufferEvent("ff");
        bufferEvent(12);
        MatcherAssert.assertThat(getField("combiner", Combiner.class).triggerCount, CoreMatchers.is(0));
        triggerCalculation();
        MatcherAssert.assertThat(getField("combiner", Combiner.class).triggerCount, CoreMatchers.is(1));
    }

    @Test
    public void testBufferedDispatchImpliedTriggerFromExternalEvent() {
        sep(c -> {
            c.addNode(new Combiner(), "combiner");
        });

        MatcherAssert.assertThat(getField("combiner", Combiner.class).triggerCount, CoreMatchers.is(0));
        bufferEvent("test");
        bufferEvent("dfsf");
        bufferEvent("ff");
        bufferEvent(12);
        MatcherAssert.assertThat(getField("combiner", Combiner.class).triggerCount, CoreMatchers.is(0));
        onEvent(14);
        MatcherAssert.assertThat(getField("combiner", Combiner.class).triggerCount, CoreMatchers.is(2));
    }


    public static class EventHolder implements NamedNode {

        private int eventCount;
        private int afterEventCount;
        private int afterTriggerCount;

        @OnEventHandler
        public boolean onString(String in) {
            eventCount++;
            return true;
        }

        @AfterEvent
        public void afterHolderEvent() {
            afterEventCount++;
        }

        @AfterTrigger
        public void afterHolderTrigger() {
            afterTriggerCount++;
        }

        @Initialise
        public void initHolder() {
        }

        @Override
        public String getName() {
            return "eventHolder";
        }
    }

    @Data
    public static class Child implements NamedNode {
        final EventHolder parent;
        private int eventCount = 0;
        private int parentCount = 0;
        private int triggerCount;

        @OnTrigger
        public boolean triggered() {
            triggerCount++;
            return true;
        }

        @OnEventHandler
        public void onDate(Date date) {
            eventCount++;
        }

        @OnParentUpdate
        public void onParent(EventHolder parent) {
            parentCount++;
        }

        @AfterEvent
        public void afterEvent() {
        }

        @Initialise
        public void initChild() {
        }

        @Override
        public String getName() {
            return "child";
        }
    }

    public static class DateHandler {
        @OnEventHandler
        public void onDate(Date date) {
        }
    }

    public static class IntegerHandler {
        @OnEventHandler
        public void onInteger(Integer date) {
        }
    }


    public static class StringHandler {
        @OnEventHandler
        public void onInteger(String date) {
        }
    }

    public static class Combiner {
        int triggerCount;
        private final StringHandler stringHandler;
        private final IntegerHandler integerHandler;

        public Combiner(StringHandler stringHandler, IntegerHandler integerHandler) {
            this.stringHandler = stringHandler;
            this.integerHandler = integerHandler;
        }

        public Combiner() {
            this(new StringHandler(), new IntegerHandler());
        }

        @OnTrigger
        public boolean triggered() {
            triggerCount++;
            return true;
        }

    }
}
