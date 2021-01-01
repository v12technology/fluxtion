/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.ext.streaming.builder.group;

import com.fluxtion.api.partition.LambdaReflection.SerializableBiConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableTriFunction;
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.AggregateFunctions;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.builder.group.GroupByContext.SourceContext;
import java.lang.reflect.Method;
import org.apache.commons.lang.StringUtils;

/**
 * The main instance a user interacts with while building a group by mapPrimitive.
 *
 * @author Greg Higgins
 * @param <K> Source type
 * @param <T> Target type
 */
public class GroupByBuilder<K, T> {

    private final GroupByContext<K, T> groupBy;
    private final GroupByContext<K, T>.SourceContext<K, T> sourceContext;

    public GroupByBuilder(GroupByContext<K, T> context, SourceContext sourceContext) {
        this.groupBy = context;
        this.sourceContext = sourceContext;
    }

    public <K> GroupByBuilder<K, T> join(K k, SerializableFunction<K, ?> f) {
        return groupBy.join(k, f);
    }

    public <K> GroupByBuilder<K, T> join(Class<K> k, SerializableFunction<K, ?> f) {
        return groupBy.join(k, f);
    }

    public <K> GroupByBuilder<K, T> join(Class<K> k, SerializableFunction<K, ?>... f) {
        return groupBy.join(k, f);
    }

    public <S> GroupByBuilder<S, T> join(Wrapper<S> k, SerializableFunction<S, ?> f) {
        return groupBy.join(k, f);
    }

    public <S> GroupByBuilder<S, T> join(Wrapper<S> k, SerializableFunction<S, ?>... f) {
        return groupBy.join(k, f);
    }

    public <V> GroupByBuilder<K, T> init(SerializableFunction<K, ? extends V> valueFunction, SerializableBiConsumer<T, ? super V> tragetFunction) {
        try {
            Method sourceMethod = valueFunction.method();
            Method targetMethod = tragetFunction.method();
            GroupByInitialiserInfo info = new GroupByInitialiserInfo(groupBy.getImportMap());
            sourceContext.setInitialiserId("initialiser" + sourceContext.getSourceInfo().id);
            if (sourceContext.isWrapped()) {
                info.setSource(((Wrapper) sourceContext.getKeyProvider()).eventClass(), sourceMethod, "source");
            } else {
                info.setSource(sourceContext.getKeyProvider().getClass(), sourceMethod, "source");
            }
            info.setTarget(sourceContext.getTargetClass(), targetMethod, "target");
            sourceContext.addInitialiserFunction(info);
        } catch (Exception e) {
            throw new RuntimeException("unable to build init function for groupBy", e);
        }
        return this;
    }
    
    public GroupByBuilder<K, T> initCopy(SerializableBiConsumer<T, T> initCopy){
        groupBy.setInitCopy(initCopy);
        return this;
    }

    public GroupByBuilder<K, T> initPrimitive(SerializableFunction<K, ? extends Number> valueFunction, SerializableBiConsumer<T, ? super Byte> tragetFunction) {
        Method sourceMethod = valueFunction.method();
        Method targetMethod = tragetFunction.method();
        GroupByInitialiserInfo info = new GroupByInitialiserInfo(groupBy.getImportMap());
        sourceContext.setInitialiserId("initialiser" + sourceContext.getSourceInfo().id);
        if (sourceContext.isWrapped()) {
            info.setSource(((Wrapper) sourceContext.getKeyProvider()).eventClass(), sourceMethod, "source");
        } else {
            info.setSource(sourceContext.getKeyProvider().getClass(), sourceMethod, "source");
        }
        info.setTarget(sourceContext.getTargetClass(), targetMethod, "target");
        sourceContext.addInitialiserFunction(info);
        return this;
    }

    public GroupByBuilder<K, T>
        avg(SerializableFunction<K, ? extends Number> sourceFunction, SerializableBiConsumer<T, ? super Byte> target) {
        return GroupByBuilder.this.mapPrimitive(sourceFunction, target, AggregateFunctions.AggregateAverage::calcAverage);
    }

    public GroupByBuilder<K, T>
        count(SerializableBiConsumer<T, ? super Byte> target) {
        return GroupByBuilder.this.mapPrimitive(target, AggregateFunctions::count);
    }

    public GroupByBuilder<K, T>
        set(SerializableFunction<K, ? extends Number> sourceFunction, SerializableBiConsumer<T, ? super Byte> target) {
        return GroupByBuilder.this.mapPrimitive(sourceFunction, target, AggregateFunctions::set);
    }

    public GroupByBuilder<K, T>
        min(SerializableFunction<K, ? extends Number> sourceFunction, SerializableBiConsumer<T, ? super Byte> target) {
        return GroupByBuilder.this.mapPrimitive(sourceFunction, target, AggregateFunctions::minimum);
    }

    public GroupByBuilder<K, T>
        max(SerializableFunction<K, ? extends Number> sourceFunction, SerializableBiConsumer<T, ? super Byte> target) {
        return GroupByBuilder.this.mapPrimitive(sourceFunction, target, AggregateFunctions::maximum);
    }

    public GroupByBuilder<K, T>
        sum(SerializableFunction<K, ? extends Number> sourceFunction, SerializableBiConsumer<T, ? super Byte> target) {
        return GroupByBuilder.this.mapPrimitive(sourceFunction, target, AggregateFunctions::calcSum);
    }

    public void optional(boolean optional) {
        sourceContext.setOptional(optional);
    }

    // ==================================================
    // static mapping mapPrimitive
    // ==================================================
    public <S, R> GroupByBuilder<K, T> map(
        SerializableFunction<K, ? extends S> supplier,
        SerializableBiConsumer<T, ? super R> target,
        SerializableBiFunction< ? super S, ? super R, ? extends R> func) {
        Class calcFunctionClass = func.getContainingClass();
        //set mapPrimitive
        GroupByFunctionInfo info = new GroupByFunctionInfo(groupBy.getImportMap());
        String id = StringUtils.uncapitalize(calcFunctionClass.getSimpleName() + GenerationContext.nextId());
        info.setFunction(calcFunctionClass, func.method(), id);
        //set source
        Class<K> sourceClass = null;
        if (sourceContext.isWrapped()) {
            sourceClass = (Class<K>) ((Wrapper) sourceContext.getKeyProvider()).eventClass();
        } else {
            sourceClass = (Class<K>) sourceContext.getGroup().getInputSource().getClass();
        }
        Method sourceMethod = supplier.method();//numericGetMethod(sourceClass, sourceFunction);
        info.setSource(sourceClass, sourceMethod, "event");
        //set source
        info.setTarget(sourceContext.getTargetClass(), target.method(), "target");
        //add to context
        sourceContext.addGroupByFunctionInfo(info);
        return this;
    }

    public <S extends Number, G extends Number, R> GroupByBuilder<K, T> mapPrimitive(
        SerializableFunction<K, ? extends S> supplier,
        SerializableBiConsumer<T, ? super Byte> target,
        SerializableBiFunction<? super G, ? super G, ? extends G> func) {
        Class calcFunctionClass = func.getContainingClass();
        //set mapPrimitive
        GroupByFunctionInfo info = new GroupByFunctionInfo(groupBy.getImportMap());
        String id = StringUtils.uncapitalize(calcFunctionClass.getSimpleName() + GenerationContext.nextId());
        info.setFunction(calcFunctionClass, func.method(), id);
        //set source
        Class<K> sourceClass = null;
        if (sourceContext.isWrapped()) {
            sourceClass = (Class<K>) ((Wrapper) sourceContext.getKeyProvider()).eventClass();
        } else {
            sourceClass = (Class<K>) sourceContext.getGroup().getInputSource().getClass();
        }
        Method sourceMethod = supplier.method();//numericGetMethod(sourceClass, sourceFunction);
        info.setSource(sourceClass, sourceMethod, "event");
        //set source
        info.setTarget(sourceContext.getTargetClass(), target.method(), "target");
        //add to context
        sourceContext.addGroupByFunctionInfo(info);
        return this;
    }

    // ==================================================
    // instance mapping mapPrimitive
    // ==================================================
    public <S, R, F> GroupByBuilder<K, T> map(
        SerializableFunction<K, ? extends S> supplier,
        SerializableBiConsumer<T, ? super R> target,
        SerializableTriFunction<F, ? super S, ? super R, ? extends R> func) {
        Class calcFunctionClass = func.getContainingClass();
        //set mapPrimitive
        GroupByFunctionInfo info = new GroupByFunctionInfo(groupBy.getImportMap());
        String id = StringUtils.uncapitalize(calcFunctionClass.getSimpleName() + GenerationContext.nextId());
        info.setFunction(calcFunctionClass, func.method(), id);
        //set source
        Class<K> sourceClass = null;
        if (sourceContext.isWrapped()) {
            sourceClass = (Class<K>) ((Wrapper) sourceContext.getKeyProvider()).eventClass();
        } else {
            sourceClass = (Class<K>) sourceContext.getGroup().getInputSource().getClass();
        }
        Method sourceMethod = supplier.method();//numericGetMethod(sourceClass, sourceFunction);
        info.setSource(sourceClass, sourceMethod, "event");
        //set source
        info.setTarget(sourceContext.getTargetClass(), target.method(), "target");
        //add to context
        sourceContext.addGroupByFunctionInfo(info);
        return this;
    }
    
    public <S extends Number, G extends Number, R, F> GroupByBuilder<K, T> mapPrimitive(
        SerializableFunction<K, ? extends S> supplier,
        SerializableBiConsumer<T, ? super Byte> target,
        SerializableTriFunction<F, ? super G, ? super G, ? extends G> func) {
        Class calcFunctionClass = func.getContainingClass();
        //set mapPrimitive
        GroupByFunctionInfo info = new GroupByFunctionInfo(groupBy.getImportMap());
        String id = StringUtils.uncapitalize(calcFunctionClass.getSimpleName() + GenerationContext.nextId());
        info.setFunction(calcFunctionClass, func.method(), id);
        //set source
        Class<K> sourceClass = null;
        if (sourceContext.isWrapped()) {
            sourceClass = (Class<K>) ((Wrapper) sourceContext.getKeyProvider()).eventClass();
        } else {
            sourceClass = (Class<K>) sourceContext.getGroup().getInputSource().getClass();
        }
        Method sourceMethod = supplier.method();//numericGetMethod(sourceClass, sourceFunction);
        info.setSource(sourceClass, sourceMethod, "event");
        //set source
        info.setTarget(sourceContext.getTargetClass(), target.method(), "target");
        //add to context
        sourceContext.addGroupByFunctionInfo(info);
        return this;
    }

    
    // ==================================================
    // instance mapping mapPrimitive
    // ==================================================
    public <S, R> GroupByBuilder<K, T>
        map(
            SerializableBiConsumer<T, ? super R> target,
            SerializableBiFunction<? super S, ? super R, ? extends R> func) {
        Class calcFunctionClass = func.getContainingClass();
        //set mapPrimitive
        GroupByFunctionInfo info = new GroupByFunctionInfo(groupBy.getImportMap());
        String id = StringUtils.uncapitalize(calcFunctionClass.getSimpleName() + GenerationContext.nextId());
        info.setFunction(calcFunctionClass, func.method(), id);
        info.setSourceThis();
        //set target
        info.setTarget(sourceContext.getTargetClass(), target.method(), "target");
        //add to context
        sourceContext.addGroupByFunctionInfo(info);
        return this;
    }
        
    public <G extends Number> GroupByBuilder<K, T>
        mapPrimitive(
            SerializableBiConsumer<T, ? super Byte> target,
            SerializableBiFunction<? super G, ? super G, ? extends G> func) {
        Class calcFunctionClass = func.getContainingClass();
        //set mapPrimitive
        GroupByFunctionInfo info = new GroupByFunctionInfo(groupBy.getImportMap());
        String id = StringUtils.uncapitalize(calcFunctionClass.getSimpleName() + GenerationContext.nextId());
        info.setFunction(calcFunctionClass, func.method(), id);
        info.setSourceThis();
        //set target
        info.setTarget(sourceContext.getTargetClass(), target.method(), "target");
        //add to context
        sourceContext.addGroupByFunctionInfo(info);
        return this;
    }

    public <S, R, F> GroupByBuilder<K, T>
        map(
            SerializableBiConsumer<T, ? super R> target,
            SerializableTriFunction<F, ? super S, ? super R, ? extends R> func) {
        Class calcFunctionClass = func.getContainingClass();
        //set mapPrimitive
        GroupByFunctionInfo info = new GroupByFunctionInfo(groupBy.getImportMap());
        String id = StringUtils.uncapitalize(calcFunctionClass.getSimpleName() + GenerationContext.nextId());
        info.setFunction(calcFunctionClass, func.method(), id);
        info.setSourceThis();
        //set target
        info.setTarget(sourceContext.getTargetClass(), target.method(), "target");
        //add to context
        sourceContext.addGroupByFunctionInfo(info);
        return this;
    }      
        
    public <G extends Number, F> GroupByBuilder<K, T>
        mapPrimitive(
            SerializableBiConsumer<T, ? super Byte> target,
            SerializableTriFunction<F, ? super G, ? super G, ? extends G> func) {
        Class calcFunctionClass = func.getContainingClass();
        //set mapPrimitive
        GroupByFunctionInfo info = new GroupByFunctionInfo(groupBy.getImportMap());
        String id = StringUtils.uncapitalize(calcFunctionClass.getSimpleName() + GenerationContext.nextId());
        info.setFunction(calcFunctionClass, func.method(), id);
        info.setSourceThis();
        //set target
        info.setTarget(sourceContext.getTargetClass(), target.method(), "target");
        //add to context
        sourceContext.addGroupByFunctionInfo(info);
        return this;
    }

    public GroupBy<T> build() {
        return groupBy.build();
    }

    public GroupBy<T> build(String publicName) {
        return groupBy.build();
    }

}
