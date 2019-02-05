package com.fluxtion.ext.declarative.builder.filter2;

import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.declarative.builder.test.TestBuilder;
import com.fluxtion.ext.declarative.builder.util.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.declarative.builder.util.LambdaReflection.SerializableSupplierNew;

/**
 *
 * @author V12 Technology Ltd.
 */
public class FilterBuilder {

    public static <T, R extends Boolean> void filterNew(SerializableFunction<T, R> filter, SerializableSupplierNew<?> supplier) {
//        Method method = filter.method();
//        if (Modifier.isStatic(method.getModifiers())) {
//            System.out.print("static filter -> ");
//        } else {
//            System.out.print("instance filter -> ");
//        }
//        System.out.println("" + method.getName());
//        //supplier
//        Method supplyMethod = supplier.method();
//        System.out.println("supply method -> "+ supplyMethod.getName());
        TestBuilder.buildTestNew(filter, supplier).buildFilter();
    }

//    public static <T extends Number, R extends Boolean> void filterNum( SerializableFunction<T, R> filter, SerializableSupplierNew<?> supplier) {
//        filterNew(filter, supplier);
//    }

    public static boolean filterweekDay(String day) {
        return true;
    }

    public static String getDay() {
        return "Tuesday";
    }

    public static void main(String[] args) {
        GenerationContext.setupStaticContext("fff", "erer", null, null);
        FilterBuilder.filterNew(FilterBuilder::filterweekDay, FilterBuilder::getDay);
    }

}
