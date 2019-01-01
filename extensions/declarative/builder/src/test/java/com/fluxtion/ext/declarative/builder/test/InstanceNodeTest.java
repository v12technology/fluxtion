package com.fluxtion.ext.declarative.builder.test;

import com.fluxtion.api.node.SEPConfig;
import static com.fluxtion.ext.declarative.builder.test.TestBuilder.buildTest;
import com.fluxtion.ext.declarative.builder.util.LambdaReflection.SerializableConsumer;
import com.fluxtion.ext.declarative.builder.helpers.DataEvent;
import com.fluxtion.ext.declarative.builder.helpers.TestResultListener;
import static com.fluxtion.ext.declarative.builder.test.InstanceNodeTest.NumberCompareValidators.gt;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.runtime.lifecycle.EventHandler;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class InstanceNodeTest extends BaseSepTest {

//    @Override
//    protected String testPackageID() {
//        return "";
//    }

    @Test
    public void testSelectValidator() {
        EventHandler sep = buildAndInitSep(ValidatorBuilder_1.class);
        TestResultListener results = getField("results");
        DataEvent de = new DataEvent();
        sep.onEvent(de);
        assertFalse(results.receivedNotification);
        //
        de.value = 50;
        sep.onEvent(de);
        assertTrue(results.receivedNotification);
        //
        results.reset();;
        de.value = 5;
        sep.onEvent(de);
        assertFalse(results.receivedNotification);
    }

    public static final class ValidatorBuilder_1 extends SEPConfig {

        @Override
        public void buildConfig() {
            addPublicNode(new TestResultListener(
                    buildTest(gt(20), DataEvent.class, DataEvent::getValue).build()
            ), "results");

        }

    }

    public static class NumberCompareValidators {

        public final int limit;

        public static NumberCompareValidators limit(int limit) {
            return new NumberCompareValidators(limit);
        }

        public static SerializableConsumer<Integer> gt(int limit) {
            return limit(limit)::greaterThan;
        }

        public static SerializableConsumer<Integer> lt(int limit) {
            return limit(limit)::lessThan;
        }

        public static SerializableConsumer<Integer> eq(int limit) {
            return limit(limit)::equal;
        }

        public NumberCompareValidators(int limit) {
            this.limit = limit;
        }

        public boolean greaterThan(int x) {
            return x > limit;
        }

        public boolean lessThan(int x) {
            return x < limit;
        }

        public boolean equal(int x) {
            return x == limit;
        }

    }

}
