/*
 * Copyright (C) 2021 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.api.Anchor;
import com.fluxtion.api.SepContext;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.ext.streaming.api.Wrapper;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author V12 Technology Ltd.
 */
public class PartitioningFieldMapper extends AbstractFilterWrapper{

    private final Wrapper targetInstance;
    private final LambdaReflection.SerializableFunction  keySupplier;
    private final LambdaReflection.SerializableFunction readField;
    private final LambdaReflection.SerializableBiConsumer writeField;
    @NoEventReference
    private final LambdaReflection.SerializableSupplier<LambdaReflection.SerializableFunction> mapperFactory;
    private transient final Map<? super Object, LambdaReflection.SerializableFunction> functionMap;
    

    public <T, K, R, S> PartitioningFieldMapper(Wrapper<T> targetInstance,
        LambdaReflection.SerializableFunction<T, K>  keySupplier,
        LambdaReflection.SerializableFunction<T, R> readField,
        LambdaReflection.SerializableBiConsumer<T, S> writeField,
        LambdaReflection.SerializableSupplier<LambdaReflection.SerializableFunction> mapper) {
        if (mapper.captured().length > 0) {
            Object add = SepContext.service().add(mapper.captured()[0]);
            SepContext.service().addOrReuse(new Anchor(add, this));
        }
        this.targetInstance = targetInstance;
        this.keySupplier = keySupplier;
        this.readField = readField;
        this.writeField = writeField;
        this.mapperFactory = mapper;
        this.functionMap = new HashMap<>();
    }

    @OnEvent
    public boolean mapPartitionedField(){
        final Object event = targetInstance.event();
        Object key = keySupplier.apply(event);
        LambdaReflection.SerializableFunction mappingFunction = functionMap.get(key);
        if(mappingFunction == null){
            mappingFunction = mapperFactory.get();
            functionMap.put(key, mappingFunction);
        }
        Object mapResult = mappingFunction.apply(readField.apply(event));
        writeField.accept(event, mapResult);
        return true;
    }
    
    @Initialise
    @Override
    public void reset(){
        functionMap.clear();
    }

    @Override
    public Object event() {
        return targetInstance.event();
    }

    @Override
    public Class eventClass() {
        return targetInstance.eventClass();
    }

}
