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
package com.fluxtion.compiler.generation.util;

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.PushReference;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import lombok.Data;
import lombok.Value;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


/**
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class MethodRefSerialisationTest extends MultipleSepTargetInProcessTest {

    public static final String SUCCESS = "success";

    public MethodRefSerialisationTest(boolean compiledSep) {
        super(compiledSep);
    }

    private static String result;

    @Test
    public void testRunnableMethodRef() {
        sep(c -> c.addNode(new RunnableExecutor(MethodRefSerialisationTest::failingTask)));
        onEvent("fail please");
        assertThat(result, is(SUCCESS));
    }

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
    public void testInstanceMethodRefConsumer() {
        sep((c) -> {
            MyPush transform = c.addPublicNode(new MyPush(), "transform");
            transform.setConsumer(new MyConsumer()::consumeMyPush);
        });
        onEvent("hello");
    }

    @Test
    public void testNodeInstanceMethodRef() {
        sep((c) -> {
            Transform transform = c.addPublicNode(new Transform(), "transform");
            final InstanceFunction instanceFunction = c.addNode(new InstanceFunction());
            instanceFunction.setPrefix("prefix");
            transform.setF(instanceFunction::prefixString);
        });
        Transform transform = getField("transform");
        onEvent("hello");
        assertThat(transform.out, is("prefixhello"));
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
            c.addNode(new FactoryGenerator<>(MyTarget::new), "factory");
        });
        FactoryGenerator<Object> transform = getField("factory");
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

        @OnEventHandler
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

        @OnEventHandler
        public void handleString(String in) {
            out = (String) f.apply(in);
        }

    }

    public static class FactoryGenerator<R> {

        private final LambdaReflection.SerializableSupplier<R> factory;
        private R instance;

        public FactoryGenerator(LambdaReflection.SerializableSupplier<R> factory) {
            this.factory = factory;
        }

        @OnEventHandler
        public void handleString(String in) {
            instance = factory.get();
        }

        public R getInstance() {
            return instance;
        }
    }

    @Data
    public static class MyPush {

        private LambdaReflection.SerializableConsumer<String> f;

        @PushReference
        private String out;

        public void setConsumer(LambdaReflection.SerializableConsumer<String> f) {
            this.f = f;
        }

        @OnEventHandler
        public void handleString(String in) {
            f.accept(in);
        }

    }

    public static String myUpperCase(String in) {
        return in.toUpperCase();
    }

    @Data
    public static class MyTarget {

        String myName;
    }

    @Data
    public static class InstanceFunction {

        private String prefix;

        public String myUpperCase(String in) {
            return in.toUpperCase();
        }

        public String prefixString(String in) {
            return prefix + in;
        }


    }

    public static class MyConsumer {
        public void consumeMyPush(String in) {

        }

        @OnTrigger
        public void onEven() {
        }

    }

    @Value
    public static class RunnableExecutor {
        LambdaReflection.SerializableRunnable runnable;

        @OnEventHandler
        public void executeTask(String in) {
            runnable.run();
        }

    }

    public static void failingTask() {
        result = SUCCESS;
    }
}
