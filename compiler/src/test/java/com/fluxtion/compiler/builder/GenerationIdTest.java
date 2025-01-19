/*
 * Copyright (c) 2025 gregory higgins.
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

package com.fluxtion.compiler.builder;

import com.fluxtion.runtime.EventProcessorBuilderService;
import org.junit.Assert;
import org.junit.Test;

public class GenerationIdTest {

    @Test
    public void increasingSequenceTest() {
        EventProcessorBuilderService builderService = new EventProcessorBuilderServiceImpl();
        Assert.assertEquals(1, builderService.nextSequenceNumber(0));
        Assert.assertEquals(1, builderService.nextSequenceNumber(0));
        Assert.assertEquals(2, builderService.nextSequenceNumber(1));
        Assert.assertEquals(3, builderService.nextSequenceNumber(2));
    }

    @Test
    public void resetSequenceTest() {
        EventProcessorBuilderService builderService = new EventProcessorBuilderServiceImpl();
        Assert.assertEquals(1, builderService.nextSequenceNumber(0));
        Assert.assertEquals(1, builderService.nextSequenceNumber(0));
        Assert.assertEquals(2, builderService.nextSequenceNumber(1));

        EventProcessorBuilderServiceImpl.resetGenerationContext();
        Assert.assertEquals(1, builderService.nextSequenceNumber(2));
    }

    @Test
    public void implicitResetSequenceTest() {
        EventProcessorBuilderService builderService = new EventProcessorBuilderServiceImpl();
        Assert.assertEquals(1, builderService.nextSequenceNumber(0));
        Assert.assertEquals(1, builderService.nextSequenceNumber(0));
        Assert.assertEquals(2, builderService.nextSequenceNumber(1));
        //implicit reset
        Assert.assertEquals(1, builderService.nextSequenceNumber(9));
    }
}
