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
import com.fluxtion.api.partition.LambdaReflection;

/**
 *
 * @author V12 Technology Ltd.
 */
public class PartitioningFieldMapper {

    private final LambdaReflection.SerializableSupplier mapper;

    public PartitioningFieldMapper(LambdaReflection.SerializableSupplier mapper) {
        if (mapper.captured().length > 0) {
            Object add = SepContext.service().add(mapper.captured()[0]);
            SepContext.service().addOrReuse(new Anchor(add, this));
        }
        this.mapper = mapper;
    }

}
