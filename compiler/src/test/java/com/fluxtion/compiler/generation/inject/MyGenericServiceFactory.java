package com.fluxtion.compiler.generation.inject;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.google.auto.service.AutoService;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

@AutoService(NodeFactory.class)
public class MyGenericServiceFactory implements NodeFactory<InjectFactoryByNameTest.ServiceInjected> {
    @Override
    public InjectFactoryByNameTest.ServiceInjected<?> createNode(Map<String, Object> config, NodeRegistry registry) {
        Field field = (Field) config.get(NodeFactory.FIELD_KEY);
        Type genericFieldType = field.getGenericType();
        final String typeName;
        if (genericFieldType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            typeName = ((Class) fieldArgTypes[0]).getCanonicalName();
        } else {
            typeName = "";
        }
        return new InjectFactoryByNameTest.ServiceInjected<>(typeName);
    }

}
