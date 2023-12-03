package com.fluxtion.compiler.generation.fieldserializer;

import com.fluxtion.compiler.generation.fieldserializer.CustomSerializerTest.WithBuilderFactory;
import com.fluxtion.compiler.generation.serialiser.FieldContext;
import com.fluxtion.compiler.generation.serialiser.FieldToSourceSerializer;
import com.google.auto.service.AutoService;

@AutoService(FieldToSourceSerializer.class)
public class WithBuilderFactorySerializer implements FieldToSourceSerializer<WithBuilderFactory> {
    @Override
    public boolean typeSupported(Class<?> type) {
        return WithBuilderFactory.class.isAssignableFrom(type);
    }

    @Override
    public String mapToSource(FieldContext<WithBuilderFactory> fieldContext) {
        fieldContext.getImportList().add(WithBuilderFactory.class);
        WithBuilderFactory withBuilder = (WithBuilderFactory) fieldContext.getInstanceToMap();
        return "WithBuilderFactory.createWithBuilder(\"" + withBuilder.getId() + "\")";
    }
}
