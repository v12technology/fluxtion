package com.fluxtion.compiler.generation.reentrant;

import com.fluxtion.compiler.builder.stream.EventFlow;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReEntrantTest extends MultipleSepTargetInProcessTest {

    public ReEntrantTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void queueEventsInOrderTest() {
        List<String> results = new ArrayList<>();
        sep(c -> EventFlow.subscribe(String.class)
                .sink("queueEvent")
                .sink("results"));

        addSink("queueEvent", (String s) ->{
            if(!s.startsWith("rentrant-")){
                onEvent("rentrant-1-" + s);
            }else if(s.startsWith("rentrant-1-")){
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
}
