package com.fluxtion.compiler.generation.function;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.partition.LambdaReflection;

public class UnaryFunction<T, R> {

    private final LambdaReflection.SerializableSupplier<T> supplier;
    private final LambdaReflection.SerializableFunction<T, R> function;
    private final LambdaReflection.SerializableToIntFunction<T> intFunction;
    private transient Object source;
    private int intResult;
    private R objectResult;

    public UnaryFunction(LambdaReflection.SerializableSupplier<T> supplier, LambdaReflection.SerializableFunction<T, R> function) {
        this.supplier = supplier;
        this.function = function;
        this.intFunction = null;
        source = supplier.captured()[0];
    }

    public UnaryFunction(LambdaReflection.SerializableSupplier<T> supplier, LambdaReflection.SerializableToIntFunction<T> intFunction) {
        this.supplier = supplier;
        this.intFunction = intFunction;
        this.function = null;
        source = supplier.captured()[0];
    }

    @OnTrigger
    public boolean calculate() {
        T t = supplier.get();
        if (intFunction != null) {
            intResult = intFunction.applyAsInt(supplier.get());
        } else {
            objectResult = function.apply(supplier.get());
        }
        return true;
    }

    public int getIntResult() {
        return intResult;
    }

    public R getObjectResult() {
        return objectResult;
    }
}
