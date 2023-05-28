package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import org.junit.Test;

import java.util.concurrent.atomic.LongAdder;

import static com.fluxtion.compiler.builder.dataflow.DataFlow.subscribe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MergeTest extends MultipleSepTargetInProcessTest {
    public MergeTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void mergeTest() {
        LongAdder adder = new LongAdder();
        sep(c -> subscribe(Long.class)
                .merge(subscribe(String.class).map(EventStreamBuildTest::parseLong))
                .sink("integers"));
        addSink("integers", adder::add);
        onEvent(200L);
        onEvent("300");
        assertThat(adder.intValue(), is(500));
    }

    @Test
    public void mergeTestStaticMethod() {
        LongAdder adder = new LongAdder();
        sep(c ->
                DataFlow.merge(
                                subscribe(Long.class),
                                subscribe(String.class).map(EventStreamBuildTest::parseLong)
                        )
                        .sink("integers"));
        addSink("integers", adder::add);
        onEvent(200L);
        onEvent("300");
        assertThat(adder.intValue(), is(500));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mergeTestVarArgStaticMethod() {
        LongAdder adder = new LongAdder();
        sep(c ->
                DataFlow.merge(
                                subscribe(Long.class),
                                subscribe(String.class).map(EventStreamBuildTest::parseLong),
                                subscribe(Integer.class).map(Integer::longValue)
                        )
                        .sink("integers"));
        addSink("integers", adder::add);
        onEvent(200L);
        onEvent("300");
        onEvent(500);
        assertThat(adder.intValue(), is(1_000));
    }
}
