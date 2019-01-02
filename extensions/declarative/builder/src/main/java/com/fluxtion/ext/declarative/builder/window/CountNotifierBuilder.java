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
package com.fluxtion.ext.declarative.builder.window;

import com.fluxtion.api.generation.GenerationContext;
import com.fluxtion.ext.declarative.api.Test;
import com.fluxtion.ext.declarative.api.window.CountNotifier;
import com.fluxtion.ext.declarative.api.window.UpdateCountTest;

/**
 *
 * @author V12 Technology Ltd.
 */
public class CountNotifierBuilder {

    public static CountNotifier countNotifier(int count, Object trackedInstance) {
        CountNotifier notifier = new CountNotifier(count, trackedInstance);
        GenerationContext.SINGLETON.getNodeList().add(notifier);
        //add to list
        return notifier;
    }

    public static Test updateCount(Object observed, int limit) {
        return updateCount(observed, limit, null);
    }

    public static Test updateCount(Object observed, int limit, Object resetNotifier) {
        Test test = new UpdateCountTest(observed, limit, resetNotifier);
        GenerationContext.SINGLETON.getNodeList().add(test);
        return test;
    }
}
