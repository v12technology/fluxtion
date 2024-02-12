package com.fluxtion.runtime.partition;

import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectionMethodReference implements LambdaReflection.MethodReferenceReflection {
    private final Method method;

    public ReflectionMethodReference(Method method) {
        this.method = method;
    }

    @Override
    public Method method(ClassLoader loader) {
        return method;
    }

    @Override
    public Method method() {
        return method;
    }

    public static class ReflectionDoubleSupplier implements LambdaReflection.SerializableDoubleSupplier {

        protected final Method method;
        protected final boolean staticMethod;
        protected final Object instance;

        public ReflectionDoubleSupplier(Method method) {
            this.method = method;
            this.staticMethod = Modifier.isStatic(method.getModifiers());
            instance = null;
        }

        public ReflectionDoubleSupplier(Object instance, Method method) {
            this.method = method;
            this.staticMethod = Modifier.isStatic(method.getModifiers());
            this.instance = instance;
        }

        @SneakyThrows
        public ReflectionDoubleSupplier(Object instance, String methodName) {
            this.method = instance.getClass().getMethod(methodName);
            this.staticMethod = Modifier.isStatic(method.getModifiers());
            this.instance = instance;
        }

        @SneakyThrows
        public ReflectionDoubleSupplier(Class<?> clazz, String methodName) {
            this.method = clazz.getMethod(methodName);
            this.staticMethod = Modifier.isStatic(method.getModifiers());
            this.instance = null;
        }

        @Override
        public Object[] captured() {
            return instance == null ? new Object[0] : new Object[]{instance};
        }

        @Override
        public Class<?> getContainingClass(ClassLoader loader) {
            return method.getDeclaringClass();
        }

        @Override
        public Class<?> getContainingClass() {
            return method.getDeclaringClass();
        }

        @Override
        public boolean isDefaultConstructor() {
            return false;
        }

        @Override
        public Method method(ClassLoader loader) {
            return method;
        }

        @Override
        public Method method() {
            return method;
        }

        @Override
        @SneakyThrows
        public double getAsDouble() {
            if (staticMethod) {
                return (double) method.invoke(null);
            } else {
                return (double) method.invoke(instance);
            }
        }
    }

    public static class ReflectionToDoubleFunction<T> extends ReflectionDoubleSupplier implements LambdaReflection.SerializableToDoubleFunction<T> {
        public ReflectionToDoubleFunction(Method method) {
            super(method);
        }

        public ReflectionToDoubleFunction(Object instance, Method method) {
            super(instance, method);
        }

        public ReflectionToDoubleFunction(Object instance, String methodName) {
            super(instance, methodName);
        }

        public ReflectionToDoubleFunction(Class<?> clazz, String methodName) {
            super(clazz, methodName);
        }

        @Override
        @SneakyThrows
        public double applyAsDouble(T value) {
            if (staticMethod) {
                return (double) method.invoke(null);
            } else {
                return (double) method.invoke(value);
            }
        }
    }

}
