package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.builder.dataflow.EventStreamBuildTest.KeyedData;
import com.fluxtion.compiler.builder.dataflow.EventStreamBuildTest.MergedType;
import com.fluxtion.compiler.builder.dataflow.EventStreamBuildTest.MyIntFilter;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.FluxtionIgnore;
import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.AbstractAggregateFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.DoubleSumFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.IntSumFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import com.fluxtion.runtime.dataflow.groupby.GroupBy.KeyValue;
import com.fluxtion.runtime.dataflow.groupby.GroupByKey;
import com.fluxtion.runtime.dataflow.helpers.Aggregates;
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import com.fluxtion.runtime.dataflow.helpers.Tuples;
import lombok.Getter;
import lombok.Value;
import lombok.val;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fluxtion.compiler.builder.dataflow.DataFlow.*;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;


public class GroupByTest extends MultipleSepTargetInProcessTest {

    public GroupByTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void groupByIdentityTest() {
        Map<String, Data> expected = new HashMap<>();
        sep(c -> {
            DataFlow.subscribe(Data.class)
                    .groupBy(Data::getName)
                    .map(GroupBy::toMap).id("results");
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
    public void groupByCompoundKeyIdentityTest() {
        Map<GroupByKey<Data>, Data> expected = new HashMap<>();
        sep(c -> DataFlow
                .groupByFields(Data::getName, Data::getValue)
                .map(GroupBy::toMap)
                .id("results"));

        Data data_A_25 = new Data("A", 25);
        Data data_A_50 = new Data("A", 50);
        onEvent(data_A_25);
        onEvent(data_A_50);

        GroupByKey<Data> key = new GroupByKey<>(Data::getName, Data::getValue);

        expected.put(key.toKey(data_A_25), new Data("A", 25));
        expected.put(key.toKey(data_A_50), new Data("A", 50));
        Map<GroupByKey<Data>, Data> actual = getStreamed("results");
        MatcherAssert.assertThat(actual, is(expected));

        Data data_A_10 = new Data("A", 10);
        Data data_B_11 = new Data("B", 11);
        onEvent(data_A_10);
        onEvent(data_B_11);

        expected.put(key.toKey(data_A_10), new Data("A", 10));
        expected.put(key.toKey(data_B_11), new Data("B", 11));
        expected.put(key.toKey(data_A_25), new Data("A", 25));
        expected.put(key.toKey(data_A_50), new Data("A", 50));

        MatcherAssert.assertThat(actual, is(expected));
    }


    @Test
    public void groupByAsListIdentityTest() {
        Map<String, List<Data>> expected = new HashMap<>();
        sep(c -> {
            DataFlow.subscribe(Data.class)
                    .groupByToList(Data::getName)
                    .map(GroupBy::toMap).id("results");
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
    public void dataFlowGroupByAsListIdentityTest() {
        Map<String, List<Data>> expected = new HashMap<>();
        sep(c -> {
            DataFlow.groupByToList(Data::getName)
                    .map(GroupBy::toMap).id("results");
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
    public void dataFlowGroupByAsListMultiFieldKeyIdentityTest() {
        Map<GroupByKey<Data>, List<Data>> expected = new HashMap<>();
        sep(c -> {
            DataFlow.groupByToList(Data::getName, Data::getValue)
                    .map(GroupBy::toMap).id("results");
        });

        Data data_A_25 = new Data("A", 25);
        Data data_A_50 = new Data("A", 50);
        onEvent(data_A_25);
        onEvent(data_A_50);
        onEvent(data_A_50);

        GroupByKey<Data> key = new GroupByKey<>(Data::getName, Data::getValue);
        expected.put(key.toKey(data_A_25), Stream.of(
                new Data("A", 25)
        ).collect(Collectors.toList()));
        expected.put(key.toKey(data_A_50), Stream.of(
                new Data("A", 50),
                new Data("A", 50)
        ).collect(Collectors.toList()));

        Map<String, Data> actual = getStreamed("results");
        MatcherAssert.assertThat(actual, is(expected));
    }

    @Test
    public void dataFlowGroupBySet() {
        Map<String, Set<Data>> expected = new HashMap<>();
        sep(c -> {
            DataFlow.groupByToSet(Data::getName)
                    .map(GroupBy::toMap).id("results");
        });
        onEvent(new Data("A", 25));
        onEvent(new Data("A", 50));
        onEvent(new Data("A", 32));
        onEvent(new Data("A", 50));
        //
        Map<String, Data> actual = getStreamed("results");
        expected.put("A", Stream.of(
                new Data("A", 25),
                new Data("A", 50),
                new Data("A", 32)
        ).collect(Collectors.toSet()));
        MatcherAssert.assertThat(actual, is(expected));


        onEvent(new Data("B", 15));
        onEvent(new Data("B", 15));
        expected.put("B", Stream.of(
                new Data("B", 15)
        ).collect(Collectors.toSet()));
        MatcherAssert.assertThat(actual, is(expected));
    }

    @Test
    public void dataFlowGroupByAsASetMultiFieldKeyIdentityTest() {
        Map<GroupByKey<Data>, Set<Data>> expected = new HashMap<>();
        sep(c -> {
            DataFlow.groupByToSet(Data::getName, Data::getValue)
                    .map(GroupBy::toMap).id("results");
        });

        Data data_A_25 = new Data("A", 25);
        Data data_A_50 = new Data("A", 50);
        onEvent(data_A_25);
        onEvent(data_A_25);
        onEvent(data_A_25);
        onEvent(data_A_50);

        GroupByKey<Data> key = new GroupByKey<>(Data::getName, Data::getValue);
        expected.put(key.toKey(data_A_25), Stream.of(
                new Data("A", 25)
        ).collect(Collectors.toSet()));
        expected.put(key.toKey(data_A_50), Stream.of(
                new Data("A", 50)
        ).collect(Collectors.toSet()));

        Map<String, Data> actual = getStreamed("results");
        MatcherAssert.assertThat(actual, is(expected));
    }

    @Test
    public void groupByAsListMaxSizeIdentityTest() {
        Map<String, List<Data>> expected = new HashMap<>();
        sep(c -> {
            DataFlow.subscribe(Data.class)
                    .groupByToList(Data::getName, 3)
                    .map(GroupBy::toMap).id("results");
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
        sep(c -> DataFlow.subscribe(KeyedData.class)
                .groupBy(KeyedData::getId, KeyedData::getAmount, IntSumFlowFunction::new)
                .map(GroupBy::lastKeyValue)
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

    @lombok.Data
    public static class IntegerMap {
        private Map<String, Integer> map = new HashMap<>();
    }

    @Test
    public void groupByFromMapTest() {
        Map<String, Integer> expected = new HashMap<>();
        sep(c ->
                DataFlow.groupByFromMap(IntegerMap::getMap)
                        .map(GroupBy::toMap)
                        .id("results"));

        IntegerMap integerMap = new IntegerMap();
        integerMap.getMap().put("A", 1);
        integerMap.getMap().put("B", 2);
        onEvent(integerMap);

        Map<String, Data> actual = getStreamed("results");
        expected.put("A", 1);
        expected.put("B", 2);
        MatcherAssert.assertThat(actual, is(expected));

        integerMap.getMap().put("C", 3);
        onEvent(integerMap);
        expected.put("C", 3);
        MatcherAssert.assertThat(actual, is(expected));
    }

    @Test
    public void mapGroupByValuesTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> {
            subscribe(KeyedData.class)
                    .groupBy(KeyedData::getId, KeyedData::getAmount)
                    .mapValues(EventStreamBuildTest::doubleInt)
                    .map(GroupBy::toMap)
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
                    .mapKeys(EventStreamBuildTest::toUpperCase)
                    .map(GroupBy::toMap)
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
                    .mapEntries(GroupByTest::mapKeyData)
                    .map(GroupBy::toMap)
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
            FlowBuilder<Map<String, Integer>> obj = subscribe(KeyedData.class)
                    .groupBy(KeyedData::getId, KeyedData::getAmount)
                    .filterValues(new MyIntFilter(500)::gt)
                    .map(GroupBy::toMap)
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
    public void deleteByValueGroupByTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(KeyedData.class)
                .groupBy(KeyedData::getId, KeyedData::getAmount)
                .deleteByValue(new EventStreamBuildTest.MyDynamicIntFilter(500)::gt)
                .map(GroupBy::toMap)
                .sink("keyValue"));

        addSink("keyValue", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        onEvent(500);
        onEvent(new KeyedData("A", 400));
        onEvent(new KeyedData("B", 233));
        onEvent(new KeyedData("B", 1000));
        onEvent(new KeyedData("B", 2000));
        onEvent(new KeyedData("C", 1000));
        onEvent(new KeyedData("B", 50));
        onEvent(new KeyedData("A", 1400));


        expected.put("A", 1400);
        expected.put("B", 50);
        expected.put("C", 1000);
        assertThat(results, CoreMatchers.is(expected));

        //cause a delete
        onEvent(500);
        expected.put("B", 50);
        assertThat(results, CoreMatchers.is(expected));

        onEvent(10);
        assertThat(results, CoreMatchers.is(expected));
    }


    @Test
    public void deleteByKeyTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(KeyedData.class)
                .groupBy(KeyedData::getId, KeyedData::getAmount)
                .deleteByKey(new DeleteSupplier()::getKeys)
                .map(GroupBy::toMap)
                .sink("keyValue"));

        addSink("keyValue", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        onEvent(new KeyedData("A", 400));
        onEvent(new KeyedData("B", 233));
        onEvent(new KeyedData("B", 1000));

        expected.put("A", 400);
        expected.put("B", 1000);
        assertThat(results, CoreMatchers.is(expected));

        onEvent("A");
        expected.put("B", 1000);
        assertThat(results, CoreMatchers.is(expected));

        onEvent("B");
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new KeyedData("B", 2000));
        onEvent(new KeyedData("C", 1000));
        onEvent(new KeyedData("B", 50));
        onEvent(new KeyedData("A", 1400));
        onEvent("A");

        expected.put("B", 50);
        expected.put("C", 1000);
        assertThat(results, CoreMatchers.is(expected));
    }

    @Test
    public void deleteByKeyWithDeleteFlowTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(KeyedData.class)
                .groupBy(KeyedData::getId, KeyedData::getAmount)
                .deleteByKey(collectionFromSubscribe(String.class), true)
                .map(GroupBy::toMap)
                .sink("keyValue"));

        addSink("keyValue", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        onEvent(new KeyedData("A", 400));
        onEvent(new KeyedData("B", 233));
        onEvent(new KeyedData("B", 1000));

        expected.put("A", 400);
        expected.put("B", 1000);
        assertThat(results, CoreMatchers.is(expected));

        onEvent("A");
        expected.put("B", 1000);
        assertThat(results, CoreMatchers.is(expected));

        onEvent("B");
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new KeyedData("B", 2000));
        onEvent(new KeyedData("C", 1000));
        onEvent(new KeyedData("B", 50));
        onEvent(new KeyedData("A", 1400));
        onEvent("A");

        expected.put("B", 50);
        expected.put("C", 1000);
        assertThat(results, CoreMatchers.is(expected));
    }

    public static class DeleteSupplier {
        @FluxtionIgnore
        private String[] deleteKeys = new String[]{};

        @OnEventHandler
        public boolean deleteKeys(String s) {
            deleteKeys = s.split(",");
            return true;
        }

        public List<String> getKeys() {
            return Arrays.asList(deleteKeys);
        }
    }

    @Test
    public void joinGroupByTest() {
        Map<String, MergedType> results = new HashMap<>();
        Map<String, MergedType> expected = new HashMap<>();

        sep(c -> {
            GroupByFlowBuilder<String, String> stringGroupBy = subscribe(String.class)
                    .groupBy(String::toString, Mappers::identity);

            GroupByFlowBuilder<String, Integer> keyedGroupBy = subscribe(KeyedData.class)
                    .groupBy(KeyedData::getId, KeyedData::getAmount);

            keyedGroupBy.innerJoin(stringGroupBy)
                    .mapValues(EventStreamBuildTest::mergedTypeFromTuple)
                    .map(GroupBy::toMap)
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

    @Test
    public void joinGroupByTestSimple() {
        Map<String, MergedType> results = new HashMap<>();
        Map<String, MergedType> expected = new HashMap<>();
        sep(c -> {
            groupBy(KeyedData::getId, KeyedData::getAmount)
                    .innerJoin(DataFlow.groupBy(String::toString))
                    .mapValues(EventStreamBuildTest::mergedTypeFromTuple)
                    .map(GroupBy::toMap)
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

    @Test
    public void biMapKeyedItemFromAnotherStreamTest() {
        sep(c -> {
            val mapped = subscribe(KeyedData.class)
                    .groupBy(KeyedData::getId)
                    .mapBiFunction(GroupByFunction.mapValueByKey(GroupByTest::applyFactor, Data::getName), subscribe(Data.class));

            mapped.map(GroupBy::toMap).id("rs");
            mapped.map(GroupBy::lastValue).id("value");
        });

        Map<String, KeyedData> expected = new HashMap<>();

        onEvent(new KeyedData("A", 400));
        onEvent(new KeyedData("B", 10));

        MatcherAssert.assertThat(getStreamed("rs"), is(nullValue()));

        onEvent(new Data("B", 5));
        expected.put("B", new KeyedData("B", 50));
        expected.put("A", new KeyedData("A", 400));
        MatcherAssert.assertThat(getStreamed("rs"), is(expected));
        MatcherAssert.assertThat(getStreamed("value"), is(new KeyedData("B", 50)));
    }

    public static KeyedData applyFactor(KeyedData keyedData, Data factor) {
        return new KeyedData(keyedData.getId(), keyedData.getAmount() * factor.getValue());
    }

    @Value
    public static class Data {
        String name;
        int value;
    }


    public enum SubSystem {REFERENCE, MARKET}

    public enum Change_type {CREATE, UPDATE, DELETE}

    @Value
    public static class MyEvent {
        SubSystem subSystem;
        Change_type change_type;
        String data;

        public static boolean isCreate(MyEvent myEvent) {
            return myEvent.change_type == Change_type.CREATE;
        }
    }


    @Value
    public static class MyModel {
        SubSystem subSystem;

        transient List<String> myData = new ArrayList<>();

        public void createItem(String newData) {
            myData.add(newData);
        }

    }

    @Test
    public void maintainModel() {
        sep(c -> {
            subscribe(MyModel.class).groupBy(MyModel::getSubSystem)
                    .mapBiFunction(
                            GroupByFunction.mapValueByKey(GroupByTest::updateItemScalar, MyEvent::getSubSystem),
                            subscribe(MyEvent.class).filter(MyEvent::isCreate))
                    .map(GroupBy::toMap)
                    .id("results");

        });

        Map<SubSystem, MyModel> expected = new HashMap<>();
        MyModel refModel = new MyModel(SubSystem.REFERENCE);
        MyModel marketModel = new MyModel(SubSystem.MARKET);


        onEvent(new MyModel(SubSystem.REFERENCE));
        onEvent(new MyModel(SubSystem.MARKET));
        MatcherAssert.assertThat(getStreamed("results"), is(nullValue()));

        onEvent(new MyEvent(SubSystem.REFERENCE, Change_type.CREATE, "greg-1"));
        refModel.myData.add("greg-1");
        expected.put(SubSystem.REFERENCE, refModel);
        expected.put(SubSystem.MARKET, marketModel);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        onEvent(new MyEvent(SubSystem.REFERENCE, Change_type.CREATE, "john"));
        refModel.myData.add("john");
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        onEvent(new MyEvent(SubSystem.MARKET, Change_type.CREATE, "BBC"));
        marketModel.myData.add("BBC");
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        onEvent(new MyEvent(SubSystem.REFERENCE, Change_type.DELETE, "greg-1"));
    }

    @Value
    public static class Data3 {
        String name;
        int value;
        int x;


    }

    @Getter
    public static class Data3Aggregate implements AggregateFlowFunction<Data3, Integer, Data3Aggregate> {
        int value;

        @Override
        public Integer reset() {
            return value;
        }

        @Override
        public Integer get() {
            return value;
        }

        @Override
        public Integer aggregate(Data3 input) {
            value += input.getX();
            return get();
        }
    }

    public static class Data3ToSum extends AbstractAggregateFlowFunction<Data3, Integer> {

        @Override
        protected Integer calculateAggregate(Data3 input, Integer previous) {
            return (previous == null ? 0 : previous) + input.getX();
        }

        @Override
        protected Integer resetAction(Integer previous) {
            return 0;
        }
    }

    @Test
    public void groupingKey() {
        Map<GroupByKey<Data3>, Data3> expected = new HashMap<>();
        sep(c -> {
            subscribe(Data3.class)
                    .groupByFields(Data3::getName, Data3::getValue)
                    .map(GroupBy::toMap)
                    .id("results");
        });
        val keyFactory = GroupByKey.build(Data3::getName, Data3::getValue);//.apply();

        onEvent(new Data3("A", 10, 1));
        expected.put(keyFactory.apply(new Data3("A", 10, 1)), new Data3("A", 10, 1));
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        Data3 data2 = new Data3("A", 10, 2);
        onEvent(data2);
        expected.put(keyFactory.apply(data2), data2);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        Data3 data3 = new Data3("A", 10, 3);
        onEvent(data3);
        expected.put(keyFactory.apply(data3), data3);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        Data3 data4 = new Data3("A", 15, 111);
        onEvent(data4);
        expected.put(keyFactory.apply(data4), data4);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        Data3 dataB1 = new Data3("B", 10, 1);
        onEvent(dataB1);
        expected.put(keyFactory.apply(dataB1), dataB1);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        Data3 dataB2 = new Data3("B", 10, 99);
        onEvent(dataB2);
        expected.put(keyFactory.apply(dataB2), dataB2);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));
    }

    @Test
    public void aggregateCompoundField() {
        Map<GroupByKey<Data3>, Integer> expected = new HashMap<>();
        sep(c -> {
            subscribe(Data3.class)
                    .groupByFieldsAggregate(Data3Aggregate::new, Data3::getName, Data3::getValue)
                    .map(GroupBy::toMap)
                    .id("results");
        });
        val keyFactory = GroupByKey.build(Data3::getName, Data3::getValue);//.apply();

        onEvent(new Data3("A", 10, 1));
        expected.put(keyFactory.apply(new Data3("A", 10, 1)), 1);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        Data3 data2 = new Data3("A", 10, 2);
        onEvent(data2);
        expected.put(keyFactory.apply(data2), 3);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        Data3 data3 = new Data3("A", 10, 3);
        onEvent(data3);
        expected.put(keyFactory.apply(data3), 6);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        Data3 data4 = new Data3("A", 15, 111);
        onEvent(data4);
        expected.put(keyFactory.apply(data4), 111);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        Data3 dataB1 = new Data3("B", 10, 1);
        onEvent(dataB1);
        expected.put(keyFactory.apply(dataB1), 1);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        Data3 dataB2 = new Data3("B", 10, 99);
        onEvent(dataB2);
        expected.put(keyFactory.apply(dataB2), 100);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));
    }

    @Test
    public void abstractAggregateFlowFunctionTest() {
        Map<String, Integer> resultMap = new HashMap<>();
        sep(c -> {
            DataFlow.groupBy(Data3::getName, Data3ToSum::new)
                    .resetTrigger(DataFlow.subscribeToSignal("reset"))
                    .map(GroupBy::toMap)
                    .id("results");
        });

        onEvent(new Data3("A", 10, 1));
        resultMap.put("A", 1);
        MatcherAssert.assertThat(getStreamed("results"), is(resultMap));

        onEvent(new Data3("A", 10, 100));
        onEvent(new Data3("A", 10, 100));
        onEvent(new Data3("B", 10, 3));
        resultMap.put("A", 201);
        resultMap.put("B", 3);
        MatcherAssert.assertThat(getStreamed("results"), is(resultMap));

        publishSignal("reset");
        resultMap.clear();
        MatcherAssert.assertThat(getStreamed("results"), is(resultMap));
    }

    @Test
    public void aggregateExtractedPropertyCompoundField() {
        Map<GroupByKey<Data3>, Integer> expected = new HashMap<>();
        sep(c -> {
            subscribe(Data3.class)
                    .groupByFieldsGetAndAggregate(
                            Data3::getX,
                            Aggregates.intSumFactory(),
                            Data3::getName, Data3::getValue)
                    .map(GroupBy::toMap)
                    .id("results");
        });
        val keyFactory = GroupByKey.build(Data3::getName, Data3::getValue);//.apply();

        onEvent(new Data3("A", 10, 1));
        expected.put(keyFactory.apply(new Data3("A", 10, 1)), 1);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        Data3 data2 = new Data3("A", 10, 2);
        onEvent(data2);
        expected.put(keyFactory.apply(data2), 3);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        Data3 data3 = new Data3("A", 10, 3);
        onEvent(data3);
        expected.put(keyFactory.apply(data3), 6);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        Data3 data4 = new Data3("A", 15, 111);
        onEvent(data4);
        expected.put(keyFactory.apply(data4), 111);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        Data3 dataB1 = new Data3("B", 10, 1);
        onEvent(dataB1);
        expected.put(keyFactory.apply(dataB1), 1);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));

        Data3 dataB2 = new Data3("B", 10, 99);
        onEvent(dataB2);
        expected.put(keyFactory.apply(dataB2), 100);
        MatcherAssert.assertThat(getStreamed("results"), is(expected));
    }


    public static MyModel updateItemScalar(MyModel model, MyEvent myEvent) {
        model.createItem(myEvent.getData());
        return model;
    }


    @Value
    public static class Trade {
        String ccyPair;
        double dealtVolume;
        double contraVolume;

        public String getDealtCcy() {
            return ccyPair.substring(0, 3);
        }

        public String getContraCcy() {
            return ccyPair.substring(3);
        }
    }

    @Value
    public static class MidPrice {
        String ccyPair;
        double rate;

        public double getRateForCcy(String ccy) {
            if (ccyPair.startsWith(ccy)) {
                return 1 / rate;
            } else if (ccyPair.contains(ccy)) {
                return rate;
            }
            return Double.NaN;
        }

        public String getOppositeCcy(String searchCcy) {
            if (ccyPair.startsWith(searchCcy)) {
                return ccyPair.substring(3);
            } else if (ccyPair.contains(searchCcy)) {
                return ccyPair.substring(0, 3);
            }
            return null;
        }

        public double getUsdRate() {
            return getRateForCcy("USD");
        }

        public String getUsdContraCcy() {
            return getOppositeCcy("USD");
        }

        public boolean hasUsdRate() {
            return getUsdContraCcy() != null;
        }
    }

    @Test
    public void multipleJoinsTheTupleMapThenReduceTest() {
        sep(c -> {
            val tradeStream = subscribe(Trade.class);

            val positionMap = JoinFlowBuilder.outerJoin(
                            tradeStream.groupBy(Trade::getDealtCcy, Trade::getDealtVolume, DoubleSumFlowFunction::new),
                            tradeStream.groupBy(Trade::getContraCcy, Trade::getContraVolume, DoubleSumFlowFunction::new))
                    .mapValues(Tuples.replaceNull(0d, 0d))
                    .mapValues(Tuples.mapTuple(Mappers::addDoubles));

            val rateMap = subscribe(MidPrice.class)
                    .filter(MidPrice::hasUsdRate)
                    .groupBy(MidPrice::getUsdContraCcy, MidPrice::getUsdRate)
                    .defaultValue(GroupBy.emptyCollection());

            JoinFlowBuilder.leftJoin(positionMap, rateMap)
                    .mapValues(Tuples.replaceNull(0d, Double.NaN))
                    .mapValues(Tuples.mapTuple(Mappers::multiplyDoubles))
                    .reduceValues(DoubleSumFlowFunction::new)
                    .id("pnl");

        });

        onEvent(new Trade("EURUSD", 100, -200));
        Assert.assertTrue(Double.isNaN(getStreamed("pnl")));
        onEvent(new Trade("EURUSD", 100, -200));
        onEvent(new Trade("USDJPY", 500, -200000));


        onEvent(new MidPrice("USDUSD", 1));
        MatcherAssert.assertThat(getStreamed("pnl"), CoreMatchers.is(Double.NaN));
        onEvent(new MidPrice("GBPUSD", 1.2));
        onEvent(new MidPrice("EURUSD", 1.5));
        MatcherAssert.assertThat(getStreamed("pnl"), CoreMatchers.is(Double.NaN));

        onEvent(new MidPrice("USDJPY", 100));
        MatcherAssert.assertThat(getStreamed("pnl"), is(closeTo(-1600, 0.01)));
    }
}
