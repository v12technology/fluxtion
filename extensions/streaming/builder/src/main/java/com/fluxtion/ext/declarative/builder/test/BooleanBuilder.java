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
package com.fluxtion.ext.declarative.builder.test;

import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.declarative.api.Test;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.api.test.AndOperator;
import com.fluxtion.ext.declarative.api.test.BooleanEitherFilter;
import com.fluxtion.ext.declarative.api.test.BooleanFilter;
import com.fluxtion.ext.declarative.api.test.BooleanMatchFilter;
import com.fluxtion.ext.declarative.api.test.NandOperator;
import com.fluxtion.ext.declarative.api.test.NorOperator;
import com.fluxtion.ext.declarative.api.test.NotOperator;
import com.fluxtion.ext.declarative.api.test.OrOperator;
import com.fluxtion.ext.declarative.api.test.XorOperator;

/**
 * Factory class for building boolean logical operations for nodes and events.
 * 
 * @author gregp
 */
public class BooleanBuilder {

    public static Test not(Object tracked) {
        NotOperator notOperator = new NotOperator(tracked);
        return GenerationContext.SINGLETON.addOrUseExistingNode(notOperator);
    }

    public static Test and(Object... tracked) {
        AndOperator and = new AndOperator(tracked);
        return GenerationContext.SINGLETON.addOrUseExistingNode(and);
    }

    public static Test or(Object... tracked) {
        OrOperator or = new OrOperator(tracked);
        return GenerationContext.SINGLETON.addOrUseExistingNode(or);
    }

    public static Test xor(Object... tracked) {
        XorOperator xor = new XorOperator(tracked);
        return GenerationContext.SINGLETON.addOrUseExistingNode(xor);
    }

    public static Test nor(Object... tracked) {
        NorOperator nor = new NorOperator(tracked);
        return GenerationContext.SINGLETON.addOrUseExistingNode(nor);
    }

    public static Test nand(Object... tracked) {
        NandOperator nand = new NandOperator(tracked);
        return GenerationContext.SINGLETON.addOrUseExistingNode(nand);
    }
    
    public static <T> Wrapper<T> filter(Wrapper<T> trackedWrapped, Object notifier) {
        BooleanFilter<T> filter = new BooleanFilter<>( trackedWrapped, notifier);
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        return filter;
    }
    
    public static <T> Wrapper<T> filter(T tracked, Object notifier) {
        BooleanFilter<T> filter = new BooleanFilter<>(tracked, notifier);
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        return filter;
    }
    public static <T> Wrapper<T> filterEither(Wrapper<T> trackedWrapped, Object notifier) {
        BooleanEitherFilter<T> filter = new BooleanEitherFilter<>( trackedWrapped, notifier);
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        return filter;
    }
    
    public static <T> Wrapper<T> filterEither(T tracked, Object notifier) {
        BooleanEitherFilter<T> filter = new BooleanEitherFilter<>(tracked, notifier);
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        return filter;
    }
    
    public static <T> Wrapper<T> filterMatch(Wrapper<T> trackedWrapped, Object notifier) {
        BooleanMatchFilter<T> filter = new BooleanMatchFilter<>( trackedWrapped, notifier);
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        return filter;
    }
    
    public static <T> Wrapper<T> filterMatch(T tracked, Object notifier) {
        BooleanMatchFilter<T> filter = new BooleanMatchFilter<>(tracked, notifier);
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        return filter;
    }
}
