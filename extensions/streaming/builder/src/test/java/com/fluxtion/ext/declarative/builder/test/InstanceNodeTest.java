package com.fluxtion.ext.declarative.builder.test;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.api.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.ext.declarative.builder.helpers.DataEvent;
import com.fluxtion.ext.declarative.builder.helpers.TestResultListener;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.api.lifecycle.EventHandler;
import static com.fluxtion.ext.declarative.api.stream.NumericPredicates.gt;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class InstanceNodeTest extends BaseSepTest {

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
                    select(DataEvent.class).filter(DataEvent::getValue, gt(20))
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
