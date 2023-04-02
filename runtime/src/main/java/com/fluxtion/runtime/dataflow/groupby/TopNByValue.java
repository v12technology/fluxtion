package com.fluxtion.runtime.dataflow.groupby;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class TopNByValue {
    private final int count;
    public SerializableFunction comparing;

    public TopNByValue(int count) {
        this.count = count;
    }

    //required for serialised version
    public <K, V> List<Entry<K, V>> filter(Object obj) {
        return filter((GroupBy) obj);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <K, V> List<Map.Entry<K, V>> filter(GroupBy groupBy) {
        return (List<Entry<K, V>>) new ArrayList<>(groupBy.toMap().entrySet()).stream()
                .sorted((Comparator<Entry>) (c1, c2) -> {
                    if (comparing != null) {
                        return ((Comparable) comparing.apply(c2.getValue())).compareTo(
                                comparing.apply(c1.getValue())
                        );
                    }
                    return ((Comparable) c2.getValue()).compareTo(c1.getValue());
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}
