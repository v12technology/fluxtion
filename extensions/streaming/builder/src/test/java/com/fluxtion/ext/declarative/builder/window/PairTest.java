/*
 * Copyright (c) 2020, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.ext.declarative.builder.window;

import com.fluxtion.ext.declarative.builder.group.AggregateTest;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.cumSum;
import lombok.Value;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class PairTest extends StreamInprocessTest {

    @Test
    public void testpair() {
        
        sep((c) -> {
            select(Pair.class).map(cumSum(), Pair::getValue).log("pair sum: '{}'", Number::intValue);
        });
        onEvent(new Pair<>("test", 10));
        onEvent(new Pair<>("test", 10));
        onEvent(new Pair<>("test", 10));
    }

    @Value
    public static class Pair<K, V> {

        K key;
        V value;

    }
}
