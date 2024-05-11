package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.IntSumFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import org.junit.Test;

import java.util.*;

import static com.fluxtion.compiler.builder.dataflow.DataFlow.subscribe;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class GroupBySlidingWindowTest extends MultipleSepTargetInProcessTest {


    public GroupBySlidingWindowTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void groupBySlidingTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(EventStreamBuildTest.KeyedData.class)
                .groupBySliding(EventStreamBuildTest.KeyedData::getId, EventStreamBuildTest.KeyedData::getAmount, IntSumFlowFunction::new, 100, 10)
                .map(GroupBy::toMap)
                .sink("map")
        );

        addSink("map", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        setTime(0);
        onEvent(new EventStreamBuildTest.KeyedData("A", 4000));

        tick(100);
        onEvent(new EventStreamBuildTest.KeyedData("A", 40));

        tick(300);
        onEvent(new EventStreamBuildTest.KeyedData("A", 40));
        onEvent(new EventStreamBuildTest.KeyedData("B", 100));

        tick(900);
        onEvent(new EventStreamBuildTest.KeyedData("C", 40));
        assertThat(results, is(expected));

        tick(1000);
        expected.put("A", 4080);
        expected.put("B", 100);
        expected.put("C", 40);
        assertThat(results, is(expected));

        tick(1230);
        expected.put("A", 40);
        expected.put("B", 100);
        expected.put("C", 40);
        assertThat(results, is(expected));

        tick(1290);
        assertThat(results, is(expected));

        tick(1300);
        expected.put("A", 40);
        expected.put("B", 100);
        expected.put("C", 40);
        assertThat(results, is(expected));

        tickDelta(10, 9);
        assertThat(results, is(expected));

        tick(1500);
        expected.put("C", 40);

        tick(1500);
        expected.put("C", 40);
        assertThat(results, is(expected));


        tick(1600);
        onEvent(new EventStreamBuildTest.KeyedData("B", 100));
        onEvent(new EventStreamBuildTest.KeyedData("C", 40));

        tick(1700);
        expected.put("B", 100);
        expected.put("C", 80);
        assertThat(results, is(expected));

        tick(2555);
        expected.put("B", 100);
        expected.put("C", 40);
        assertThat(results, is(expected));

        tick(2705);
        assertThat(results, is(expected));
    }

    @Test
    public void groupBySlidingNoTickTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(EventStreamBuildTest.KeyedData.class)
                .groupBySliding(
                        EventStreamBuildTest.KeyedData::getId, EventStreamBuildTest.KeyedData::getAmount,
                        IntSumFlowFunction::new,
                        100, 10)
                .map(GroupBy::toMap)
                .sink("map")
        );

        addSink("map", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        setTime(0);
        onEvent(new EventStreamBuildTest.KeyedData("A", 4000));

        setTime(100);
        onEvent(new EventStreamBuildTest.KeyedData("A", 40));

        setTime(300);
        onEvent(new EventStreamBuildTest.KeyedData("A", 40));
        onEvent(new EventStreamBuildTest.KeyedData("B", 100));

        setTime(900);
        onEvent(new EventStreamBuildTest.KeyedData("C", 40));
        assertThat(results, is(expected));

        setTime(1000);
        onEvent(new EventStreamBuildTest.KeyedData("C", 400));

        expected.put("A", 4080);
        expected.put("B", 100);
        expected.put("C", 40);
        assertThat(results, is(expected));

        setTime(1500);
        onEvent(new EventStreamBuildTest.KeyedData("B", 1000));
        expected.put("C", 440);
        assertThat(results, is(expected));

        setTime(2200);
        onEvent(new EventStreamBuildTest.KeyedData("A", 99));
        expected.put("B", 1000);
        assertThat(results, is(expected));

        tick(2500);
        expected.put("A", 99);
        expected.put("B", 1000);
        assertThat(results, is(expected));


        tick(2600);
        expected.put("A", 99);
        assertThat(results, is(expected));

        tick(3600);
        assertThat(results, is(expected));
    }

    @Test
    public void groupBySlidingTopNTest() {
        List<Map.Entry<String, Integer>> results = new ArrayList<>();
        List<Map.Entry<String, Integer>> expected = new ArrayList<>();

        sep(c -> subscribe(EventStreamBuildTest.KeyedData.class)
                .groupBySliding(EventStreamBuildTest.KeyedData::getId, EventStreamBuildTest.KeyedData::getAmount, IntSumFlowFunction::new, 100, 10)
                .map(Mappers.topNByValue(2))
                .sink("list")
        );

        addSink("list", (List<Map.Entry<String, Integer>> in) -> {
            results.clear();
            expected.clear();
            results.addAll(in);
        });

        setTime(0);
        onEvent(new EventStreamBuildTest.KeyedData("A", 400));
        onEvent(new EventStreamBuildTest.KeyedData("B", 1000));
        onEvent(new EventStreamBuildTest.KeyedData("C", 100));

        tick(500);
        onEvent(new EventStreamBuildTest.KeyedData("A", 40));
        onEvent(new EventStreamBuildTest.KeyedData("B", 100));
        onEvent(new EventStreamBuildTest.KeyedData("D", 2000));

        tick(700);
        onEvent(new EventStreamBuildTest.KeyedData("A", 500));
        onEvent(new EventStreamBuildTest.KeyedData("B", 100));

        tick(900);
        onEvent(new EventStreamBuildTest.KeyedData("C", 400));
        onEvent(new EventStreamBuildTest.KeyedData("B", 100));

        tick(1000);
        assertThat(results, contains(new AbstractMap.SimpleEntry<>("D", 2000), new AbstractMap.SimpleEntry<>("B", 1300)));

        tick(1101);
        assertThat(results, contains(new AbstractMap.SimpleEntry<>("D", 2000), new AbstractMap.SimpleEntry<>("A", 540)));

        tick(1600);
        assertThat(results, contains(new AbstractMap.SimpleEntry<>("A", 500), new AbstractMap.SimpleEntry<>("C", 400)));

        tick(1800);
        assertThat(results, contains(new AbstractMap.SimpleEntry<>("C", 400), new AbstractMap.SimpleEntry<>("B", 100)));

        tick(2000);
        assertTrue(results.isEmpty());
    }
}
