package com.fluxtion.compiler.builder.factory;

import lombok.Value;

import java.util.Map;

/**
 * Configuration for a root node to be injected into the graph
 */
@Value
public class RootInjectedNode {
    String name;
    Class<?> rootClass;
    Map<String, ?> config;
}
