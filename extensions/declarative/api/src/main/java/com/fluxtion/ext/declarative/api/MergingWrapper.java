package com.fluxtion.ext.declarative.api;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import java.util.ArrayList;
import java.util.List;

/**
 * Merges streams into a single node in the SEP execution graph.
 *
 * @author V12 Technology Ltd.
 */
public class MergingWrapper<T> implements Wrapper<T> {

    private T event;
    private Class<T> clazz;
    private final String className;
    public List<Wrapper> wrappedNodes;
    public List nodes;

    public MergingWrapper(String className) {
        this.className = className;
        try {
            this.clazz = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException ex) {
        }
        wrappedNodes = new ArrayList<>();
    }

    public MergingWrapper(Class<T> clazz) {
        this(clazz.getCanonicalName());
        this.clazz = clazz;
    }

    @OnParentUpdate("wrappedNodes")
    public void dependencyUpdate(Wrapper<? extends T> wrappedNode) {
        event = wrappedNode.event();
    }

    @OnEvent
    public boolean updated() {
        return true;
    }

    public Wrapper<T> merge(Wrapper<? extends T>... nodes) {
        for (Wrapper node : nodes) {
            wrappedNodes.add(node);
        }
        return this;
    }
    
    public <S extends T> Wrapper merge(S... nodesT){
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
