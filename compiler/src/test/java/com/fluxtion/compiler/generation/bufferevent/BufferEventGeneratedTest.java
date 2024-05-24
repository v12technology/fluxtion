package com.fluxtion.compiler.generation.bufferevent;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.*;
import com.fluxtion.runtime.node.NamedNode;
import lombok.Data;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.Date;

public class BufferEventGeneratedTest extends MultipleSepTargetInProcessTest {

    public BufferEventGeneratedTest(SepTestConfig compiledSep) {
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
        MatcherAssert.assertThat(eventHolder.afterEventCount, CoreMatchers.is(1));
        MatcherAssert.assertThat(eventHolder.afterTriggerCount, CoreMatchers.is(0));


        bufferEvent(new Date());
        bufferEvent(new Date());
        //child
        MatcherAssert.assertThat(child.parentCount, CoreMatchers.is(3));
        MatcherAssert.assertThat(child.eventCount, CoreMatchers.is(2));
        MatcherAssert.assertThat(child.triggerCount, CoreMatchers.is(0));
        //parent
        MatcherAssert.assertThat(eventHolder.eventCount, CoreMatchers.is(3));
        MatcherAssert.assertThat(eventHolder.afterEventCount, CoreMatchers.is(1));
        MatcherAssert.assertThat(eventHolder.afterTriggerCount, CoreMatchers.is(0));


        triggerCalculation();
        //child
        MatcherAssert.assertThat(child.parentCount, CoreMatchers.is(3));
        MatcherAssert.assertThat(child.eventCount, CoreMatchers.is(2));
        MatcherAssert.assertThat(child.triggerCount, CoreMatchers.is(1));
        //parent
        MatcherAssert.assertThat(eventHolder.eventCount, CoreMatchers.is(3));
        MatcherAssert.assertThat(eventHolder.afterEventCount, CoreMatchers.is(2));
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
        MatcherAssert.assertThat(eventHolder.afterEventCount, CoreMatchers.is(1));
        MatcherAssert.assertThat(eventHolder.afterTriggerCount, CoreMatchers.is(0));

        triggerCalculation();
        MatcherAssert.assertThat(eventHolder.eventCount, CoreMatchers.is(3));
        MatcherAssert.assertThat(eventHolder.afterEventCount, CoreMatchers.is(2));
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

    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedBufferTest() {
        sep(c -> {
            c.addNode(new EventHolder());
            c.setSupportBufferAndTrigger(false);
        });

        bufferEvent("test");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedTriggerTest() {
        sep(c -> {
            c.addNode(new EventHolder());
            c.setSupportBufferAndTrigger(false);
        });

        triggerCalculation();
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
        public boolean onDate(Date date) {
            eventCount++;
            return true;
        }

        @OnParentUpdate
        public boolean onParent(EventHolder parent) {
            parentCount++;
            return true;
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
        public boolean onDate(Date date) {
            return true;
        }
    }

    public static class IntegerHandler {
        @OnEventHandler
        public boolean onInteger(Integer date) {
            return true;
        }
    }


    public static class StringHandler {
        @OnEventHandler
        public boolean onInteger(String date) {
            return true;
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
