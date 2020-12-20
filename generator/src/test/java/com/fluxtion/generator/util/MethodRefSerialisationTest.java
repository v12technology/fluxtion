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
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import java.util.Comparator;
import lombok.Data;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
    public void testMethodRefFinal() {
        sep((c) -> {
            c.addPublicNode(new TransformFinal(MethodRefSerialisationTest::myUpperCase), "transform");
        });
        TransformFinal transform = getField("transform");
        onEvent("hello");
        assertThat(transform.out, is("HELLO"));
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
        public void handleString(String in){
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
        public void handleString(String in){
            out = (String) f.apply(in);
        }

    }
    
    public static String myUpperCase(String in){
        return in.toUpperCase();
    }

    @Data
    public static class MyTarget {

        String myName;
    }
}
