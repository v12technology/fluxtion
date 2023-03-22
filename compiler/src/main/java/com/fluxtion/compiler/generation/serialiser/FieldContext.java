package com.fluxtion.compiler.generation.serialiser;

import com.fluxtion.compiler.generation.model.Field;

import java.util.List;
import java.util.Set;

public class FieldContext {
    private final Object instanceToMap;
    private final List<Field> nodeFields;
    private final Set<Class<?>> importList;

    public FieldContext(Object instanceToMap, List<Field> nodeFields, Set<Class<?>> importList) {
        this.instanceToMap = instanceToMap;
        this.nodeFields = nodeFields;
        this.importList = importList;
    }

    public Object getInstanceToMap() {
        return instanceToMap;
    }

    public List<Field> getNodeFields() {
        return nodeFields;
    }

    public Set<Class<?>> getImportList() {
        return importList;
    }
}
