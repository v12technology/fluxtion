package com.fluxtion.runtime.node;

import com.fluxtion.runtime.audit.EventLogNode;

import java.util.Objects;

/**
 * Implements {@link NamedNode} overriding hashcode and equals using the name as the equality test and hash code seed
 */
public abstract class SingleNamedNode extends EventLogNode implements NamedNode {

    private final String name;

    public SingleNamedNode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleNamedNode that = (SingleNamedNode) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}