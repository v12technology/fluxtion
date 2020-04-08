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

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.MultiKey;
import com.fluxtion.ext.streaming.builder.util.ImportMap;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A class defining the grouping key used in aggregate processing of events.
 * 
 * @author Greg Higgins
 * @param <S> source data provider
 * @param <T> target type
 */
public class Group <S, T> {
    
    /**
     * TODO return a groupByFunctionBuilder
     * 
     * 
     * @param <S> The data node Type from which a grouping key will be extracted at runtime.
     * @param k The actual data node the key will be extracted from.
     * @param f A function used to extract the key value from K used to group source data by.
     * 
     * @return The actual grouping function.
     */
    public static <S> Group<S, ?> groupBy(S k, SerializableFunction<S, ?> f){
        return new Group(k, f);
    }
    
    /**
     *
     * @param <S> The data node Type from which a grouping key will be extracted at runtime.
     * @param <T> Target type that is the holder of aggregate function results.
     * 
     * @param k The actual data node the key will be extracted from.
     * @param f A function used to extract the key value from K used to group source data by.
     * @param target target of any aggregate function created with this grouping
     * @return A builder
     */
    public static <S, T> GroupByBuilder<S, T> groupBy(S k, SerializableFunction<S, ?> f, Class<T> target){
        final Group group = new Group(k, f, target);
        return GroupByContext.builder(group);
    }
    
    public static <S, T> GroupByBuilder<S, T> groupBy(Wrapper<S> k, SerializableFunction<S, ?> f, Class<T> target){
        final Group group = new Group(k, f, target);
        group.wrapped = true;
        return GroupByContext.builder(group);
    }
      
    public static <S, T> GroupByBuilder<S, T> groupBy(Class<S> k, SerializableFunction<S, ?> f, Class<T> target){
        try {
            final Group group = new Group(k.newInstance(), f, target);
            group.eventClass = true;
            return GroupByContext.builder(group);
        } catch (Exception ex) {
            throw new RuntimeException("cannot build Event class requires default constructor", ex);
        }
    }
    
    public static <S, T> GroupByBuilder<S, T> groupBy(Class<S> k, Class<T> target, SerializableFunction<S, ?>... f){
        try {
            ArrayList<MultiKeyInfo> keyList = new ArrayList<>();
            ImportMap importMap = ImportMap.newMap();
            for (SerializableFunction<S, ?> function : f) {
                MultiKeyInfo info = new MultiKeyInfo(importMap);
                Method sourceMethod = function.method();
                info.setSource(sourceMethod, sourceMethod.getName() + GenerationContext.nextId());
                keyList.add(info);
            }
            HashMap<String, List<MultiKeyInfo>> multiKeySourceMap = new HashMap<>();
            multiKeySourceMap.put(importMap.addImport(k), keyList);
            String multiKeyClassName = "MultiKeyFor_" + target.getSimpleName() + "_" + GenerationContext.nextId();
            MultiKey<?> multiKey = MultiKeyGenerator.generate(keyList, k, multiKeySourceMap, importMap, multiKeyClassName);
            final Group group = new Group(k.newInstance(), multiKey, target);
            group.multiKeySourceMap = multiKeySourceMap;
            group.multiKeyList = keyList;
            group.eventClass = true;
            group.multiKeyImportMap = importMap;
            group.multiKeyClassName = multiKeyClassName;
            return GroupByContext.builder(group);
        } catch (Exception ex) {
            throw new RuntimeException("cannot build Event class requires default constructor", ex);
        }
    }
    
    private boolean eventClass = false;
    private boolean wrapped = false;
    private final S inputSource;
    private final SerializableFunction<S, ?> keyFunction;
    private MultiKey<?> multiKey;
    private Class<T> target;
    private Group joinedGroup;
    private ArrayList<MultiKeyInfo> multiKeyList;
    private HashMap<String, List<MultiKeyInfo>> multiKeySourceMap;
    private ImportMap multiKeyImportMap;
    private String multiKeyClassName;

    
    private void setMultiKeyClassName(String className){
        if(joinedGroup==null){
            multiKeyClassName = className;
        }else{
            joinedGroup.setMultiKeyClassName(className);
        }
    }
    
    private void setMultiKey(MultiKey<?> multiKey){
        if(joinedGroup==null){
            multiKey = multiKey;
        }else{
            joinedGroup.setMultiKey(multiKey);
        }
    }
    
    public MultiKey<?> getMultiKey(){
        if(joinedGroup==null){
            return multiKey;
        }else{
            return joinedGroup.getMultiKey();
        }
    }
    
    String getMultiKeyClassName(){
        if(joinedGroup==null){
            return multiKeyClassName;
        }else{
            return joinedGroup.getMultiKeyClassName();
        }
    }
    
    
    private Group(S input, SerializableFunction<S, ?> keyFunction) {
        this(input, keyFunction, null);
    }

    private Group(S input, SerializableFunction<S, ?> keyFunction, Class<T> target) {
        this.inputSource = input;
        this.keyFunction = keyFunction;
        this.target = target;
    }

    private Group(S input, MultiKey<?> multiKey, Class<T> target) {
        this.inputSource = input;
        keyFunction = null;
        this.multiKey = multiKey;
        this.target = target;
    }
    
    public <S1> Group<S1, T> join(S1 secondInput, SerializableFunction<S1, ?> keyFunction){
        Group g = new Group(secondInput, keyFunction, getTargetClass());
        g.joinedGroup = this;
        return g;
    }
    
    
    public <S1, T> Group<S1, T> join(Wrapper<S1> secondInput, SerializableFunction<S1, ?> keyFunction){
        Group g = new Group(secondInput, keyFunction, getTargetClass());
        g.joinedGroup = this;
        g.wrapped = true;
        return g;
    }
    
    public <S1> Group<S1, T> join(Class<S1> secondInput, SerializableFunction<S1, ?> keyFunction){
        try {
            Group g = new Group(secondInput.newInstance(), keyFunction, getTargetClass());
            g.eventClass = true;
            g.joinedGroup = this;
            return g;
        } catch (Exception ex) {
            throw new RuntimeException("cannot build Event class requires default constructor", ex);
        }
    }
    
    public <S1> Group<S1, T> join(Class<S1> secondInput, SerializableFunction<S1, ?>... keyFunction){
        try {
            ArrayList<MultiKeyInfo> keyList = new ArrayList<>();
            int i = 0;
            for (SerializableFunction<S1, ?> function : keyFunction) {
                MultiKeyInfo info = new MultiKeyInfo(multiKeyImportMap);
                Method sourceMethod = function.method();
                info.setSource(sourceMethod, multiKeyList.get(i).getId());
                i++;
//                info.setSource(sourceMethod, sourceMethod.getName() + GenerationContext.nextId());
                keyList.add(info);
            }
            multiKeySourceMap.put(multiKeyImportMap.addImport(secondInput), keyList);
            String multiKeyClassName = "MultiKeyFor_" + target.getSimpleName() + "_" + GenerationContext.nextId();
            MultiKey<?> multiKey = MultiKeyGenerator.generate(keyList, secondInput, multiKeySourceMap, multiKeyImportMap, multiKeyClassName);
            System.out.println(multiKey);
            final Group group = new Group(secondInput.newInstance(), multiKey, getTargetClass());
            group.multiKeyList = keyList;
            group.eventClass = true;
            group.joinedGroup = this;
            group.multiKeySourceMap = multiKeySourceMap;
            group.multiKeyImportMap = multiKeyImportMap;
            group.setMultiKeyClassName(multiKeyClassName);
            group.setMultiKey(multiKey);
            return group;
        } catch (Exception ex) {
            throw new RuntimeException("cannot build Event class requires default constructor", ex);
        }  
    }

    public <T1> Group<S, T1> target(Class<T1> t){
        target =  (Class<T>) t;
        return  (Group<S, T1>) this;
    }
    
    public Class<T> getTargetClass() {
        return target;
    }

    public Class<S> getInputClass() {
        return (Class<S>) inputSource.getClass();
    }

    public S getInputSource() {
        return inputSource;
    }

    public SerializableFunction<S, ?> getKeyFunction() {
        return keyFunction;
    }

    public boolean isEventClass() {
        return eventClass;
    }

    public boolean isWrapped() {
        return wrapped;
    }
    
    public boolean isMultiKey(){
        return multiKey!=null;
    }

    public HashMap<String, List<MultiKeyInfo>> getMultiKeySourceMap() {
        return multiKeySourceMap;
    }
     
}
