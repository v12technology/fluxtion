/*
 * SPDX-FileCopyrightText: © 2025 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import org.junit.Assert;
import org.junit.Test;

import static com.fluxtion.compiler.builder.dataflow.EventStreamBuildTest.NotifyAndPushTarget;

public class MultiplePushTargetsTest extends MultipleSepTargetInProcessTest {
    public MultiplePushTargetsTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void pushMultipleReference() {
        sep(c -> {
            DataFlow.subscribe(String.class)
                    .push(
                            new NotifyAndPushTarget("A")::setStringPushValue,
                            new NotifyAndPushTarget("B")::setStringPushValue,
                            new NotifyAndPushTarget("C")::setStringPushValue,
                            new NotifyAndPushTarget("D")::setStringPushValue);
        });

        NotifyAndPushTarget A = getField("A");
        NotifyAndPushTarget B = getField("B");
        NotifyAndPushTarget C = getField("C");
        NotifyAndPushTarget D = getField("D");

        Assert.assertEquals(0, A.getOnEventCount());
        Assert.assertEquals(0, B.getOnEventCount());
        Assert.assertEquals(0, C.getOnEventCount());
        Assert.assertEquals(0, D.getOnEventCount());

        onEvent("test");
        Assert.assertEquals(1, A.getOnEventCount());
        Assert.assertEquals(1, B.getOnEventCount());
        Assert.assertEquals(1, C.getOnEventCount());
        Assert.assertEquals(1, D.getOnEventCount());

        Assert.assertEquals("test", A.getStringPushValue());
        Assert.assertEquals("test", B.getStringPushValue());
        Assert.assertEquals("test", C.getStringPushValue());
        Assert.assertEquals("test", D.getStringPushValue());
    }

    @Test
    public void pushMultipleDouble() {
        sep(c -> {
            DataFlow.subscribe(String.class)
                    .mapToDouble(Double::parseDouble)
                    .push(
                            new NotifyAndPushTarget("A")::setDoublePushValue,
                            new NotifyAndPushTarget("B")::setDoublePushValue,
                            new NotifyAndPushTarget("C")::setDoublePushValue,
                            new NotifyAndPushTarget("D")::setDoublePushValue);
        });

        NotifyAndPushTarget A = getField("A");
        NotifyAndPushTarget B = getField("B");
        NotifyAndPushTarget C = getField("C");
        NotifyAndPushTarget D = getField("D");

        Assert.assertEquals(0, A.getOnEventCount());
        Assert.assertEquals(0, B.getOnEventCount());
        Assert.assertEquals(0, C.getOnEventCount());
        Assert.assertEquals(0, D.getOnEventCount());

        onEvent("20.5");
        Assert.assertEquals(1, A.getOnEventCount());
        Assert.assertEquals(1, B.getOnEventCount());
        Assert.assertEquals(1, C.getOnEventCount());
        Assert.assertEquals(1, D.getOnEventCount());

        Assert.assertEquals(20.5, A.getDoublePushValue(), 0.0001);
        Assert.assertEquals(20.5, B.getDoublePushValue(), 0.0001);
        Assert.assertEquals(20.5, C.getDoublePushValue(), 0.0001);
        Assert.assertEquals(20.5, D.getDoublePushValue(), 0.0001);
    }

    @Test
    public void pushMultipleInt() {
        sep(c -> {
            DataFlow.subscribe(String.class)
                    .mapToInt(Mappers::parseInt)
                    .push(
                            new NotifyAndPushTarget("A")::setIntPushValue,
                            new NotifyAndPushTarget("B")::setIntPushValue,
                            new NotifyAndPushTarget("C")::setIntPushValue,
                            new NotifyAndPushTarget("D")::setIntPushValue);
        });

        NotifyAndPushTarget A = getField("A");
        NotifyAndPushTarget B = getField("B");
        NotifyAndPushTarget C = getField("C");
        NotifyAndPushTarget D = getField("D");

        Assert.assertEquals(0, A.getOnEventCount());
        Assert.assertEquals(0, B.getOnEventCount());
        Assert.assertEquals(0, C.getOnEventCount());
        Assert.assertEquals(0, D.getOnEventCount());

        onEvent("20");
        Assert.assertEquals(1, A.getOnEventCount());
        Assert.assertEquals(1, B.getOnEventCount());
        Assert.assertEquals(1, C.getOnEventCount());
        Assert.assertEquals(1, D.getOnEventCount());

        Assert.assertEquals(20, A.getIntPushValue());
        Assert.assertEquals(20, B.getIntPushValue());
        Assert.assertEquals(20, C.getIntPushValue());
        Assert.assertEquals(20, D.getIntPushValue());
    }

    @Test
    public void pushMultipleLong() {
        sep(c -> {
            DataFlow.subscribe(String.class)
                    .mapToLong(Mappers::parseLong)
                    .push(
                            new NotifyAndPushTarget("A")::setLongPushValue,
                            new NotifyAndPushTarget("B")::setLongPushValue,
                            new NotifyAndPushTarget("C")::setLongPushValue,
                            new NotifyAndPushTarget("D")::setLongPushValue);
        });

        NotifyAndPushTarget A = getField("A");
        NotifyAndPushTarget B = getField("B");
        NotifyAndPushTarget C = getField("C");
        NotifyAndPushTarget D = getField("D");

        Assert.assertEquals(0, A.getOnEventCount());
        Assert.assertEquals(0, B.getOnEventCount());
        Assert.assertEquals(0, C.getOnEventCount());
        Assert.assertEquals(0, D.getOnEventCount());

        onEvent("20");
        Assert.assertEquals(1, A.getOnEventCount());
        Assert.assertEquals(1, B.getOnEventCount());
        Assert.assertEquals(1, C.getOnEventCount());
        Assert.assertEquals(1, D.getOnEventCount());

        Assert.assertEquals(20L, A.getLongPushValue());
        Assert.assertEquals(20L, B.getLongPushValue());
        Assert.assertEquals(20L, C.getLongPushValue());
        Assert.assertEquals(20L, D.getLongPushValue());
    }
}
