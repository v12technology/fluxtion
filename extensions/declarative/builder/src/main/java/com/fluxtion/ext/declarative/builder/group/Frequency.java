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
package com.fluxtion.ext.declarative.builder.group;

import com.fluxtion.ext.declarative.api.group.GroupBy;
import com.fluxtion.ext.declarative.api.numeric.MutableInt;
import java.util.function.Function;

/**
 *
 * @author V12 Technology Ltd.
 */
public class Frequency {

    public static <K, T> GroupBy<MutableInt> frequency(K k, Function<K, ?> f) {
        GroupByBuilder<K, MutableInt> wcQuery = Group.groupBy(k, f, MutableInt.class);
        wcQuery.count(MutableInt::setValue);
        GroupBy<MutableInt> wc = wcQuery.build();
        return wc;
    }
}
