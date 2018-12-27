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
package com.fluxtion.extension.declarative.builder.test;

import com.fluxtion.api.generation.GenerationContext;
import com.fluxtion.extension.declarative.api.Test;
import com.fluxtion.extension.declarative.api.test.AndOperator;
import com.fluxtion.extension.declarative.api.test.NotOperator;
import com.fluxtion.extension.declarative.api.test.OrOperator;
import com.fluxtion.extension.declarative.api.test.XorOperator;

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
}
