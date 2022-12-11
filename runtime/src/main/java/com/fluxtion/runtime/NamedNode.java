package com.fluxtion.runtime;

/**
 * A unique name for a node in an instance {@link EventProcessor}. Advised to return a human-readable name that will make debugging
 * and code generation easier to interpret. Has no semantic meaning.
 */
public interface NamedNode {
    String getName();
}
