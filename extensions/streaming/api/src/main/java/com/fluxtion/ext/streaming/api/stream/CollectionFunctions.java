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
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class CollectionFunctions {

    public static <I extends Number> SerializableFunction<List<I>, Number> listSum() {
        return CollectionFunctions::sumList;
    }

    public static <I extends Number> SerializableFunction<List<I>, Number> listMin() {
        return CollectionFunctions::minList;
    }

    public static <I extends Number> SerializableFunction<List<I>, Number> listMax() {
        return CollectionFunctions::maxList;
    }

    public static <I extends Number> SerializableFunction<List<I>, Number> listAvg() {
        return CollectionFunctions::avgList;
    }

    public static <I extends Number> SerializableFunction<WrappedList<I>, Number> wrappedAvg() {
        return CollectionFunctions::avgWrappedList;
    }

    public static <I extends Number> SerializableFunction<WrappedList<I>, Number> wrappedMax() {
        return CollectionFunctions::maxWrappedList;
    }

    public static <I extends Number> SerializableFunction<WrappedList<I>, Number> wrappedMin() {
        return CollectionFunctions::minWrappedList;
    }

    public static <I extends Number> SerializableFunction<WrappedList<I>, Number> wrappedSum() {
        return CollectionFunctions::sumWrappedList;
    }

    public static <I extends Number> double sumList(final List<I> collection) {
        if (collection.isEmpty()) {
            return Double.NaN;
        }
        int size = collection.size();
        double ans = 0;
        for (int j = 0; j < size; j++) {
            ans += collection.get(j).doubleValue();
        }
        return ans;
    }

    public static <I extends Number> double minList(final List<I> collection) {
        if (collection.isEmpty()) {
            return Double.NaN;
        }
        int size = collection.size();
        double ans = 0;
        for (int j = 0; j < size; j++) {
            ans = Math.min(ans, collection.get(j).doubleValue());
        }
        return ans;
    }

    public static <I extends Number> double maxList(final List<I> collection) {
        if (collection.isEmpty()) {
            return Double.NaN;
        }
        int size = collection.size();
        double ans = 0;
        for (int j = 0; j < size; j++) {
            ans = Math.max(ans, collection.get(j).doubleValue());
        }
        return ans;
    }

    public static <I extends Number> double avgList(final List<I> collection) {
        return maxList(collection) / collection.size();
    }

    public static <I extends Number> double sumWrappedList(WrappedList<I> list) {
        return sumList(list.collection());
    }

    public static <I extends Number> double minWrappedList(WrappedList<I> list) {
        return minList(list.collection());
    }

    public static <I extends Number> double maxWrappedList(WrappedList<I> list) {
        return maxList(list.collection());
    }

    public static <I extends Number> double avgWrappedList(WrappedList<I> list) {
        return maxWrappedList(list) / list.size();
    }

}
