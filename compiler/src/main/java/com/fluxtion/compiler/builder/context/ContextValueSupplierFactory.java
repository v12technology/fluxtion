package com.fluxtion.compiler.builder.context;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.compiler.generation.GenerationContext;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.node.ContextValueSupplier;
import com.fluxtion.runtime.node.ContextValueSupplierNode;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public class ContextValueSupplierFactory implements NodeFactory<ContextValueSupplier> {

    private static int count;

    @Override
    public ContextValueSupplier<?> createNode(Map<String, Object> config, NodeRegistry registry) {
        final Field field = (Field) config.get(NodeFactory.FIELD_KEY);
        final Type genericFieldType = field.getGenericType();
        final Class<?> rawType;
        if (genericFieldType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            rawType = ((Class) fieldArgTypes[0]);
        } else {
            rawType = Object.class;
        }
        final String typeName = "contextService_" + rawType.getSimpleName() + "_" + count++;
        return new ContextValueSupplierNode<>(rawType, true, null, typeName.replace(".", "_"));
    }

    @Override
    public void preSepGeneration(GenerationContext context, Map<String, Auditor> auditorMap) {
        count = 0;
    }
}
