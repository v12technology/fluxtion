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
package com.fluxtion.ext.declarative.api.util;

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.stream.SerialisedFunctionHelper.LambdaFunction;
import static com.fluxtion.ext.streaming.api.stream.SerialisedFunctionHelper.addLambda;
import java.lang.reflect.Modifier;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class LambdaTest {

    @Test
    public void testSerLambda() {
        LambdaFunction<String, Boolean> lambda = addLambda((String s) -> "hello".equals(s), "src/test/resources");
        assertTrue(lambda.apply("hello"));
        assertFalse(lambda.apply("rtrtrt"));
    }

    @Test
    @Ignore
    public void testLambda() {
        SerializableFunction filter = (s) -> true;
        assertFalse(isLambda(LambdaTest::validateStatic));
        assertFalse(isLambda(this::validateInstance));
        assertTrue(isLambda(filter));
        assertTrue(isLambda(LambdaTest::validatePrivateStatic));
    }

    public boolean validateInstance(String s) {
        return true;
    }

    public static boolean validateStatic(String s) {
        return true;
    }

    private static boolean validatePrivateStatic(String s) {
        return true;
    }

    private <S> boolean isLambda(SerializableFunction<S, Boolean> func) {
        int modifiers = func.method().getModifiers();
        final boolean isLambda = Modifier.isStatic(modifiers) && Modifier.isPrivate(modifiers);
        return isLambda;
    }

}
