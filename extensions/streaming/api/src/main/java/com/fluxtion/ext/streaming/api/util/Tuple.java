/*
 * Copyright (c) 2020, V12 Technology Ltd.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.streaming.api.util;

import java.util.Comparator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tuple<K, V> {

    K key;
    V value;

    public static void initCopy(Tuple source, Tuple target) {
        target.key = source.key;
    }

    public Object myKey() {
        return key;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Tuple> Class<T> generify() {
        return (Class<T>) _generify(Tuple.class);
    }
    
    @SuppressWarnings("unchecked")
    private static  <T extends Tuple> Class<T> _generify(Class<?> cls) {
        return (Class<T>) cls;
    }
    
    public static NumberValueComparator numberValComparator(){
        return new NumberValueComparator();
    }
    
    public static ValueComparator valComparator(){
        return new ValueComparator();
    }
    
    public static class NumberValueComparator implements Comparator<Tuple<?, Number>> {

        @Override
        public int compare(Tuple<?, Number> o1, Tuple<?, Number> o2) {
            return (int) (o1.getValue().doubleValue() - o2.getValue().doubleValue());
        }

    }
    
    public static class ValueComparator<C extends Comparable> implements Comparator<Tuple<?, C>> {

        @Override
        public int compare(Tuple<?, C> o1, Tuple<?, C> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }

    }
}
