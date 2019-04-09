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
package com.fluxtion.ext.streaming.api;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import java.util.ArrayList;
import java.util.List;

/**
 * Merges streams into a single node in the SEP execution graph. The merge will make available
 * the last parent that was updated via the {@link #event() } call.
 *
 * @author V12 Technology Ltd.
 */
public class MergingWrapper<T> implements Wrapper<T> {

    private T event;
    private final Class<T> clazz;
    public List<Wrapper> wrappedNodes;
    public List<T> nodes;
    
    public static <T> MergingWrapper<T> merge(Class<T> clazz, Wrapper<? extends T>... nodes){
        MergingWrapper<T> merger = new MergingWrapper<>(clazz);
        merger.mergeWrappers(nodes);
        return SepContext.service().add(merger);
    }
    
    public static <T> MergingWrapper<T> merge(Wrapper<T>... nodes){
        MergingWrapper<T> merger = new MergingWrapper<>(nodes[0].eventClass());
        merger.mergeWrappers(nodes);
        return SepContext.service().add(merger);
    }
    
    public static <T, S extends T> MergingWrapper<T> merge(Class<T> clazz, S... nodes){
        MergingWrapper<T> merger = new MergingWrapper<>(clazz);
        merger.mergeNodes(nodes);
        return SepContext.service().add(merger);
    }
    
    public static <T> MergingWrapper<T> merge(T... nodes){
        MergingWrapper<T> merger = new MergingWrapper<>((Class<T>)nodes[0].getClass());
        merger.mergeNodes(nodes);
        return SepContext.service().add(merger);
    }

    public MergingWrapper(Class<T> clazz) {
        this.clazz = clazz;
        wrappedNodes = new ArrayList<>();
        nodes = new ArrayList<>();
    }

    @OnParentUpdate("wrappedNodes")
    public void mergeWrapperUpdate(Wrapper<? extends T> wrappedNode) {
        event = wrappedNode.event();
    }

    @OnParentUpdate("nodes")
    public <S extends T> void mergeUpdate(S node) {
        event = node;
    }

    @OnEvent
    public boolean updated() {
        return true;
    }

    public Wrapper<T> mergeWrappers(Wrapper<? extends T>... nodes) {
        for (Wrapper node : nodes) {
            wrappedNodes.add(node);
        }
        return this;
    }
    
    public <S extends T> Wrapper mergeNodes(S... nodesT){
        for (S node : nodesT) {
            nodes.add(node);
        }
        return this;
    }

    @Override
    public T event() {
        return event;
    }

    @Override
    public Class<T> eventClass() {
        return clazz;
    }

}
