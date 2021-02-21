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
package com.fluxtion.ext.streaming.builder.factory;

import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.stream.Argument;
import static com.fluxtion.ext.streaming.api.stream.Argument.arg;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions;
import static com.fluxtion.ext.streaming.builder.factory.MappingBuilder.map;
import java.util.Set;

/**
 *
 * @author V12 Technology Ltd.
 */
public interface CollectionFunctionsBuilder {
        
    public static <T, S> Wrapper<Set<S>> unique(LambdaReflection.SerializableFunction<T, S> supplier) {
        return map(unique(), arg(supplier)).notifyOnChange(true);
    }

    public static <S> LambdaReflection.SerializableFunction<S, Set<S>> unique() {
        return new StreamFunctions.Unique<S>()::addUnique;
    }

    public static <S> Wrapper<Set<S>> unique(Argument<S> arg) {
        return map(new StreamFunctions.Unique<S>()::addUnique, arg).notifyOnChange(true);
    }

    public static <S> Wrapper<Set<S>> unique(Wrapper<S> arg) {
        return map(new StreamFunctions.Unique<S>()::addUnique, arg(arg)).notifyOnChange(true);
    }

}
