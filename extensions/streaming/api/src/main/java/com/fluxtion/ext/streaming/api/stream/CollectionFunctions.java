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
package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.WrappedList;
import java.util.List;

/**
 * Functions to use with WrappedList
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class CollectionFunctions {

    public static <I extends Number> SerializableFunction<WrappedList<I>, Number> collectionAvg() {
        return CollectionFunctions::avg;
    }
    
    public static <I extends Number> SerializableFunction<WrappedList<I>, Number> collectionMax() {
        return CollectionFunctions::max;
    }

    public static <I extends Number> SerializableFunction<WrappedList<I>, Number> collectionMin() {
        return CollectionFunctions::min;
    }

    public static <I extends Number> SerializableFunction<WrappedList<I>, Number> collectionSum() {
        SerializableFunction<WrappedList<I>, Number> sumCollection = CollectionFunctions::sum;
        return CollectionFunctions::sum;
    }

    public static <I extends Number> double sum(WrappedList<I> list) {
        if (list.isEmpty()) {
            return Double.NaN;
        }
        final List<I> collection = list.collection();
        int size = collection.size();
        double ans = collection.get(0).doubleValue();
        for (int j = 0; j < size; j++) {
            ans += collection.get(j).doubleValue();
        }
        return ans;
    }

    public static <I extends Number> double min(WrappedList<I> list) {
        if (list.isEmpty()) {
            return Double.NaN;
        }
        final List<I> collection = list.collection();
        int size = collection.size();
        double ans = collection.get(0).doubleValue();
        for (int j = 0; j < size; j++) {
            ans = Math.min(ans, collection.get(j).doubleValue());
        }
        return ans;
    }

    public static <I extends Number> double max(WrappedList<I> list) {
        if (list.isEmpty()) {
            return Double.NaN;
        }
        final List<I> collection = list.collection();
        int size = collection.size();
        double ans = collection.get(0).doubleValue();
        for (int j = 0; j < size; j++) {
            ans = Math.max(ans, collection.get(j).doubleValue());
        }
        return ans;
    }
    public static <I extends Number> double avg(WrappedList<I> list) {
        return max(list)/list.size();
    }
    
    
}
