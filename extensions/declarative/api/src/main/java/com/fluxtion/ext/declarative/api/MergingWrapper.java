package com.fluxtion.ext.declarative.api;

/**
 * Merges streams into a single node in the SEP execution graph.
 * @author V12 Technology Ltd.
 */
public class MergingWrapper<T> implements Wrapper<T> {

    private T event;
    private Class<T> clazz;
    private final String className;

    public MergingWrapper(String className) {
        this.className = className;
        try {
            this.clazz = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException ex) {
        }
    }

    public MergingWrapper(Class<T> clazz) {
        this(clazz.getCanonicalName());
        this.clazz = clazz;
    }

    public Wrapper<T> merge(Wrapper<? super T>... nodes){
        return this;
    }
//    
//    public <S super T> void m2(S... s){
//        
//    }
    
    
    @Override
    public T event() {
        return event;
    }

    @Override
    public Class<T> eventClass() {
        return clazz;
    }
    
}
