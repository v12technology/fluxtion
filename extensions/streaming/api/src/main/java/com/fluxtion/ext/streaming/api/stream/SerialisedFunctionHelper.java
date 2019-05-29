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
package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.function.Function;

/**
 *
 * @author V12 Technology Ltd.
 */
public class SerialisedFunctionHelper {

    private static final String ROOT_DIR_DEFAULT = "src/main/resources";
    private static final String ROOT_DIR_TEST = "src/test/resources";
    public static boolean isTest = false;
    private static String ROOT_DIR = ROOT_DIR_DEFAULT;
    private static final String FUNCTION_DIR = "lambda.ser";
    private static int count;

    private static void serialise(Serializable s, String name) throws IOException {
        final File outDir = new File(isTest?ROOT_DIR_TEST:ROOT_DIR, FUNCTION_DIR);
        Files.createDirectories(outDir.toPath());
        OutputStream os = new FileOutputStream(new File(outDir, name));
        try (ObjectOutputStream ois = new ObjectOutputStream(os)) {
            ois.writeObject(s);
        }
    }

    private static <T> T deserialise(String lambdaName) throws IOException, ClassNotFoundException {
        InputStream is = SerialisedFunctionHelper.class.getClassLoader().getResourceAsStream(FUNCTION_DIR + "/" + lambdaName);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(is);
        } catch (Exception exception) {
            final File outDir = new File(isTest?ROOT_DIR_TEST:ROOT_DIR, FUNCTION_DIR);
            ois = new ObjectInputStream(new FileInputStream(new File(outDir, lambdaName)));
        }
        return (T) ois.readObject();
    }

    public static <S, B> LambdaFunction<S, B> addLambda(SerializableFunction<S, B> func, String dir) {
        ROOT_DIR = dir;
        LambdaFunction<S, B> addLambda = addLambda(func);
        ROOT_DIR = ROOT_DIR_DEFAULT;
        return addLambda;
    }

    public static <S, B> LambdaFunction<S, B> addLambda(SerializableFunction<S, B> func) {
        LambdaFunction<S, B> retFunction = null;
        if (isLambda(func)) {
            String serName = func.serialized().getImplMethodName() +"_"+ count++;
            try {
                serialise(func, serName);
                retFunction = new LambdaFunction<>(serName);
                retFunction.delegate = deserialise(serName);
            } catch (Exception ex) {
                throw new RuntimeException("cannot serialise lambda function:" + func, ex);
            }
        }
        return retFunction;
    }

    private static boolean isLambda(LambdaReflection.MethodReferenceReflection methodRef) {
        int modifiers = methodRef.method().getModifiers();
        final boolean isLambda = Modifier.isStatic(modifiers) && Modifier.isPrivate(modifiers);
        return isLambda;
    }

    public static class LambdaFunction<T, R> implements Function<T, R> {

        private final String fileName;
        private Function<T, R> delegate;

        public LambdaFunction(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public R apply(T t) {
            return delegate.apply(t);
        }

        @Initialise
        public void init() {
            try {
                delegate = deserialise(fileName);
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException("cannot deserialise lambda function:" + fileName, ex);
            }
        }

    }

}
