package com.fluxtion.compiler.builder.factory;

import java.util.Map;

public class SingletonNodeFactory<T> implements NodeFactory<T> {
    private final T instance;
    private final Class<T> classType;
    private final String factoryName;

    public <S extends T> SingletonNodeFactory(S instance, Class<T> classType, String factoryName) {
        this.instance = instance;
        this.classType = classType;
        this.factoryName = factoryName;
    }

    @Override
    public T createNode(Map<String, Object> config, NodeRegistry registry) {
        return registry.registerNode(instance, factoryName);
    }

    @Override
    public String factoryName() {
        return factoryName;
    }

    @Override
    public Class<T> injectionType() {
        return classType;
    }
}
