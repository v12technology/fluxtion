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
package com.fluxtion.ext.streaming.builder.factory;

import com.fluxtion.api.partition.LambdaReflection.SerializableBiConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableTriFunction;
import com.fluxtion.ext.streaming.api.group.AggregateFunctions;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.util.Tuple;
import com.fluxtion.ext.streaming.builder.group.Group;

/**
 * Shortcut functions to produce aggregate grouping functions on an event stream. Results are returned as a Tuple:
 * <ul>
 * <li>Tuple::key - the grouping key for the data
 * <li>Tuple::value - the result of the function
 * </ul>
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class GroupFunctionsBuilder {

    public static <S, K, V extends Number, F, R extends Number> GroupBy<Tuple<K, Number>> groupByCalc(
        SerializableFunction<S, K> keySupplier,
        SerializableFunction<S, V> valueSupplier,
        SerializableBiFunction<? super R, ? super R, ? extends R> calcFunctionClass
    ) {
        Class<Tuple<K, Number>> tupleClass = Tuple.generify();
        SerializableBiConsumer<Tuple<K, Number>, ? super Byte> consumer = tupleSetRef(Tuple<K, ? super Byte>::setValue);
        GroupBy<Tuple<K, Number>> build = Group.groupBy(keySupplier, tupleClass)
                .init(keySupplier, Tuple::setKey)
                .initCopy(Tuple::copyKey)
                .mapPrimitive(valueSupplier, consumer, calcFunctionClass)
            .build();
        return build;
    }

    public static <S, K, V extends Number, F, R extends Number> GroupBy<Tuple<K, Number>> groupByCalc(
        SerializableFunction<S, K> keySupplier,
        SerializableBiFunction<? super R, ? super R, ? extends R> calcFunctionClass
    ) {
        Class<Tuple<K, Number>> tupleClass = Tuple.generify();
        SerializableBiConsumer<Tuple<K, Number>, ? super Byte> consumer = tupleSetRef(Tuple<K, ? super Byte>::setValue);
        GroupBy<Tuple<K, Number>> build = Group.groupBy(keySupplier, tupleClass)
            .init(keySupplier, Tuple::setKey)
            .initCopy(Tuple::copyKey)
            .mapPrimitive( consumer, calcFunctionClass)
            .build();
        return build;
    }

    public static <S, K, R, T > GroupBy<Tuple<K, R>> groupByCalcComplex(
        SerializableFunction<S, K> keySupplier,
        SerializableBiFunction<? super S, ? super R, ? extends R> calcFunctionClass
    ) {
        Class<Tuple<K, R>> tupleClass = Tuple.generify();
        GroupBy<Tuple<K, R>> build = Group.groupBy(keySupplier, tupleClass)
            .init(keySupplier, Tuple::setKey)
            .initCopy(Tuple::copyKey)
            .map( Tuple::setValue, calcFunctionClass)
            .build();
        return build;
    }

    public static <S, K, V extends Number, F, R extends Number> GroupBy<Tuple<K, Number>> groupByCalc(
        SerializableFunction<S, K> keySupplier,
        SerializableFunction<S, V> valueSupplier,
        SerializableTriFunction<F, ? super R, ? super R, ? extends R> calcFunctionClass
    ) {
        Class<Tuple<K, Number>> tupleClass = Tuple.generify();
        SerializableBiConsumer<Tuple<K, Number>, ? super Byte> consumer = tupleSetRef(Tuple<K, ? super Byte>::setValue);
        GroupBy<Tuple<K, Number>> build = Group.groupBy(keySupplier, tupleClass)
            .init(keySupplier, Tuple::setKey)
            .initCopy(Tuple::copyKey)
            .mapPrimitive(valueSupplier, consumer, calcFunctionClass)
            .build();
        return build;
    }

    public static <S, K, V extends Number, F, R extends Number> GroupBy<Tuple<K, Number>> groupByCalc(
        SerializableFunction<S, K> keySupplier,
        SerializableTriFunction<F, ? super R, ? super R, ? extends R> calcFunctionClass
    ) {
        Class<Tuple<K, Number>> tupleClass = Tuple.generify();
        SerializableBiConsumer<Tuple<K, Number>, ? super Byte> consumer = tupleSetRef(Tuple<K, ? super Byte>::setValue);
        GroupBy<Tuple<K, Number>> build = Group.groupBy(keySupplier, tupleClass)
            .init(keySupplier, Tuple::setKey)
            .initCopy(Tuple::copyKey)
            .mapPrimitive( consumer, calcFunctionClass)
            .build();
        return build;
    }

    public static <S, K, R, T, F> GroupBy<Tuple<K, R>> groupByCalcComplex(
        SerializableFunction<S, K> keySupplier,
        SerializableTriFunction<F, ? super S, ? super R, ? extends R> calcFunctionClass
    ) {
        Class<Tuple<K, R>> tupleClass = Tuple.generify();
        GroupBy<Tuple<K, R>> build = Group.groupBy(keySupplier, tupleClass)
            .init(keySupplier, Tuple::setKey)
            .initCopy(Tuple::copyKey)
            .map( Tuple::setValue, calcFunctionClass)
            .build();
        return build;
    }
    
    private static <K, R> SerializableBiConsumer<K, R> tupleSetRef(SerializableBiConsumer<K, R> c){
        return (SerializableBiConsumer<K, R>) c;
    }

    public static <K, S, T extends Number> GroupBy<Tuple<K, Number>> groupBySum(
        SerializableFunction<S, K> key,
        SerializableFunction<S, T> supplier
    ) {
        return groupByCalc(key, supplier, AggregateFunctions.AggregateSum::calcCumSum);
    }

    public static <K, S, T extends Number> GroupBy<Tuple<K, Number>> groupByAvg(
        SerializableFunction<S, K> key,
        SerializableFunction<S, T> supplier
    ) {
        return GroupFunctionsBuilder.groupByCalc(key, supplier, AggregateFunctions.AggregateAverage::calcAverage);
    }

    public static <K, S, T extends Number> GroupBy<Tuple<K, Number>> groupByMax(
        SerializableFunction<S, K> key,
        SerializableFunction<S, T> supplier
    ) {
        return groupByCalc(key, supplier, AggregateFunctions::maximum);
    }

    public static <K, S, T extends Number> GroupBy<Tuple<K, Number>> groupByMin(
        SerializableFunction<S, K> key,
        SerializableFunction<S, T> supplier
    ) {
        return groupByCalc(key, supplier, AggregateFunctions::minimum);
    }

    public static <K, S, T extends Number> GroupBy<Tuple<K, Number>> groupByCount(
        SerializableFunction<S, K> key
    ) {
        return groupByCalc(key, AggregateFunctions::count);
    }
}
