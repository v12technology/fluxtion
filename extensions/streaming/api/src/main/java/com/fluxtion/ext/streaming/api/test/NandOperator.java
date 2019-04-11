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
package com.fluxtion.ext.streaming.api.test;

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.streaming.api.Test;
import java.util.Arrays;

/**
 * Boolean and operator.
 *
 * @author gregp
 */
public class NandOperator extends BaseBooleanOperator {

    public NandOperator(Object[] tracked) {
        super(tracked);
    }

    @OnEvent(dirty = false)
    public boolean testNand() {
        updateCount = (updateCount + testCount());
        boolean ret = updateCount < tracked.length;
        updateCount = 0;
        return ret;
    }

}
