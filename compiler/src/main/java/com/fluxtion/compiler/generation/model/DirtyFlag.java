/*
 * Copyright (c) 2019, 2024 gregory higgins.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.compiler.generation.model;

import java.util.Objects;

/**
 * A flag that represents the state of a node in a SEP.
 *
 * @author Greg Higgins
 */
public class DirtyFlag {
    public final Field node;
    public final String name;
    public boolean alwaysDirty;
    public boolean requiresInvert = false;

    public DirtyFlag(Field node, String name) {
        this.node = node;
        this.name = name;
        alwaysDirty = false;
        requiresInvert = false;
    }

    public DirtyFlag(Field node, String name, boolean alwaysDirty) {
        this.node = node;
        this.name = name;
        this.alwaysDirty = alwaysDirty;
    }

    public DirtyFlag clone() {
        DirtyFlag df = new DirtyFlag(node, name, alwaysDirty);
        df.requiresInvert = requiresInvert;
        return df;
    }

    public Field getNode() {
        return node;
    }

    public String getName() {
        return name;
    }

    public String getForkedName() {
        return "fork_" + node.getName();
    }

    public boolean isAlwaysDirty() {
        return alwaysDirty;
    }

    public boolean isRequiresInvert() {
        return requiresInvert;
    }

    @Override
    public String toString() {
        return "DirtyFlag{" + "node=" + node + ", name=" + name + ", defaultVal=" + alwaysDirty + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.node);
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + (this.alwaysDirty ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DirtyFlag other = (DirtyFlag) obj;
        if (this.alwaysDirty != other.alwaysDirty) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.node, other.node);
    }

}
