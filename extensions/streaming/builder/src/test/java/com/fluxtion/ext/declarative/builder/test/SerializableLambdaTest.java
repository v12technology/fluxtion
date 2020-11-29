package com.fluxtion.ext.declarative.builder.test;

import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.api.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;
import static com.fluxtion.builder.generation.GenerationContext.SINGLETON;
import com.fluxtion.ext.declarative.builder.helpers.StaticFunctions;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.generator.util.BaseSepTest;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;
import java.util.function.Function;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class SerializableLambdaTest {

    @Test
    @Ignore
    public void lmabdaTest1() {
        StaticFunctions sf_0 = new StaticFunctions();
        StaticFunctions sf_1 = new StaticFunctions();
        ref(StaticFunctions::add, null);
        ref(StaticFunctions::intToString, null);
        ref(sf_0::instanceMethod, sf_0);
        ref(sf_1::instanceMethod, sf_1);

    }

    @Test
    public void staticSupplierTest() {
        StaticFunctions sf_1 = new StaticFunctions();
        instanceSupplierMethod(sf_1::getInt);
        staticSupplierMethod(StaticFunctions::getInt);
        SerializableSupplier s = (sf_1::getInt);
    }

    @Test
    public void staticConsumerTest() {
        StaticFunctions sf_0 = new StaticFunctions();
        consumerMethod(StaticFunctions::add);
        consumerMethod(sf_0::instanceMethod);
        consumerMethod(sf_0::setint);
    }

    @Test
    public void readWriteTest() {
        StaticFunctions sf_0 = new StaticFunctions();
        buildTest_v2(sf_0::getInt, sf_0::setint);
    }


    public <C> void buildTest_v2(SerializableSupplier<C> supplier, SerializableConsumer<C> rule) {

    }

    public void instanceSupplierMethod(LambdaReflection.SerializableSupplier s) {
//    public void instanceSupplierMethod(LambdaReflection.SerializableSupplier s){

    }

    public < T, S> void staticSupplierMethod(Function<S, T> f) {

    }

    public <T> void consumerMethod(LambdaReflection.SerializableConsumer<T> s) {
//    public void instanceSupplierMethod(LambdaReflection.SerializableSupplier s){

    }

    public < S> void consumerMethod(Consumer<S> s) {
//    public void instanceSupplierMethod(LambdaReflection.SerializableSupplier s){

    }

    @Test
    @Ignore
    public void lambdaGetterTest() {
        StaticFunctions sf_1 = new StaticFunctions();
        supplier(sf_1::getInt, sf_1);
//        supplier(StaticFunctions::getInt, sf_1);
    }

    @Test
    @Ignore
    public void lambdaWrapperTest() {
        StaticFunctions sf_1 = new StaticFunctions();
        WrapFunctions wrapper = new WrapFunctions();
        wrapper.functions = sf_1;

        supplierWrapped(StaticFunctions::getInt, wrapper);

    }

    public static <T> LambdaReflection.SerializableConsumer<T> ref(LambdaReflection.SerializableConsumer<T> r, Object instance) {
        if (!Modifier.isStatic(r.method(SINGLETON.getClassLoader()).getModifiers())) {
            assertEquals(instance, r.captured()[0]);
        } else {
            assertEquals(0, r.captured().length);
        }
        return r;
    }

    public static <T> void supplier(LambdaReflection.SerializableSupplier<T> supplier, T instance) {
        if (!Modifier.isStatic(supplier.method(SINGLETON.getClassLoader()).getModifiers())) {
            assertEquals(instance, supplier.captured()[0]);
        } else {
            assertEquals(0, supplier.captured().length);
        }

    }

    public static < T, S> void supplierWrapped(Function<T, S> supplier, Wrapper<T> instance) {
//        if (!Modifier.isStatic(supplier.method().getModifiers())) {
//            assertEquals(instance, supplier.captured()[0]);
//        }else{
//            assertEquals(0, supplier.captured().length);
//        }

    }

    public static class WrapFunctions implements Wrapper<StaticFunctions> {

        public StaticFunctions functions;

        @Override
        public StaticFunctions event() {
            return functions;
        }

        @Override
        public Class<StaticFunctions> eventClass() {
            return StaticFunctions.class;
        }

    }

}
