package com.fluxtion.compiler.generation.implicitnodeadd;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.PushReference;
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.ReflectionMethodReference;
import com.fluxtion.runtime.partition.ReflectionMethodReference.ReflectionToDoubleFunction;
import com.fluxtion.runtime.time.FixedRateTrigger;
import lombok.SneakyThrows;
import lombok.Value;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleSupplier;

import static com.fluxtion.compiler.builder.dataflow.DataFlow.subscribe;
import static com.fluxtion.compiler.builder.dataflow.EventStreamBuildTest.NotifyAndPushTarget;
import static com.fluxtion.runtime.partition.LambdaReflection.SerializableDoubleSupplier;
import static com.fluxtion.runtime.partition.LambdaReflection.SerializableToDoubleFunction;
import static org.junit.Assert.assertNotNull;

public class SerializedLambdaTest extends MultipleSepTargetInProcessTest {

    public SerializedLambdaTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void addEnclosingMethodInstanceTest() {
        sep(c -> c.addNode(
                new MyFunctionHolder(new MyInstanceFunction("test")::toCaps), "result"));

        onEvent("test");
        MyFunctionHolder result = getField("result");
        Assert.assertEquals("TEST", result.output);
        Assert.assertFalse(result.triggered);
    }

    @Test
    public void addEnclosingMethodNamedInstanceTest() {
        sep(c -> {
            MyInstanceFunction myInstanceFunction = c.addNode(
                    new MyInstanceFunction("test"), "myInstanceFunction");
            c.addNode(new MyFunctionHolder(myInstanceFunction::toCaps), "result");
        });

        MyInstanceFunction myInstanceFunction = getField("myInstanceFunction");
        assertNotNull(myInstanceFunction);
        onEvent("test");
        MyFunctionHolder result = getField("result");
        Assert.assertEquals("TEST", result.output);
        Assert.assertFalse(result.triggered);
    }

    @Test
    public void addEnclosingMethodMultipleInstanceTest() {
        sep(c -> {

            SerializableFunction<String, String> function = new MyInstanceFunction("test")::toCaps;
            c.addNode(
                    new MyFunctionHolder(function), "result");

            c.addNode(
                    new MyFunctionHolder(function), "result2");
        });

        onEvent("test");
        MyFunctionHolder result = getField("result");
        Assert.assertEquals("TEST", result.output);
        Assert.assertFalse(result.triggered);
    }

    @Test
    public void instanceLambdaWithEventHandlerTest() {
        sep(c -> c.addNode(
                new MyFunctionHolder(new MyInstanceFunctionWithHandler()::toCaps), "result"));

        onEvent("test");
        MyFunctionHolder result = getField("result");
        Assert.assertEquals("test->TEST", result.output);
        Assert.assertTrue(result.triggered);
    }

    @Test
    public void noTriggerInstanceLambdaWithEventHandlerTest() {
        sep(c -> c.addNode(
                new MyNoTriggerFunctionHolder(new MyIntegerInstanceFunctionWithHandler()::toCaps), "result"));

        onEvent("test");
        MyNoTriggerFunctionHolder result = getField("result");
        Assert.assertEquals("->TEST", result.output);
        Assert.assertFalse(result.triggered);

        onEvent(10);
        Assert.assertEquals("->TEST", result.output);
        Assert.assertFalse(result.triggered);
    }

    @Test
    public void instanceLambdaWithEventHandlerDifferentTypesTest() {
        sep(c -> c.addNode(
                new MyFunctionHolder(new MyIntegerInstanceFunctionWithHandler()::toCaps), "result"));

        onEvent("test");
        MyFunctionHolder result = getField("result");
        Assert.assertEquals("->TEST", result.output);
        Assert.assertFalse(result.triggered);

        onEvent(10);
        Assert.assertEquals("->TEST", result.output);
        Assert.assertTrue(result.triggered);
    }

    @Test
    public void instancePushLambdaWithEventHandlerTest() {
        sep(c -> c.addNode(
                new MyPushFunctionHolder(new MyInstanceFunctionWithTriggerAndHandler()::toCaps), "result"));

        List<String> resultList = new ArrayList<>();
        onEvent(resultList);
        Assert.assertTrue(resultList.isEmpty());
        onEvent(20);
        MatcherAssert.assertThat(resultList, IsIterableContainingInOrder.contains("20"));
    }

    @Test
    public void staticMethodReferenceTest() {
        sep(c -> c.addNode(
                new MyFunctionHolder(SerializedLambdaTest::staticToCaps), "result"));

        onEvent("test");
        MyFunctionHolder result = getField("result");
        Assert.assertEquals("TEST", result.output);
    }

    @Test
    public void regressionTest() {
        sep(c -> {
            subscribe(String.class)
//                    .mapToInt(MutableInt::intValue)
                    .mapToInt(Mappers.count()).id("sum")
                    .resetTrigger(new FixedRateTrigger(100))
                    .publishTriggerOverride(new FixedRateTrigger(5))
                    .sink("result")
            ;
        });
    }

    @Test
    public void regressionTriggerPush() throws NoSuchMethodException {
        sep(c -> subscribe(String.class)
                .push(new NotifyAndPushTarget()::setStringPushValue));
    }

    @SneakyThrows
    @Test
    public void reflectionLambdaTest() {
        DoubleSupplier v = new ReflectionMethodReference.ReflectionDoubleSupplier(new Random(10), Random.class.getMethod("nextDouble"));
        System.out.println("random.nextDouble:" + v.getAsDouble());
    }

    @SneakyThrows
    @Test
    public void regressionTriggerPushXX() {
        writeSourceFile = true;
        sep(new RefToDouble(new ReflectionMethodReference.ReflectionDoubleSupplier(new FuncHolder(), "myDoubleFunctionInstance")));
        RefToDouble.d = 2;
        Assert.assertEquals(2.0, RefToDouble.d, 0.00001);
        onEvent("TEST");
        Assert.assertEquals(10.0, RefToDouble.d, 0.00001);
    }

    @Test
    public void toDoubleTest() {
        writeSourceFile = true;
        sep(new InvokeFuncHolder(new ReflectionToDoubleFunction<>(FuncHolder.class, "myDoubleFunctionInstance")));
        InvokeFuncHolder.d = 2;
        Assert.assertEquals(2.0, InvokeFuncHolder.d, 0.00001);
        onEvent(new FuncHolder());
        Assert.assertEquals(10.0, InvokeFuncHolder.d, 0.00001);
    }
    

    public static class InvokeFuncHolder {
        private final SerializableToDoubleFunction function;
        public static double d = Double.NaN;

        public <T> InvokeFuncHolder(SerializableToDoubleFunction<T> function) {
            this.function = function;
        }

        @OnEventHandler()
        @SuppressWarnings("unchecked")
        public boolean injectInstance(FuncHolder instance) {
            d = function.applyAsDouble(instance);
            System.out.println("ans:" + d);
            return false;
        }

    }


    public static class RefToDouble {
        private final SerializableDoubleSupplier func;
        public static double d = Double.NaN;

        public RefToDouble(SerializableDoubleSupplier func) {
            this.func = func;
        }

        @OnEventHandler
        public boolean stringUpdate(String in) {
            d = func.getAsDouble();
            System.out.println("Value:" + func.getAsDouble());
            return true;
        }
    }


    public static class FuncHolder {

        public double myDoubleFunctionInstance() {
            return 10;
        }

        public static double myDoubleFunction() {
            return 10;
        }

        public static double myDoubleFunction(Object o) {
            return 0;
        }
    }

    public static class MyFunctionHolder {

        private final SerializableFunction<String, String> instanceFunction;
        public transient String output;

        public boolean triggered;

        public MyFunctionHolder(SerializableFunction<String, String> instanceFunction) {
            this.instanceFunction = instanceFunction;
        }

        @OnEventHandler
        public boolean processString(String in) {
            output = instanceFunction.apply(in);
            return true;
        }

        @OnTrigger
        public boolean triggered() {
            triggered = true;
            return true;
        }
    }

    public static class MyNoTriggerFunctionHolder {
        @NoTriggerReference
        private final SerializableFunction<String, String> instanceFunction;
        public transient String output;

        public boolean triggered;

        public MyNoTriggerFunctionHolder(SerializableFunction<String, String> instanceFunction) {
            this.instanceFunction = instanceFunction;
        }

        @OnEventHandler
        public boolean processString(String in) {
            output = instanceFunction.apply(in);
            return true;
        }

        @OnTrigger
        public boolean triggered() {
            triggered = true;
            return true;
        }
    }


    public static class MyPushFunctionHolder {
        @PushReference
        private final SerializableFunction<String, String> instanceFunction;
        public transient String output;

        public MyPushFunctionHolder(SerializableFunction<String, String> instanceFunction) {
            this.instanceFunction = instanceFunction;
        }

        @OnEventHandler
        public boolean processString(Integer in) {
            output = instanceFunction.apply("" + in);
            return true;
        }
    }

    @Value
    public static class MyInstanceFunction {

        String myArg;

        public String toCaps(String in) {
            return in.toUpperCase();
        }
    }


    public static class MyInstanceFunctionWithHandler {

        public String myArg = "";

        @OnEventHandler
        public boolean stringUpdate(String in) {
            myArg = in;
            return true;
        }

        public String toCaps(String in) {
            return myArg + "->" + in.toUpperCase();
        }
    }

    public static class MyIntegerInstanceFunctionWithHandler {

        public String myArg = "";

        @OnEventHandler
        public boolean stringUpdate(Integer in) {
            myArg = in.toString();
            return true;
        }

        public String toCaps(String in) {
            return myArg + "->" + in.toUpperCase();
        }
    }

    public static class MyInstanceFunctionWithTriggerAndHandler {

        public List<String> reultList;
        public String asCaps;

        public String triggerResult;

        @OnEventHandler
        public boolean stringUpdate(ArrayList<String> in) {
            reultList = in;
            return true;
        }

        @OnTrigger
        public boolean triggered() {
            reultList.add(asCaps);
            asCaps = "";
            return true;
        }

        public String toCaps(String in) {
            asCaps = in.toUpperCase();
            return asCaps;
        }
    }

    public static String staticToCaps(String in) {
        return in.toUpperCase();
    }
}
