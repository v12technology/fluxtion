package com.fluxtion.ext.declarative.api;

import com.fluxtion.api.annotations.OnParentUpdate;
import java.util.ArrayList;
import java.util.List;

/**
 * Merges streams into a single node in the SEP execution graph.
 * @author V12 Technology Ltd.
 */
public class MergingWrapper<T> implements Wrapper<T> {

    private T event;
    private Class<T> clazz;
    private final String className;
    public List<Wrapper> wrappedNodes;

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
    public void wrapperUpdate(Wrapper<? extends T> wrappedNode){
        event = wrappedNode.event();
    }

    public Wrapper<T> merge(Wrapper<? extends T>... nodes){
        for (Wrapper node : nodes) {
            wrappedNodes.add( node);
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
