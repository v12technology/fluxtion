package com.fluxtion.compiler.generation.exportfunction;

import com.fluxtion.runtime.partition.LambdaReflection;
import org.junit.Test;

import java.lang.reflect.Method;

public class MethodEquivalentTest {
    public static boolean equal(Method method1, Method other) {
        if (method1.getName().equals(other.getName())) {
            if (!method1.getReturnType().equals(other.getReturnType()))
                return false;
            return equalParamTypes(method1.getParameterTypes(), other.getParameterTypes());
        }
        return false;
    }

    static boolean equalParamTypes(Class<?>[] params1, Class<?>[] params2) {
        /* Avoid unnecessary cloning */
        if (params1.length == params2.length) {
            for (int i = 0; i < params1.length; i++) {
                if (params1[i] != params2[i])
                    return false;
            }
            return true;
        }
        return false;
    }

    @Test
    public void testMethod() {
        LambdaReflection.SerializableBiConsumer<ClassA, String> aMethod = ClassA::method1;
        LambdaReflection.SerializableBiConsumer<ClassB, String> bMethod = ClassB::method1;
        equal(aMethod.method(), bMethod.method());
    }


    public interface IntShared<T> {
        void method1(String a);

        void method2(T a);
    }

    public static class ClassA implements IntShared<Integer> {
        @Override
        public void method1(String a) {

        }

        @Override
        public void method2(Integer a) {

        }
    }

    public static class ClassB implements IntShared<Number> {
        @Override
        public void method1(String a) {

        }

        @Override
        public void method2(Number a) {

        }
    }
}
