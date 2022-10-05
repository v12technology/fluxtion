package com.fluxtion.compiler;

import lombok.Value;

import java.util.Map;

/**
 * Configuration for a root node to be injected into the graph
 */
@Value
public class RootNodeConfig {
    String name;
    Class<?> rootClass;
    Map<String, Object> config;
}
