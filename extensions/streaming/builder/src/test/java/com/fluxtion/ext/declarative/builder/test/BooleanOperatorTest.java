package com.fluxtion.ext.declarative.builder.test;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.ext.streaming.api.EventWrapper;
import com.fluxtion.ext.streaming.api.Test;
import com.fluxtion.ext.streaming.builder.event.EventSelect;
import static com.fluxtion.ext.streaming.builder.test.BooleanBuilder.not;
import static com.fluxtion.ext.streaming.builder.test.BooleanBuilder.and;
import static com.fluxtion.ext.streaming.builder.test.BooleanBuilder.nand;
import static com.fluxtion.ext.streaming.builder.test.BooleanBuilder.or;
import static com.fluxtion.ext.streaming.builder.test.BooleanBuilder.xor;
import com.fluxtion.ext.declarative.builder.helpers.MyData;
import com.fluxtion.ext.declarative.builder.helpers.TestResultListener;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.ext.streaming.api.Wrapper;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.gt;
import net.vidageek.mirror.dsl.Mirror;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static com.fluxtion.ext.streaming.builder.test.BooleanBuilder.nor;

/**
 *
 * @author gregp
 */
public class BooleanOperatorTest extends BaseSepTest {
 
    @org.junit.Test
    public void testNot() throws Exception {
        EventHandler sep = buildAndInitSep(Builder.class);
        TestResultListener results = (TestResultListener) new Mirror().on(sep).get().field("results");
        //results
        assertFalse(results.receivedNotification);

        results.reset();
        sep.onEvent(new MyData(100, 100, "EUR"));
        assertTrue(results.receivedNotification);

        results.reset();
        sep.onEvent(new MyData(190, 100, "EUR"));
        assertTrue(results.receivedNotification);

        results.reset();
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertFalse(results.receivedNotification);

        results.reset();
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertFalse(results.receivedNotification);

    }

    @org.junit.Test
    public void testAnd() throws Exception {
        EventHandler sep = buildAndInitSep(BuilderAnd.class);
        TestResultListener resultsAnd = (TestResultListener) new Mirror().on(sep).get().field("results");
        TestResultListener resultsNand = (TestResultListener) new Mirror().on(sep).get().field("resultsNand");
        TestResultListener resultsOr = (TestResultListener) new Mirror().on(sep).get().field("resultsOr");
        TestResultListener resultsNorManual = (TestResultListener) new Mirror().on(sep).get().field("resultsNorManual");
        TestResultListener resultsNorAuto = (TestResultListener) new Mirror().on(sep).get().field("resultsNorAuto");
        TestResultListener resultsXor = (TestResultListener) new Mirror().on(sep).get().field("resultsXor");
        //results
        assertFalse(resultsAnd.receivedNotification);

        resultsAnd.reset();
        resultsNand.reset();
        resultsOr.reset();
        resultsNorManual.reset();
        resultsNorAuto.reset();
        resultsXor.reset();
        sep.onEvent(new MyData(10, 100, "EUR"));
        assertFalse(resultsAnd.receivedNotification);
        assertTrue(resultsNand.receivedNotification);
        assertFalse(resultsOr.receivedNotification);
        assertFalse(resultsXor.receivedNotification);
        assertFalse(resultsNorManual.receivedNotification);
        assertTrue(resultsNorAuto.receivedNotification);

        resultsAnd.reset();
        resultsNand.reset();
        resultsOr.reset();
        resultsNorManual.reset();
        resultsNorAuto.reset();
        resultsXor.reset();
        sep.onEvent(new MyData(10000, 100, "EUR"));
        assertTrue(resultsAnd.receivedNotification);
        assertFalse(resultsNand.receivedNotification);
        assertTrue(resultsOr.receivedNotification);
        assertFalse(resultsXor.receivedNotification);
        assertFalse(resultsNorManual.receivedNotification);
        assertFalse(resultsNorAuto.receivedNotification);

        resultsAnd.reset();
        resultsNand.reset();
        resultsOr.reset();
        resultsNorManual.reset();
        resultsNorAuto.reset();
        resultsXor.reset();
        sep.onEvent(new MyData(190, 100, "EUR"));
        assertFalse(resultsAnd.receivedNotification);
        assertTrue(resultsNand.receivedNotification);
        assertFalse(resultsOr.receivedNotification);
        assertFalse(resultsXor.receivedNotification);
        assertFalse(resultsXor.receivedNotification);
        assertFalse(resultsNorManual.receivedNotification);
        assertTrue(resultsNorAuto.receivedNotification);

        resultsAnd.reset();
        resultsNand.reset();
        resultsOr.reset();
        resultsNorManual.reset();
        resultsNorAuto.reset();
        resultsXor.reset();
        sep.onEvent(new MyData(750, 100, "EUR"));
        assertFalse(resultsAnd.receivedNotification);
        assertTrue(resultsNand.receivedNotification);
        assertTrue(resultsOr.receivedNotification);
        assertTrue(resultsXor.receivedNotification);
        assertFalse(resultsNorManual.receivedNotification);
        assertFalse(resultsNorAuto.receivedNotification);

        resultsAnd.reset();
        resultsNand.reset();
        resultsOr.reset();
        resultsNorManual.reset();
        resultsNorAuto.reset();
        resultsXor.reset();
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertTrue(resultsAnd.receivedNotification);
        assertFalse(resultsNand.receivedNotification);
        assertTrue(resultsOr.receivedNotification);
        assertFalse(resultsXor.receivedNotification);
        assertFalse(resultsNorManual.receivedNotification);
        assertFalse(resultsNorAuto.receivedNotification);

    }

    public static class Builder extends SEPConfig {

        public Builder() throws Exception {
            EventWrapper<MyData> selectMyData = EventSelect.select(MyData.class);
            Wrapper<MyData> test = selectMyData.filter(MyData::getIntVal, gt(200));
            Test not = not(test);
            addPublicNode(new TestResultListener(not), "results");
        }

    }

    public static class BuilderAnd extends SEPConfig {

        public BuilderAnd() throws Exception {
            EventWrapper<MyData> selectMyData = EventSelect.select(MyData.class);
            Wrapper<MyData> test_200 = selectMyData.filter(MyData::getIntVal, gt(200));
            Wrapper<MyData> test_500 = selectMyData.filter(MyData::getIntVal, gt(500));
            Wrapper<MyData> test_1000 = selectMyData.filter(MyData::getIntVal, gt(1000));
            Test and = and(test_200, test_500, test_1000);
            Test nand = nand(test_200, test_500, test_1000);
            Test or = or(test_200, test_500, test_1000);
            Test xor = xor(test_200, test_500, test_1000);
            Test nor_manual = not(or);
            Test nor_auto = nor(test_200, test_500, test_1000);
            addPublicNode(new TestResultListener(and), "results");
            addPublicNode(new TestResultListener(nand), "resultsNand");
            addPublicNode(new TestResultListener(or), "resultsOr");
            addPublicNode(new TestResultListener(nor_manual), "resultsNorManual");
            addPublicNode(new TestResultListener(nor_auto), "resultsNorAuto");
            addPublicNode(new TestResultListener(xor), "resultsXor");
        }
    }

}
