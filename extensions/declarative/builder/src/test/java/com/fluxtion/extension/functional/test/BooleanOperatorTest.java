package com.fluxtion.extension.functional.test;

import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.ext.declarative.api.EventWrapper;
import com.fluxtion.ext.declarative.api.Test;
import com.fluxtion.extension.declarative.builder.event.EventSelect;
import static com.fluxtion.extension.declarative.builder.test.BooleanBuilder.not;
import static com.fluxtion.extension.declarative.builder.test.BooleanBuilder.and;
import static com.fluxtion.extension.declarative.builder.test.BooleanBuilder.nand;
import static com.fluxtion.extension.declarative.builder.test.BooleanBuilder.or;
import static com.fluxtion.extension.declarative.builder.test.BooleanBuilder.xor;
import static com.fluxtion.extension.declarative.builder.test.TestBuilder.buildTest;
import com.fluxtion.extension.functional.helpers.MyData;
import com.fluxtion.extension.functional.helpers.TestResultListener;
import com.fluxtion.extension.functional.helpers.Tests;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.runtime.lifecycle.EventHandler;
import net.vidageek.mirror.dsl.Mirror;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static com.fluxtion.extension.declarative.builder.test.BooleanBuilder.nor;

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
        assertTrue(resultsNorManual.receivedNotification);
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
        assertTrue(resultsNorManual.receivedNotification);
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
            Test test = buildTest(Tests.Greater.class, selectMyData, MyData::getIntVal)
                    .arg(200).build();
            Test not = not(test);
            addPublicNode(new TestResultListener(not), "results");
        }

    }

    public static class BuilderAnd extends SEPConfig {

        public BuilderAnd() throws Exception {
            EventWrapper<MyData> selectMyData = EventSelect.select(MyData.class);
            Test test_200 = buildTest(Tests.Greater.class, selectMyData, MyData::getIntVal)
                    .arg(200).build();
            Test test_500 = buildTest(Tests.Greater.class, selectMyData, MyData::getIntVal)
                    .arg(500).build();
            Test test_1000 = buildTest(Tests.Greater.class, selectMyData, MyData::getIntVal)
                    .arg(1000).build();
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
