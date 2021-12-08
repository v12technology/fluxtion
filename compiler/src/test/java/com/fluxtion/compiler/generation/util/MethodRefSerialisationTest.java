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

import com.fluxtion.runtim.annotations.EventHandler;
import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.annotations.PushReference;
import com.fluxtion.runtim.partition.LambdaReflection;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableFunction;
import lombok.Data;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class MethodRefSerialisationTest extends MultipleSepTargetInProcessTest {

    public MethodRefSerialisationTest(boolean compiledSep) {
        super(compiledSep);
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
            transform.setConsumer(c.addNode(new MyConsumer())::consumeMyPush);
        });
        onEvent("hello");
    }

    @Test
    public void testNodeInstanceMethdRef() {
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
            c.addNode(new FactoryGenerator(MyTarget::new), "factory");
        });
        FactoryGenerator transform = getField("factory");
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

    public static class FactoryGenerator<R> {

        private final LambdaReflection.SerializableSupplier<R> factory;
        private R instance;

        public FactoryGenerator(LambdaReflection.SerializableSupplier<R> factory) {
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

    @Data
    public static class MyPush {
        private LambdaReflection.SerializableConsumer<String> f;
        private String out;
        @PushReference
        private transient Object pushRef;

        public void setConsumer(LambdaReflection.SerializableConsumer<String> f){
            pushRef = f.captured()[0];
            this.f = f;
        }

        @EventHandler
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

    public static class MyConsumer{
        public void consumeMyPush(String in){

        }

        @OnEvent
        public void onEven(){}

    }
}
