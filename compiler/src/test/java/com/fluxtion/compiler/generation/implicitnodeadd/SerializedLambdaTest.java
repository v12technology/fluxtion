package com.fluxtion.compiler.generation.implicitnodeadd;

import com.fluxtion.compiler.builder.stream.EventStreamBuildTest.NotifyAndPushTarget;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.PushReference;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.helpers.Mappers;
import com.fluxtion.runtime.time.FixedRateTrigger;
import lombok.Value;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;

public class SerializedLambdaTest extends MultipleSepTargetInProcessTest {

    public SerializedLambdaTest(boolean compiledSep) {
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
    public void addEnclosingMethodMultipleInstanceTest() {
        writeSourceFile = true;
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
    public void regressionTriggerPush() {
        sep(c -> subscribe(String.class)
                .push(new NotifyAndPushTarget()::setStringPushValue));
    }


    public static class MyFunctionHolder {
        private final SerializableFunction<String, String> instanceFunction;
        public transient String output;

        public boolean triggered;

        public MyFunctionHolder(SerializableFunction<String, String> instanceFunction) {
            this.instanceFunction = instanceFunction;
        }

        @OnEventHandler
        public void processString(String in) {
            output = instanceFunction.apply(in);
        }

        @OnTrigger
        public void triggered() {
            triggered = true;
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
        public void processString(String in) {
            output = instanceFunction.apply(in);
        }

        @OnTrigger
        public void triggered() {
            triggered = true;
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
        public void processString(Integer in) {
            output = instanceFunction.apply("" + in);
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
        public void stringUpdate(String in) {
            myArg = in;
        }

        public String toCaps(String in) {
            return myArg + "->" + in.toUpperCase();
        }
    }

    public static class MyIntegerInstanceFunctionWithHandler {

        public String myArg = "";

        @OnEventHandler
        public void stringUpdate(Integer in) {
            myArg = in.toString();
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
        public void stringUpdate(ArrayList<String> in) {
            reultList = in;
        }

        @OnTrigger
        public void triggered() {
            reultList.add(asCaps);
            asCaps = "";
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
