package com.fluxtion.ext.declarative.builder.filter2;

import com.fluxtion.ext.declarative.builder.test.TestBuilder;
import com.fluxtion.ext.declarative.builder.util.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.declarative.builder.util.LambdaReflection.SerializableSupplierNew;
import java.lang.reflect.Method;

/**
 *
 * @author V12 Technology Ltd.
 */
public class FilterBuilder {

    public static <T, R extends Boolean> void filterNew(SerializableFunction<T, R> filter, SerializableSupplierNew<T> supplier) {
        TestBuilder.buildTestNew(filter, supplier, false).buildFilter();
    }
    
    public static <T, R extends Boolean, S> void filterNew(SerializableFunction<T, R> filter, S supplier, Method accessor) {
        TestBuilder.buildTestNew(filter, supplier, accessor, false).buildFilter();
    }

    /**
     * filters numeric values, providing casts as necessary. Java generic types
     * cannot handle multiple primitive types in a generic method, the wildcard
     * type allows any numeric type to be passed in.
     * @param <T>
     * @param <R>
     * @param filter
     * @param supplier 
     */
    public static <T extends Number, R extends Boolean> void filterNum( SerializableFunction<T, R> filter, SerializableSupplierNew<?> supplier) {
        TestBuilder.buildTestNew(filter, supplier, true).buildFilter();
    }

}
