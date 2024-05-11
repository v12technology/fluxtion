package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.IntSumFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.fluxtion.compiler.builder.dataflow.DataFlow.subscribe;
import static org.hamcrest.MatcherAssert.assertThat;

public class GroupByTumblingWindowTest extends MultipleSepTargetInProcessTest {
    public GroupByTumblingWindowTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void groupByTumblingTest() {
//        addAuditor();
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(EventStreamBuildTest.KeyedData.class)
                .groupByTumbling(EventStreamBuildTest.KeyedData::getId, EventStreamBuildTest.KeyedData::getAmount, IntSumFlowFunction::new, 100)
                .map(GroupBy::toMap)
                .sink("map"));

        addSink("map", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        setTime(0);
        onEvent(new EventStreamBuildTest.KeyedData("A", 40));

        tickDelta(25);
        onEvent(new EventStreamBuildTest.KeyedData("A", 40));

        tickDelta(25);
        onEvent(new EventStreamBuildTest.KeyedData("A", 40));
        onEvent(new EventStreamBuildTest.KeyedData("B", 100));

        tickDelta(25);
        onEvent(new EventStreamBuildTest.KeyedData("A", 40));
        onEvent(new EventStreamBuildTest.KeyedData("B", 100));

        tickDelta(25);//100
        expected.put("A", 160);
        expected.put("B", 200);
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new EventStreamBuildTest.KeyedData("B", 400));
        onEvent(new EventStreamBuildTest.KeyedData("C", 30));

        tickDelta(25);
        onEvent(new EventStreamBuildTest.KeyedData("B", 400));
        onEvent(new EventStreamBuildTest.KeyedData("C", 30));

        tickDelta(25);
        onEvent(new EventStreamBuildTest.KeyedData("C", 30));

        tickDelta(25);
        onEvent(new EventStreamBuildTest.KeyedData("C", 30));

        tickDelta(25);//100
        expected.put("B", 800);
        expected.put("C", 120);
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new EventStreamBuildTest.KeyedData("C", 80));

        tickDelta(25);
        onEvent(new EventStreamBuildTest.KeyedData("C", 80));

        tickDelta(50);
        onEvent(new EventStreamBuildTest.KeyedData("C", 80));

        tickDelta(25);//100
        expected.put("C", 240);
        assertThat(results, CoreMatchers.is(expected));

        tickDelta(200);
        assertThat(results, CoreMatchers.is(expected));
    }


    @Test
    public void groupByTumblingNoTickTest() {
//        addAuditor();
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(EventStreamBuildTest.KeyedData.class)
                .groupByTumbling(
                        EventStreamBuildTest.KeyedData::getId, EventStreamBuildTest.KeyedData::getAmount, //keys
                        IntSumFlowFunction::new, //aggregate function
                        100)//window size
                .map(GroupBy::toMap)
                .sink("map"));

        addSink("map", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        setTime(0);
        onEvent(new EventStreamBuildTest.KeyedData("A", 40));

        setTime(25);
        onEvent(new EventStreamBuildTest.KeyedData("A", 40));

        setTime(50);
        onEvent(new EventStreamBuildTest.KeyedData("A", 40));
        onEvent(new EventStreamBuildTest.KeyedData("B", 100));

        setTime(75);
        onEvent(new EventStreamBuildTest.KeyedData("A", 40));
        onEvent(new EventStreamBuildTest.KeyedData("B", 100));

        setTime(100);//100
        onEvent(new EventStreamBuildTest.KeyedData("B", 400));
        onEvent(new EventStreamBuildTest.KeyedData("C", 30));

        expected.put("A", 160);
        expected.put("B", 200);
        assertThat(results, CoreMatchers.is(expected));


        setTime(125);
        onEvent(new EventStreamBuildTest.KeyedData("B", 400));
        onEvent(new EventStreamBuildTest.KeyedData("C", 30));

        setTime(150);
        onEvent(new EventStreamBuildTest.KeyedData("C", 30));

        setTime(175);
        onEvent(new EventStreamBuildTest.KeyedData("C", 30));

        setTime(200);
        onEvent(new EventStreamBuildTest.KeyedData("C", 80));

        expected.put("B", 800);
        expected.put("C", 120);
        assertThat(results, CoreMatchers.is(expected));

        setTime(225);
        onEvent(new EventStreamBuildTest.KeyedData("C", 80));

        setTime(250);
        onEvent(new EventStreamBuildTest.KeyedData("C", 80));

        setTime(250);
        onEvent(new EventStreamBuildTest.KeyedData("C", 80));

        tick(300);

        expected.put("C", 320);
        assertThat(results, CoreMatchers.is(expected));

        tick(800);
        assertThat(results, CoreMatchers.is(expected));
    }
}
