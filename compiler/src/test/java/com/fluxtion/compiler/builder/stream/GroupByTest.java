package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.builder.stream.EventStreamBuildTest.KeyedData;
import com.fluxtion.compiler.builder.stream.EventStreamBuildTest.MergedType;
import com.fluxtion.compiler.builder.stream.EventStreamBuildTest.MyIntFilter;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIntSum;
import com.fluxtion.runtime.stream.groupby.GroupBy;
import com.fluxtion.runtime.stream.groupby.GroupBy.KeyValue;
import com.fluxtion.runtime.stream.groupby.GroupByStreamed;
import com.fluxtion.runtime.stream.helpers.Mappers;
import lombok.Value;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class GroupByTest extends MultipleSepTargetInProcessTest {

    public GroupByTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void groupByIdentityTest() {
        Map<String, Data> expected = new HashMap<>();
        sep(c -> {
            EventFlow.subscribe(Data.class)
                    .groupBy(Data::getName)
                    .map(GroupBy::map).id("results");
        });

        onEvent(new Data("A", 25));
        onEvent(new Data("A", 50));

        Map<String, Data> actual = getStreamed("results");
        expected.put("A", new Data("A", 50));
        MatcherAssert.assertThat(actual, is(expected));

        onEvent(new Data("A", 10));
        onEvent(new Data("B", 11));
        expected.put("A", new Data("A", 10));
        expected.put("B", new Data("B", 11));
        MatcherAssert.assertThat(actual, is(expected));
    }


    @Test
    public void groupByAsListIdentityTest() {
        Map<String, List<Data>> expected = new HashMap<>();
        sep(c -> {
            EventFlow.subscribe(Data.class)
                    .groupByAsList(Data::getName)
                    .map(GroupBy::map).id("results");
        });

        onEvent(new Data("A", 25));
        onEvent(new Data("A", 50));

        Stream.of(new Data("A", 50)).collect(Collectors.toList());
        Map<String, Data> actual = getStreamed("results");
        expected.put("A", Stream.of(
                new Data("A", 25),
                new Data("A", 50)
        ).collect(Collectors.toList()));
        MatcherAssert.assertThat(actual, is(expected));

        onEvent(new Data("B", 11));
        expected.put("B", Stream.of(
                new Data("B", 11)
        ).collect(Collectors.toList()));
        MatcherAssert.assertThat(actual, is(expected));
    }

    @Test
    public void groupByAsListMaxSizeIdentityTest() {
        Map<String, List<Data>> expected = new HashMap<>();
        sep(c -> {
            EventFlow.subscribe(Data.class)
                    .groupByAsList(Data::getName, 3)
                    .map(GroupBy::map).id("results");
        });

        onEvent(new Data("A", 25));
        onEvent(new Data("A", 50));
        onEvent(new Data("A", 60));
        onEvent(new Data("A", 70));

        Stream.of(new Data("A", 50)).collect(Collectors.toList());
        Map<String, Data> actual = getStreamed("results");
        expected.put("A", Stream.of(
                new Data("A", 50),
                new Data("A", 60),
                new Data("A", 70)
        ).collect(Collectors.toList()));
        MatcherAssert.assertThat(actual, is(expected));

        onEvent(new Data("B", 11));
        expected.put("B", Stream.of(
                new Data("B", 11)
        ).collect(Collectors.toList()));
        MatcherAssert.assertThat(actual, is(expected));
    }

    @Test
    public void groupByTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();
        sep(c -> subscribe(KeyedData.class)
                .groupBy(KeyedData::getId, KeyedData::getAmount, AggregateIntSum::new)
                .map(GroupByStreamed::keyValue)
                .sink("keyValue"));

        addSink("keyValue", (KeyValue<String, Integer> kv) -> {
            results.clear();
            expected.clear();
            results.put(kv.getKey(), kv.getValue());
        });
        onEvent(new KeyedData("A", 22));
        expected.put("A", 22);
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new KeyedData("B", 250));
        expected.put("B", 250);
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new KeyedData("B", 140));
        expected.put("B", 390);
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new KeyedData("A", 22));
        expected.put("A", 44);
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new KeyedData("A", 22));
        expected.put("A", 66);
        assertThat(results, CoreMatchers.is(expected));
    }


    @Test
    public void groupByTumblingTest() {
//        addAuditor();
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(KeyedData.class)
                .groupByTumbling(KeyedData::getId, KeyedData::getAmount, AggregateIntSum::new, 100)
                .map(GroupBy::map)
                .sink("map"));

        addSink("map", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        setTime(0);
        onEvent(new KeyedData("A", 40));

        tickDelta(25);
        onEvent(new KeyedData("A", 40));

        tickDelta(25);
        onEvent(new KeyedData("A", 40));
        onEvent(new KeyedData("B", 100));

        tickDelta(25);
        onEvent(new KeyedData("A", 40));
        onEvent(new KeyedData("B", 100));

        tickDelta(25);//100
        expected.put("A", 160);
        expected.put("B", 200);
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new KeyedData("B", 400));
        onEvent(new KeyedData("C", 30));

        tickDelta(25);
        onEvent(new KeyedData("B", 400));
        onEvent(new KeyedData("C", 30));

        tickDelta(25);
        onEvent(new KeyedData("C", 30));

        tickDelta(25);
        onEvent(new KeyedData("C", 30));

        tickDelta(25);//100
        expected.put("B", 800);
        expected.put("C", 120);
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new KeyedData("C", 80));

        tickDelta(25);
        onEvent(new KeyedData("C", 80));

        tickDelta(50);
        onEvent(new KeyedData("C", 80));

        tickDelta(25);//100
        expected.put("C", 240);
        assertThat(results, CoreMatchers.is(expected));

        tickDelta(200);
        assertThat(results, CoreMatchers.is(expected));
    }

    @Test
    public void mapGroupByValuesTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> {
            subscribe(KeyedData.class)
                    .groupBy(KeyedData::getId, KeyedData::getAmount)
                    .map(GroupByFunction.mapValues(EventStreamBuildTest::doubleInt))
                    .map(GroupBy::map)
                    .sink("keyValue");
        });

        addSink("keyValue", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        onEvent(new KeyedData("A", 400));
        onEvent(new KeyedData("B", 233));
        onEvent(new KeyedData("B", 1000));
        onEvent(new KeyedData("B", 2000));
        onEvent(new KeyedData("C", 1000));
        onEvent(new KeyedData("B", 50));

        expected.put("A", 800);
        expected.put("B", 100);
        expected.put("C", 2000);
        assertThat(results, CoreMatchers.is(expected));
    }

    @Test
    public void mapGroupByKeysTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> {
            subscribe(KeyedData.class)
                    .groupBy(KeyedData::getId, KeyedData::getAmount)
                    .map(GroupByFunction.mapKeys(EventStreamBuildTest::toUpperCase))
                    .map(GroupBy::map)
                    .sink("keyValue");
        });

        addSink("keyValue", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        onEvent(new KeyedData("a", 400));
        onEvent(new KeyedData("b", 233));
        onEvent(new KeyedData("b", 1000));
        onEvent(new KeyedData("b", 2000));
        onEvent(new KeyedData("c", 1000));
        onEvent(new KeyedData("b", 50));

        expected.put("A", 400);
        expected.put("B", 50);
        expected.put("C", 1000);
        assertThat(results, CoreMatchers.is(expected));
    }


    @Test
    public void mapGroupByEntriesTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> {
            subscribe(KeyedData.class)
                    .groupBy(KeyedData::getId)
                    .map(GroupByFunction.mapEntry(GroupByTest::mapKeyData))
                    .map(GroupBy::map)
                    .sink("keyValue");
        });

        addSink("keyValue", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        onEvent(new KeyedData("a", 400));
        onEvent(new KeyedData("b", 233));
        onEvent(new KeyedData("b", 1000));
        onEvent(new KeyedData("b", 2000));
        onEvent(new KeyedData("c", 1000));
        onEvent(new KeyedData("b", 50));

        expected.put("A", 800);
        expected.put("B", 100);
        expected.put("C", 2000);
        assertThat(results, CoreMatchers.is(expected));
    }

    public static Map.Entry<String, Integer> mapKeyData(Map.Entry<String, KeyedData> input) {
        return new AbstractMap.SimpleEntry<>(
                input.getKey().toUpperCase(), input.getValue().getAmount() * 2);
    }

    @Test
    public void filterGroupByTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> {
            EventStreamBuilder<Map<String, Integer>> obj = subscribe(KeyedData.class)
                    .groupBy(KeyedData::getId, KeyedData::getAmount)
                    .map(GroupByFunction.filterValues(new MyIntFilter(500)::gt))
                    .map(GroupBy::map)
                    .sink("keyValue");
        });

        addSink("keyValue", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        onEvent(new KeyedData("A", 400));
        onEvent(new KeyedData("B", 233));
        onEvent(new KeyedData("B", 1000));
        onEvent(new KeyedData("B", 2000));
        onEvent(new KeyedData("C", 1000));
        onEvent(new KeyedData("B", 50));
        onEvent(new KeyedData("A", 1400));

        expected.put("A", 1400);
        expected.put("C", 1000);
        assertThat(results, CoreMatchers.is(expected));
    }

    @Test
    public void joinGroupByTest() {
        Map<String, MergedType> results = new HashMap<>();
        Map<String, MergedType> expected = new HashMap<>();

        sep(c -> {
            EventStreamBuilder<GroupByStreamed<String, String>> stringGroupBy = subscribe(String.class)
                    .groupBy(String::toString, Mappers::identity);

            EventStreamBuilder<GroupByStreamed<String, Integer>> keyedGroupBy = subscribe(KeyedData.class)
                    .groupBy(KeyedData::getId, KeyedData::getAmount);

            GroupByFunction.innerJoinStreams(keyedGroupBy, stringGroupBy)
                    .map(GroupByFunction.mapValues(EventStreamBuildTest::mergedTypefromTuple))
                    .map(GroupBy::map)
                    .sink("merged");
        });

        addSink("merged", (Map<String, MergedType> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        onEvent(new KeyedData("A", 400));
        onEvent(new KeyedData("B", 233));
        onEvent("A");

        expected.put("A", new MergedType(400, "A"));
        assertThat(results, CoreMatchers.is(expected));
    }


    @Value
    public static class Data {
        String name;
        int value;
    }

}
