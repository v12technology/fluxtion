package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.stream.aggregate.functions.AggregateIntSum;
import junit.framework.TestCase;
import lombok.Value;

public class GroupByCollectionTest extends TestCase {

    public void testMap() {
        GroupByCollection<Data, Integer, String, Integer, AggregateIntSum> groupCount =
                new GroupByCollection<>(Data::getKey, Data::getIntValue, AggregateIntSum::new);


        groupCount.aggregate(new Data("A", 23));
        groupCount.aggregate(new Data("A", 23));
        groupCount.aggregate(new Data("B", 565));
        groupCount.aggregate(new Data("B", 565));
        groupCount.aggregate(new Data("B", 565));
        groupCount.aggregate(new Data("B", 565));
        groupCount.aggregate(new Data("A", 250));

        System.out.println(groupCount.map());
    }

    @Value
    public static class Data{
        String key;
        int intValue;
    }
}