package com.fluxtion.compiler.generation.serialiser;

import com.fluxtion.compiler.generation.model.Field;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class FieldContext<T> {
    private final T instanceToMap;
    private final List<Field> nodeFields;
    private final Set<Class<?>> importList;
    @Getter(AccessLevel.NONE)
    private final MapFieldToJavaSource mapFieldToJavaSource;

    public FieldContext(T instanceToMap, List<Field> nodeFields, Set<Class<?>> importList, MapFieldToJavaSource mapFieldToJavaSource) {
        this.instanceToMap = instanceToMap;
        this.nodeFields = nodeFields;
        this.importList = importList;
        this.mapFieldToJavaSource = mapFieldToJavaSource;
    }

    public String mapToJavaSource(Object instance) {
        return mapFieldToJavaSource.mapToJavaSource(instance, nodeFields, importList);
    }

}
