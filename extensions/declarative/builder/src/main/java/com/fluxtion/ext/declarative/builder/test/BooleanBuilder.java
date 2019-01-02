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
import com.fluxtion.ext.declarative.api.test.BooleanFilter;
import com.fluxtion.ext.declarative.api.test.NotOperator;
import com.fluxtion.ext.declarative.api.test.OrOperator;
import com.fluxtion.ext.declarative.api.test.XorOperator;

/**
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
        OrOperator and = new OrOperator(tracked);
        return GenerationContext.SINGLETON.addOrUseExistingNode(and);
    }

    public static Test xor(Object... tracked) {
        XorOperator and = new XorOperator(tracked);
        return GenerationContext.SINGLETON.addOrUseExistingNode(and);
    }

    public static Test nor(Object... tracked) {
        return not(or(tracked));
    }

    public static Test nand(Object... tracked) {
        return not(and(tracked));
    }
    
    public static <T> BooleanFilter<T> filter(Wrapper<T> trackedWrapped, Object notifier) {
        BooleanFilter<T> filter = new BooleanFilter<>( trackedWrapped, notifier);
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        return filter;
    }
    
    public static <T> BooleanFilter<T> filter(T tracked, Object notifier) {
        BooleanFilter<T> filter = new BooleanFilter<>(tracked, notifier);
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        return filter;
    }
}
