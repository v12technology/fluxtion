package com.fluxtion.compiler.generation.serialiser;

import com.fluxtion.compiler.generation.model.Field;

import java.util.List;
import java.util.Set;

public interface MapFieldToJavaSource {
    String mapToJavaSource(Object primitiveVal, List<Field> nodeFields, Set<Class<?>> importList);
}
