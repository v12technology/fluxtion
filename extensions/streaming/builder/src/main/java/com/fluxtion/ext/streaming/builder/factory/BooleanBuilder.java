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
package com.fluxtion.ext.streaming.builder.factory;

import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.streaming.api.Test;
import com.fluxtion.ext.streaming.api.test.AndOperator;
import com.fluxtion.ext.streaming.api.test.NandOperator;
import com.fluxtion.ext.streaming.api.test.NorOperator;
import com.fluxtion.ext.streaming.api.test.NotOperator;
import com.fluxtion.ext.streaming.api.test.OrOperator;
import com.fluxtion.ext.streaming.api.test.XorOperator;

/**
 * Factory class for building Test notifications based upon boolean operations on events
 * emitted by tracked nodes. Tests can be used in conjunction with {@link FilterByNotificationBuilder}
 * to create complex filtering rules.
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

}
