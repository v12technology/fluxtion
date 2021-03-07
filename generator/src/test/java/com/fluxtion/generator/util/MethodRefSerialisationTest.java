/*
 * Copyright (c) 2020, V12 Technology Ltd.
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
package com.fluxtion.generator.util;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import lombok.Data;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class MethodRefSerialisationTest extends BaseSepInprocessTest {

    @Test
    public void testMethodRef() {
        sep((c) -> {
            Transform transform = c.addPublicNode(new Transform(), "transform");
            transform.setF(MethodRefSerialisationTest::myUpperCase);
        });
        Transform transform = getField("transform");
        onEvent("hello");
        assertThat(transform.out, is("HELLO"));
    }

    @Test
    public void testInstanceMethodRef() {
        sep((c) -> {
            Transform transform = c.addPublicNode(new Transform(), "transform");
            transform.setF(new InstanceFunction()::myUpperCase);
        });
        Transform transform = getField("transform");
        onEvent("hello");
        assertThat(transform.out, is("HELLO"));
    }

    @Test
    public void testMethodRefFinal() {
        sep((c) -> {
            c.addPublicNode(new TransformFinal(MethodRefSerialisationTest::myUpperCase), "transform");
        });
        TransformFinal transform = getField("transform");
        onEvent("hello");
        assertThat(transform.out, is("HELLO"));
    }

    @Test
    public void testConstructor() {
        sep(c -> {
            c.addNode(new FactoryGeneraor(MyTarget::new), "factory");
        });
        FactoryGeneraor transform = getField("factory");
        assertNull(transform.getInstance());
        onEvent("hello");
        assertNotNull(transform.getInstance());
    }

    public static class Transform {

        private SerializableFunction f;
        private String out;

        public <T, R> SerializableFunction<T, R> getF() {
            return f;
        }

        public <T, R> void setF(SerializableFunction<T, R> f) {
            this.f = f;
        }

        @EventHandler
        public void handleString(String in) {
            out = (String) f.apply(in);
        }

    }

    public static class TransformFinal {

        private final SerializableFunction f;
        private String out;

        public <T, R> TransformFinal(SerializableFunction<T, R> f) {
            this.f = f;
        }

        @EventHandler
        public void handleString(String in) {
            out = (String) f.apply(in);
        }

    }

    public static class FactoryGeneraor<R> {

        private final LambdaReflection.SerializableSupplier<R> factory;
        private R instance;

        public FactoryGeneraor(LambdaReflection.SerializableSupplier<R> factory) {
            this.factory = factory;
        }

        @EventHandler
        public void handleString(String in) {
            instance = factory.get();
        }

        public R getInstance() {
            return instance;
        }
    }

    public static String myUpperCase(String in) {
        return in.toUpperCase();
    }

    @Data
    public static class MyTarget {

        String myName;
    }

    public static class InstanceFunction {

        public String myUpperCase(String in) {
            return in.toUpperCase();
        }
    }
}
