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
package com.fluxtion.ext.futext.builder.math;

import com.fluxtion.extension.declarative.api.group.GroupBy;
import com.fluxtion.extension.declarative.builder.group.Group;
import com.fluxtion.extension.declarative.builder.group.GroupByBuilder;
import com.fluxtion.ext.futext.api.ascii.ByteBufferDelimiter;
import com.fluxtion.extension.declarative.api.numeric.MutableInt;
import java.util.function.Function;

/**
 *
 * @author gregp
 */
public interface Frequency {

    public static <K, T> GroupBy<MutableInt> frequency(ByteBufferDelimiter buffer) {
        return frequency(buffer, ByteBufferDelimiter::asString);
    }

    public static <K, T> GroupBy<MutableInt> frequency(K k, Function<K, ?> f) {
        GroupByBuilder<K, MutableInt> wcQuery = Group.groupBy(k, f, MutableInt.class);
        wcQuery.count(MutableInt::setValue);
        GroupBy<MutableInt> wc = wcQuery.build();
        return wc;
    }
}
