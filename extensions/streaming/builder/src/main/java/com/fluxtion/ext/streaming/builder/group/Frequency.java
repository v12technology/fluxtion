/*
 * Copyright (C) 2019 V12 Technology Ltd.
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
package com.fluxtion.ext.streaming.builder.group;

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;

/**
 *
 * @author V12 Technology Ltd.
 */
public class Frequency {

    public static <K, T> GroupBy<MutableNumber> frequency(SerializableFunction<T, K> f) {
        GroupByBuilder<T, MutableNumber> wcQuery = Group.groupBy(f, MutableNumber.class);
        wcQuery.count(MutableNumber::setIntValue);
        return wcQuery.build();
    }

    public static <K, T> GroupBy<MutableNumber> frequency(SerializableSupplier<K> f) {
        GroupByBuilder<K, MutableNumber> wcQuery = Group.groupBy(f, MutableNumber.class);
        wcQuery.count(MutableNumber::setIntValue);
        return wcQuery.build();
    }

    public static <K, T> GroupBy<MutableNumber> frequency(Wrapper<T> wrapper, SerializableFunction<T, K> f) {
        GroupByBuilder<T, MutableNumber> wcQuery = Group.groupBy(wrapper, MutableNumber.class, f);
        wcQuery.count(MutableNumber::setIntValue);
        return wcQuery.build();
    }
}
