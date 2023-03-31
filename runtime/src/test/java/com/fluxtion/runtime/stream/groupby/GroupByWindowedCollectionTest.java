package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.stream.aggregate.functions.AggregateIntSum;
import lombok.Value;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class GroupByWindowedCollectionTest {

    Supplier<GroupByWindowedCollection<Data, String, Integer, Integer, AggregateIntSum>> supplier =
            () -> new GroupByWindowedCollection<>(Data::getKey, Data::getIntValue, AggregateIntSum::new);

    @Test
    public void testMap() {
        GroupByWindowedCollection<Data, String, Integer, Integer, AggregateIntSum> groupCount = supplier.get();

        groupCount.aggregate(new Data("A", 25));
        groupCount.aggregate(new Data("A", 65));
        groupCount.aggregate(new Data("B", 20));
        groupCount.aggregate(new Data("B", 30));
        groupCount.aggregate(new Data("B", 40));
        groupCount.aggregate(new Data("B", 10));
        groupCount.aggregate(new Data("A", 110));

        assertThat(groupCount.toMap(), is(
                map("A", 200, "B", 100)
        ));
    }

    @Test
    public void testCombine() {
        GroupByWindowedCollection<Data, String, Integer, Integer, AggregateIntSum> group1 = supplier.get();
        GroupByWindowedCollection<Data, String, Integer, Integer, AggregateIntSum> group2 = supplier.get();
        GroupByWindowedCollection<Data, String, Integer, Integer, AggregateIntSum> group3 = supplier.get();

        group2.aggregate(new Data("A", 30));

        group1.combine(group2);
        group1.combine(group2);
        group1.combine(group2);
        assertThat(group1.toMap(), is(map("A", 90)));

        group3.aggregate(new Data("B", 44));
        group1.combine(group3);
        assertThat(group1.toMap(), is(
                map("A", 90, "B", 44)
        ));
    }

    @Test
    public void testDeduct() {
        GroupByWindowedCollection<Data, String, Integer, Integer, AggregateIntSum> aggregate = supplier.get();
        GroupByWindowedCollection<Data, String, Integer, Integer, AggregateIntSum> group1 = supplier.get();

        group1.aggregate(new Data("A", 30));

        aggregate.combine(group1);
        aggregate.combine(group1);
        aggregate.combine(group1);
        assertThat(aggregate.toMap(), is(map("A", 90)));

        aggregate.deduct(group1);
        assertThat(aggregate.toMap(), is(map("A", 60)));

        aggregate.deduct(group1);
        assertThat(aggregate.toMap(), is(map("A", 30)));

        aggregate.deduct(group1);
        assertTrue(aggregate.toMap().isEmpty());
    }

    @Test
    public void combineAndDeductTest() {
        GroupByWindowedCollection<Data, String, Integer, Integer, AggregateIntSum> aggregate = supplier.get();
        GroupByWindowedCollection<Data, String, Integer, Integer, AggregateIntSum> group1 = supplier.get();
        GroupByWindowedCollection<Data, String, Integer, Integer, AggregateIntSum> group2 = supplier.get();
        GroupByWindowedCollection<Data, String, Integer, Integer, AggregateIntSum> group3 = supplier.get();

        group1.aggregate(new Data("A", 30));
        group1.aggregate(new Data("A", 30));
        group1.aggregate(new Data("B", 50));
        group1.aggregate(new Data("C", 150));

        group2.aggregate(new Data("B", 50));
        group2.aggregate(new Data("C", 150));

        group3.aggregate(new Data("C", 150));
        group3.aggregate(new Data("D", 150));

        aggregate.combine(group1);
        aggregate.combine(group2);
        aggregate.combine(group3);
        assertThat(aggregate.toMap(), is(map(
                "A", 60,
                "B", 100,
                "C", 450,
                "D", 150)));

        aggregate.deduct(group1);
        assertThat(aggregate.toMap(), is(map(
                "B", 50,
                "C", 300,
                "D", 150)));

        aggregate.deduct(group3);
        assertThat(aggregate.toMap(), is(map(
                "B", 50,
                "C", 150)));

        aggregate.combine(group1);
        assertThat(aggregate.toMap(), is(map(
                "A", 60,
                "B", 100,
                "C", 300)));

        aggregate.deduct(group2);
        assertThat(aggregate.toMap(), is(map(
                "A", 60,
                "B", 50,
                "C", 150)));

        aggregate.deduct(group1);
        assertTrue(aggregate.toMap().isEmpty());
    }

    @Value
    public static class Data {
        String key;
        int intValue;
    }

    private static Map<String, Integer> map(String key, Integer value) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private static Map<String, Integer> map(String key, Integer value, String key2, Integer value2) {
        Map<String, Integer> map = map(key, value);
        map.put(key2, value2);
        return map;
    }

    private static Map<String, Integer> map(String key, Integer value,
                                            String key2, Integer value2,
                                            String key3, Integer value3) {
        Map<String, Integer> map = map(key, value, key2, value2);
        map.put(key3, value3);
        return map;
    }

    private static Map<String, Integer> map(String key, Integer value,
                                            String key2, Integer value2,
                                            String key3, Integer value3,
                                            String key4, Integer value4
    ) {
        Map<String, Integer> map = map(key, value, key2, value2, key3, value3);
        map.put(key4, value4);
        return map;
    }
}