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

import com.fluxtion.api.Anchor;
import com.fluxtion.api.SepContext;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.partition.LambdaReflection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gregp
 */
public class FieldMapperFunctions {

    public static class PartitionCollection<T> {

        private final LambdaReflection.SerializableFunction keySupplier;
        private final LambdaReflection.SerializableFunction readField;
        private final LambdaReflection.SerializableBiConsumer writeField;
        @NoEventReference
        private final LambdaReflection.SerializableSupplier< LambdaReflection.SerializableFunction> mapperFactory;
        private transient final Map<? super Object, LambdaReflection.SerializableFunction> functionMap;

        public <T, K, R, S> PartitionCollection(
            LambdaReflection.SerializableFunction<T, K> keySupplier,
            LambdaReflection.SerializableFunction<T, R> readField,
            LambdaReflection.SerializableBiConsumer<T, S> writeField,
            LambdaReflection.SerializableSupplier<LambdaReflection.SerializableFunction> mapperFactory) {
            if (mapperFactory.captured().length > 0 && SepContext.service().buildTime()) {
                Object add = SepContext.service().add(mapperFactory.captured()[0]);
                SepContext.service().addOrReuse(new Anchor(add, this));
            }
            this.keySupplier = keySupplier;
            this.readField = readField;
            this.writeField = writeField;
            this.mapperFactory = mapperFactory;
            this.functionMap = new HashMap<>();
        }

        public void mapField(Collection<T> collection) {
            collection.forEach(event -> {
                Object key = keySupplier.apply(event);
                LambdaReflection.SerializableFunction mappingFunction = functionMap.get(key);
                if (mappingFunction == null) {
                    mappingFunction = mapperFactory.get();
                    functionMap.put(key, mappingFunction);
                }
                Object mapResult = mappingFunction.apply(readField.apply(event));
                writeField.accept(event, mapResult);
            });
        }

    }
}
