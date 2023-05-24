package com.fluxtion.compiler.generation.reentrant;

import com.fluxtion.compiler.builder.dataflow.DataFlow;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.Start;
import com.fluxtion.runtime.annotations.Stop;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.callback.EventDispatcher;
import com.fluxtion.runtime.output.SinkPublisher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReEntrantTest extends MultipleSepTargetInProcessTest {

    public ReEntrantTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void queueEventsInOrderTest() {
        List<String> results = new ArrayList<>();
        sep(c -> DataFlow.subscribe(String.class)
                .sink("queueEvent")
                .sink("results"));

        addSink("queueEvent", (String s) -> {
            if (!s.startsWith("rentrant-")) {
                onEvent("rentrant-1-" + s);
            } else if (s.startsWith("rentrant-1-")) {
                onEvent("rentrant-2-" + s);
            }
        });

        addSink("results", (String s) -> results.add(s));

        onEvent("first");
        onEvent("second");
        onEvent("third");

        List<String> expected = Arrays.asList(
                "first", "rentrant-1-first", "rentrant-2-rentrant-1-first",
                "second", "rentrant-1-second", "rentrant-2-rentrant-1-second",
                "third", "rentrant-1-third", "rentrant-2-rentrant-1-third");

        assertThat(results, is(expected));
    }

    @Test
    public void queueStartReentrantTest() {
        List<String> results = new ArrayList<>();
        sep(c -> {
            c.addNode(new StartClass());
        });
        addSink("lifecycleSink", (String s) -> results.add(s));
        onEvent("event1");
        start();
        stop();

        List<String> expected = Arrays.asList(
                "event1",
                "started", "reentrant-start",
                "stopped", "reentrant-stop"
        );

        assertThat(results, is(expected));
    }

    public static class StartClass {
        public SinkPublisher<String> publisher = new SinkPublisher<>("lifecycleSink");
        @Inject
        public EventDispatcher dispatcher;

        @Start
        public void start() {
            dispatcher.processAsNewEventCycle("reentrant-start");
            publisher.publish("started");
        }

        @Stop
        public void stop() {
            dispatcher.processAsNewEventCycle("reentrant-stop");
            publisher.publish("stopped");
        }

        @OnEventHandler
        public boolean stringUpdate(String in) {
            publisher.publish(in);
            return false;
        }

    }
}
