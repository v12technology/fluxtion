package com.fluxtion.compiler.generation.serialiser;

import java.io.File;

public interface MetaSerializer {

    static String classToSource(FieldContext<Class<?>> fieldContext) {
        fieldContext.getImportList().add(File.class);
        Class<?> clazz = fieldContext.getInstanceToMap();
        return clazz.getCanonicalName() + ".class";
    }
}
