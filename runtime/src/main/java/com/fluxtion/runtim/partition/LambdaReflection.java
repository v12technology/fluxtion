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
package com.fluxtion.runtim.partition;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import static java.util.Arrays.asList;
import java.util.Objects;
import java.util.function.*;

/**
 *
 * @author Greg Higgins
 */
public interface LambdaReflection {

    interface MethodReferenceReflection {

        //inspired by: http://benjiweber.co.uk/blog/2015/08/17/lambda-parameter-names-with-reflection/
        default SerializedLambda serialized() {
            try {
                Method replaceMethod = getClass().getDeclaredMethod("writeReplace");
                replaceMethod.setAccessible(true);
                return (SerializedLambda) replaceMethod.invoke(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        default Class<?> getContainingClass(ClassLoader loader) {
            try {
                String className = serialized().getImplClass().replaceAll("/", ".");
                return Class.forName(className, true,  loader);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        default Class<?> getContainingClass() {
            try {
                String className = serialized().getImplClass().replaceAll("/", ".");
                return Class.forName(className );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        default Object[] captured() {
            final SerializedLambda serialized = serialized();
            Object[] args = new Object[serialized.getCapturedArgCount()];
            for (int i = 0; i < serialized.getCapturedArgCount(); i++) {
                args[i] = serialized.getCapturedArg(i);
            }
            return args;
        }
        
        default Method method(ClassLoader loader) {
            SerializedLambda lambda = serialized();
            Class<?> containingClass = getContainingClass(loader);
            return asList(containingClass.getDeclaredMethods())
                    .stream()
                    .filter(method -> Objects.equals(method.getName(), lambda.getImplMethodName()))
                    .findFirst()
                    .orElseThrow(UnableToGuessMethodException::new);
        }
        
        default boolean isDefaultConstructor(){
            return serialized().getImplMethodName().equalsIgnoreCase("<init>");
        }
        
        default Method method() {
            SerializedLambda lambda = serialized();
            Class<?> containingClass = getContainingClass();
            return asList(containingClass.getDeclaredMethods())
                    .stream()
                    .filter(method -> Objects.equals(method.getName(), lambda.getImplMethodName()))
                    .findFirst()
                    .orElseThrow(UnableToGuessMethodException::new);
        }
        
        class UnableToGuessMethodException extends RuntimeException {
        }
    }

    interface SerializableSupplier<t> extends Supplier<t>, Serializable, MethodReferenceReflection {
    }

    interface SerializableConsumer<t> extends Consumer<t>, Serializable, MethodReferenceReflection {
    }

    interface SerializableIntConsumer extends IntConsumer, Serializable, MethodReferenceReflection {
    }

    interface SerializableDoubleConsumer extends DoubleConsumer, Serializable, MethodReferenceReflection {
    }

    interface SerializableLongConsumer extends LongConsumer, Serializable, MethodReferenceReflection {
    }

    interface SerializableBiConsumer<t, u> extends BiConsumer<t, u>, Serializable, MethodReferenceReflection {
    }

    interface SerializableFunction<t, r> extends Function<t, r>, Serializable, MethodReferenceReflection {
    }

    interface SerializableIntFunction<r> extends IntFunction<r>, Serializable, MethodReferenceReflection {
    }

    interface SerializableDoubleFunction<r> extends DoubleFunction<r>, Serializable, MethodReferenceReflection {
    }

    interface SerializableLongFunction<r> extends LongFunction<r>, Serializable, MethodReferenceReflection {
    }

    interface SerializableToIntFunction<t> extends ToIntFunction<t>, Serializable, MethodReferenceReflection {
    }

    interface SerializableIntUnaryOperator extends IntUnaryOperator, Serializable, MethodReferenceReflection {
    }

    interface SerializableDoubleUnaryOperator extends DoubleUnaryOperator, Serializable, MethodReferenceReflection {
    }

    interface SerializableLongUnaryOperator extends LongUnaryOperator, Serializable, MethodReferenceReflection {
    }

    interface SerializableToDoubleFunction<t> extends ToDoubleFunction<t>, Serializable, MethodReferenceReflection {
    }

    interface SerializableToLongFunction<t> extends ToLongFunction<t>, Serializable, MethodReferenceReflection {
    }

    interface SerializableDoubleToIntFunction extends DoubleToIntFunction, Serializable, MethodReferenceReflection {
    }

    interface SerializableLongToIntFunction extends LongToIntFunction, Serializable, MethodReferenceReflection {
    }

    interface SerializableIntToDoubleFunction extends IntToDoubleFunction, Serializable, MethodReferenceReflection {
    }

    interface SerializableLongToDoubleFunction extends LongToDoubleFunction, Serializable, MethodReferenceReflection {
    }

    interface SerializableIntToLongFunction extends IntToLongFunction, Serializable, MethodReferenceReflection {
    }

    interface SerializableDoubleToLongFunction extends DoubleToLongFunction, Serializable, MethodReferenceReflection {
    }

    interface SerializableBiFunction<f, t, r> extends BiFunction<f, t, r>, Serializable, MethodReferenceReflection {
    }

    interface SerializableTriFunction<f, t, u, r> extends TriFunction<f, t, u, r>, Serializable, MethodReferenceReflection {
    }

    interface SerializableQuadFunction<f, t, u, v, r> extends QuadFunction<f, t, u, v, r>, Serializable, MethodReferenceReflection {
    }

    @FunctionalInterface
    interface TriFunction<F, T, U, R> {
        R apply(F f, T t, U u);
    }
    @FunctionalInterface
    interface QuadFunction<F, T, U, V, R> {
        R apply(F f, T t, U u, V v);
    }

    static <T> Method getMethod(LambdaReflection.SerializableConsumer<T> supplier){
        return supplier.method();
    }

    static <T, R> Method getMethod(LambdaReflection.SerializableFunction<T, R> supplier){
        return supplier.method();
    }

    static <T, I, R> Method getMethod(LambdaReflection.SerializableBiFunction<T, I, R> supplier){
        return supplier.method();
    }

}
