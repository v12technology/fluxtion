/*
 * Copyright (C) 2021 V12 Technology Ltd
 *  All rights reserved.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Server Side Public License, version 1,
 *  as published by MongoDB, Inc.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Server Side Public License for more details.
 *
 *  You should have received a copy of the Server Side Public License
 *  along with this program.  If not, see 
 *  <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.api.SepContext;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.api.Anchor;
import com.fluxtion.ext.streaming.api.Wrapper;

/**
 *
 * @author gregp
 */
public class FieldMapper extends AbstractFilterWrapper {

    private final Wrapper targetInstance;
    private final LambdaReflection.SerializableFunction readField;
    private final LambdaReflection.SerializableBiConsumer writeField;
    private final LambdaReflection.SerializableFunction mapper;

    public static <T, R, S> Wrapper<T> setField(Wrapper<T> targetInstance,
        LambdaReflection.SerializableFunction<T, R> readField,
        LambdaReflection.SerializableBiConsumer<T, ? super S> writeField,
        LambdaReflection.SerializableFunction<? super R, ? extends S> mapper) {
        final Wrapper wrapper = SepContext.service().add(new FieldMapper(targetInstance, readField, writeField, mapper));
        return wrapper;
    }

    public static <T, R, S> Wrapper<T> setField(T targetInstance,
        LambdaReflection.SerializableFunction<T, R> readField,
        LambdaReflection.SerializableBiConsumer<T, ? super S> writeField,
        LambdaReflection.SerializableFunction<? super R, ? extends S> mapper) {
        return SepContext.service().add(new FieldMapper(NodeWrapper.wrap(targetInstance), readField, writeField, mapper));
    }

    public <T, R, S> FieldMapper(Wrapper<T> targetInstance,
        LambdaReflection.SerializableFunction<T, R> readField,
        LambdaReflection.SerializableBiConsumer<T, S> writeField,
        LambdaReflection.SerializableFunction<? super R, ? extends S> mapper) {
        if(mapper.captured().length > 0){
            Object add = SepContext.service().add(mapper.captured()[0]);
            SepContext.service().addOrReuse(new Anchor( add, this));
        }
        this.targetInstance = targetInstance;
        this.readField = readField;
        this.writeField = writeField;
        this.mapper = mapper;
    }

    @OnEvent
    public boolean updateField() {
        Object inputArg = readField.apply(targetInstance.event());
        writeField.accept(targetInstance.event(), mapper.apply(inputArg));
        return true;
    }

    @Override
    public Object event() {
        return  targetInstance.event();
    }

    @Override
    public Class eventClass() {
        return targetInstance.eventClass();
    }
}
