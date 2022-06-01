package com.fluxtion.runtime.stream.predicate;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.groupby.GroupBy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class TopNPredicate {
    private final int count;
    public SerializableFunction comparing;

    public TopNPredicate(int count) {
        this.count = count;
    }

    //required for serialised version
    public  <K, V> List<Entry<K, V>> filter(Object obj) {
        return filter((GroupBy) obj);
    }

    public <K, V> List<Map.Entry<K, V>> filter(GroupBy groupBy) {
        return (List<Entry<K, V>>) new ArrayList<>(groupBy.map().entrySet()).stream()
                .sorted(new Comparator<Entry>() {
                    @Override
                    public int compare(Entry c1, Entry c2) {
                        if(comparing!=null){
                            return ((Comparable)comparing.apply(c2.getValue())).compareTo(
                                    comparing.apply(c1.getValue())
                            );
                        }
                        return ((Comparable)c2.getValue()).compareTo(c1.getValue());
                    }
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}
