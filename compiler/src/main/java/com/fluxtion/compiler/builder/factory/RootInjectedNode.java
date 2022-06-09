package com.fluxtion.compiler.builder.factory;

import lombok.Value;

import java.util.Map;

/**
 * Configuration for a root node to be injected into the graph
 * @param <T>
 */
@Value
public class RootInjectedNode<T> {
    String name;
    Class<T> rootClass;
    Map<String, ?> config;
}
