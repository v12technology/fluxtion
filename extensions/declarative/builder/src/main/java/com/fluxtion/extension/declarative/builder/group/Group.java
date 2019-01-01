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
package com.fluxtion.extension.declarative.builder.group;

import com.fluxtion.ext.declarative.api.group.MultiKey;
import com.fluxtion.api.generation.GenerationContext;
import com.fluxtion.ext.declarative.api.Wrapper;
import static com.fluxtion.extension.declarative.builder.factory.FunctionGeneratorHelper.methodFromLambda;
import com.fluxtion.extension.declarative.builder.util.ImportMap;
import com.fluxtion.runtime.event.Event;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * A class defining the grouping key used in aggregate processing of events.
 * 
 * @author Greg Higgins
 * @param <K> source data provider
 * @param <T> target type
 */
public class Group <K, T> {
    
    /**
     * TODO return a groupByFunctionBuilder
     * 
     * 
     * @param <K> The data node Type from which a grouping key will be extracted at runtime.
     * @param k The actual data node the key will be extracted from.
     * @param f A function used to extract the key value from K used to group source data by.
     * 
     * @return The actual grouping function.
     */
    public static <K> Group<K, ?> groupBy(K k, Function<K, ?> f){
        return new Group(k, f);
    }
    
    /**
     *
     * @param <K> The data node Type from which a grouping key will be extracted at runtime.
     * @param <T> Target type that is the holder of aggregate function results.
     * 
     * @param k The actual data node the key will be extracted from.
     * @param f A function used to extract the key value from K used to group source data by.
     * @param target target of any aggregate function created with this grouping
     * @return A builder
     */
    public static <K, T> GroupByBuilder<K, T> groupBy(K k, Function<K, ?> f, Class<T> target){
        final Group group = new Group(k, f, target);
        return GroupByContext.builder(group);
    }
    
    public static <K, T> GroupByBuilder<K, T> groupBy(Wrapper<K> k, Function<K, ?> f, Class<T> target){
        final Group group = new Group(k, f, target);
        group.wrapped = true;
        return GroupByContext.builder(group);
    }
      
    public static <K extends Event, T> GroupByBuilder<K, T> groupBy(Class<K> k, Function<K, ?> f, Class<T> target){
        try {
            final Group group = new Group(k.newInstance(), f, target);
            group.eventClass = true;
            return GroupByContext.builder(group);
        } catch (Exception ex) {
            throw new RuntimeException("cannot build Event class requires default constructor", ex);
        }
    }
    
    public static <K extends Event, T> GroupByBuilder<K, T> groupBy(Class<K> k, Class<T> target, Function<K, ?>... f){
        try {
            ArrayList<MultiKeyInfo> keyList = new ArrayList<>();
            ImportMap importMap = ImportMap.newMap();
            for (Function<K, ?> function : f) {
                MultiKeyInfo info = new MultiKeyInfo(importMap);
                Method sourceMethod = methodFromLambda(k, function);
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
    private final K inputSource;
    private final Function<K, ?> keyFunction;
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
    
    
    private Group(K input, Function<K, ?> keyFunction) {
        this(input, keyFunction, null);
    }

    private Group(K input, Function<K, ?> keyFunction, Class<T> target) {
        this.inputSource = input;
        this.keyFunction = keyFunction;
        this.target = target;
    }

    private Group(K input, MultiKey<?> multiKey, Class<T> target) {
        this.inputSource = input;
        keyFunction = null;
        this.multiKey = multiKey;
        this.target = target;
    }
    
    public <K1> Group<K1, T> join(K1 secondInput, Function<K1, ?> keyFunction){
        Group g = new Group(secondInput, keyFunction, getTargetClass());
        g.joinedGroup = this;
        return g;
    }
    
    
    public <K1, T> Group<K1, T> join(Wrapper<K1> secondInput, Function<K1, ?> keyFunction){
        Group g = new Group(secondInput, keyFunction, getTargetClass());
        g.joinedGroup = this;
        g.wrapped = true;
        return g;
    }
    
    public <K1 extends Event> Group<K1, T> join(Class<K1> secondInput, Function<K1, ?> keyFunction){
        try {
            Group g = new Group(secondInput.newInstance(), keyFunction, getTargetClass());
            g.eventClass = true;
            g.joinedGroup = this;
            return g;
        } catch (Exception ex) {
            throw new RuntimeException("cannot build Event class requires default constructor", ex);
        }
    }
    
    public <K1 extends Event> Group<K1, T> join(Class<K1> secondInput, Function<K1, ?>... keyFunction){
        try {
            ArrayList<MultiKeyInfo> keyList = new ArrayList<>();
            int i = 0;
            for (Function<K1, ?> function : keyFunction) {
                MultiKeyInfo info = new MultiKeyInfo(multiKeyImportMap);
                Method sourceMethod = methodFromLambda(secondInput, function);
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

    public <T1> Group<K, T1> target(Class<T1> t){
        target =  (Class<T>) t;
        return  (Group<K, T1>) this;
    }
    
    public Class<T> getTargetClass() {
        return target;
    }
    
    public K getInputSource() {
        return inputSource;
    }

    public Function<K, ?> getKeyFunction() {
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
     
}
