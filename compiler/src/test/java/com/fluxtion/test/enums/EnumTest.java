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
package com.fluxtion.test.enums;

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class EnumTest extends MultipleSepTargetInProcessTest {

    public EnumTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testEnum() {
        sep((c) -> c.addPublicNode(new DayProcessor(), "dayProcessor").firsDayOfWeek = DayOfWeek.MONDAY);
        DayProcessor dp = getField("dayProcessor");
        Assert.assertEquals(dp.firsDayOfWeek, DayOfWeek.MONDAY);
    }

    public static class DayProcessor {

        public DayOfWeek firsDayOfWeek;

        @OnEventHandler
        public void endOfDay(EndOfDayEvent eod) {
        }

    }

}
