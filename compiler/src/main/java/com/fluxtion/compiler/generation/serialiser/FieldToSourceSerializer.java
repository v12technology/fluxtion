package com.fluxtion.compiler.generation.serialiser;

import java.util.ServiceLoader;

/**
 * Loads a FieldToSourceSerializer using the {@link ServiceLoader} support provided
 * by Java platform. New factories can be added to Fluxtion using the extension
 * mechanism described in {@link ServiceLoader} documentation.
 */
public interface FieldToSourceSerializer {

    int DEFAULT_PRIORITY = 500;

    boolean typeSupported(Class<?> type);

    String mapToSource(FieldContext fieldContext);

    default String language() {
        return "java";
    }

    default int priority() {
        return DEFAULT_PRIORITY;
    }
}
