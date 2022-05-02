package com.fluxtion.compiler.generation.callback;

import com.fluxtion.compiler.builder.stream.EventFlow;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.callback.EventDispatcher;
import com.fluxtion.runtime.stream.helpers.Mappers;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EventDispatchTest extends MultipleSepTargetInProcessTest {

    public EventDispatchTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void redispatchSingleEventTest() {
        sep(c -> {
            c.addNode(new Redispatcher());
            EventFlow.subscribe(MyEvent.class)
                    .mapToInt(Mappers.count())
                    .id("count");
        });
        onEvent("test");
        onEvent("test");
        onEvent("ignore");
        onEvent("test");
        assertThat(getStreamed("count"), is(3));
    }

    @Test
    public void redispatchMultipleEventTest() {
        sep(c -> {
            c.addNode(new Redispatcher());
            EventFlow.subscribe(MyEvent.class)
                    .mapToInt(Mappers.count())
                    .id("count");
        });
        onEvent("repeat");
        onEvent("ignore");
        assertThat(getStreamed("count"), is(3));
        onEvent("test");
        assertThat(getStreamed("count"), is(4));
    }

    @Test
    public void redispatchFromStreamTest() {
        sep(c -> {
            EventFlow.subscribe(String.class)
                    .map(EventDispatchTest::toMyEvent)
                    .processAsNewGraphEvent();

            EventFlow.subscribe(MyEvent.class)
                    .mapToInt(Mappers.count())
                    .id("count");
        });
        onEvent("test");
        onEvent("test");
        assertThat(getStreamed("count"), is(2));
        onEvent("test");
        assertThat(getStreamed("count"), is(3));
    }

    @Test
    public void redispatchMultipleEventFromStreamTest(){
        sep(c -> {
            EventFlow.subscribe(String.class)
                    .flatMap(EventDispatchTest::csvToIterable)
                    .map(EventDispatchTest::toMyEvent)
                    .processAsNewGraphEvent();

            EventFlow.subscribe(MyEvent.class)
                    .mapToInt(Mappers.count())
                    .id("count");
        });
        onEvent("1,2,3");
        onEvent("4");
        assertThat(getStreamed("count"), is(4));
        onEvent("5,six,seven,8");
        assertThat(getStreamed("count"), is(8));
    }

    public static class MyEvent {
    }

    public static class Redispatcher {
        @Inject
        @NoTriggerReference
        public EventDispatcher eventDispatcher;

        @OnEventHandler
        public void handleString(String s) {
            if (s.startsWith("test")) {
                eventDispatcher.processEvent(new MyEvent());
            } else if (s.startsWith("repeat")) {
                eventDispatcher.processEvents(Arrays.asList(new MyEvent(), new MyEvent(), new MyEvent()));
            }
        }
    }

    public static MyEvent toMyEvent(String in) {
        return new MyEvent();
    }

    public static Iterable<String> csvToIterable(String input){
        return Arrays.asList(input.split(","));
    }

}
