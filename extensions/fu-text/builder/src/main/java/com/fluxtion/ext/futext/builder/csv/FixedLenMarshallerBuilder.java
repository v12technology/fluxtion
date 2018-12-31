/* 
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.futext.builder.csv;

import com.fluxtion.extension.declarative.builder.util.LambdaReflection.SerializableBiConsumer;
import java.lang.reflect.Method;

/**
 *
 * @author gregp
 * @param <T>
 */
public class FixedLenMarshallerBuilder<T> extends RecordParserBuilder<FixedLenMarshallerBuilder<T>, T> {

    public static <S> FixedLenMarshallerBuilder<S> fixedLenMarshaller(Class<S> target) {
        return fixedLenMarshaller(target, 0);
    }

    public static <S> FixedLenMarshallerBuilder<S> fixedLenMarshaller(Class<S> target, int headerLInes) {
        FixedLenMarshallerBuilder<S> csvMarshallerBuilder = new FixedLenMarshallerBuilder<>(target, headerLInes);
        return csvMarshallerBuilder;
    }

    private FixedLenMarshallerBuilder(Class<T> target, int headerLines) {
        super(target, headerLines, true);
    }

    public <U> FixedLenMarshallerBuilder<T> mapFixed(int colStart, int length, SerializableBiConsumer<T, U> targetFunction) {
        Method targetMethod = targetFunction.method();
        CsvPushFunctionInfo info = new CsvPushFunctionInfo(importMap);
        info.setTarget(targetClazz, targetMethod, targetId);
        info.setSourceFixedField(colStart, length);
        info.setTrim(true);
        srcMappingList.add(info);
        return this;
    }

}
