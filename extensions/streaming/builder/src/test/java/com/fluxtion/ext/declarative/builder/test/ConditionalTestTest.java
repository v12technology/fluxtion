/* 
 *  Copyright (C) [2016]-[2017] V12 Technology Limited
 *  
 *  This software is subject to the terms and conditions of its EULA, defined in the
 *  file "LICENCE.txt" and distributed with this software. All information contained
 *  herein is, and remains the property of V12 Technology Limited and its licensors, 
 *  if any. This source code may be protected by patents and patents pending and is 
 *  also protected by trade secret and copyright law. Dissemination or reproduction 
 *  of this material is strictly forbidden unless prior written permission is 
 *  obtained from V12 Technology Limited.  
 */
package com.fluxtion.ext.declarative.builder.test;

import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.ext.streaming.api.EventWrapper;
import com.fluxtion.ext.streaming.api.MergingWrapper;
import static com.fluxtion.ext.streaming.api.MergingWrapper.merge;
import com.fluxtion.ext.streaming.api.Wrapper;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.gt;
import com.fluxtion.ext.streaming.api.stream.StringPredicates;
import com.fluxtion.ext.streaming.builder.event.EventSelect;
import static com.fluxtion.ext.streaming.builder.event.EventSelect.select;
import com.fluxtion.ext.declarative.builder.helpers.FilterResultListener;
import com.fluxtion.ext.declarative.builder.helpers.MyData;
import com.fluxtion.ext.declarative.builder.helpers.MyDataChildNode;
import com.fluxtion.ext.declarative.builder.helpers.MyDataHandler;
import com.fluxtion.ext.declarative.builder.helpers.TestResultListener;
import static com.fluxtion.ext.streaming.builder.stream.StreamBuilder.stream;
import com.fluxtion.generator.util.BaseSepTest;
import net.vidageek.mirror.dsl.Mirror;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


/**
 *
 * @author Greg Higgins
 */
public class ConditionalTestTest extends BaseSepTest {
    
    public int zero(CharSequence f){
        return 0;
    }
    
    ///WORK IN PROGRESS
    public int biMethod(int a, int b){
        return 1;
    }
    
    ///WORK IN PROGRESS
    public static int biMethodStatic(int a, int b){
        return 1;
    }
    
    @org.junit.Test
    public void testStringsEqual() throws Exception {
        buildAndInitSep(StringEqualsBuilder.class);
        TestResultListener results_1 = getField("results_1");
        TestResultListener results_2 = getField("results_2");
        FilterResultListener<MyData> filter_1 = getField("filter_1");
        //results
        MyData data = new MyData(1, 1, "EURUSD");
        sep.onEvent(data);
        assertFalse(results_1.receivedNotification);
        assertTrue(results_2.receivedNotification);
        assertFalse(filter_1.receivedNotification);
        //results
        data = new MyData(1, 1, "EURJPY");
        results_1.reset();
        results_2.reset();
        sep.onEvent(data);
        assertTrue(results_1.receivedNotification);
        assertFalse(results_2.receivedNotification);
        assertTrue(filter_1.receivedNotification);
        Assert.assertEquals("EURJPY", filter_1.wrappedInstance.getStringVal());
    }
    
    public static class StringEqualsBuilder extends SEPConfig {
        {
            EventWrapper<MyData> selectMyData = EventSelect.select(MyData.class);
            addPublicNode(new TestResultListener(selectMyData.filter(MyData::getStringVal, StringPredicates.is("EURJPY"))), "results_1");
            addPublicNode(new TestResultListener(selectMyData.filter(MyData::getStringVal, StringPredicates.is("EURUSD"))), "results_2");
            addPublicNode(new FilterResultListener(selectMyData.filter(MyData::getStringVal, StringPredicates.is("EURJPY"))), "filter_1");
        }
    }

    @org.junit.Test
    public void testSelectAndAlwaysNotify() throws Exception {
        buildAndInitSep(Builder.class);
        TestResultListener results = (TestResultListener) new Mirror().on(sep).get().field("results");
        //results
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(100, 100, "EUR"));
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(190, 100, "EUR"));
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertTrue(results.receivedNotification);
        results.reset();
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertTrue(results.receivedNotification);

    }

    @org.junit.Test
    public void testSelectAndNotifyOnce() throws Exception {
        buildAndInitSep(Builder2.class);
        TestResultListener results = (TestResultListener) new Mirror().on(sep).get().field("results");
        //results
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(100, 100, "EUR"));
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(190, 100, "EUR"));
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertTrue(results.receivedNotification);
        results.reset();
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertFalse(results.receivedNotification);
    }

    @org.junit.Test
    public void testNodeAndNotifyOnce() throws Exception {
        buildAndInitSep(Builder3.class);
        TestResultListener results = (TestResultListener) new Mirror().on(sep).get().field("results");
        //results
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(100, 100, "EUR"));
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(190, 100, "EUR"));
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertTrue(results.receivedNotification);
        results.reset();
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertFalse(results.receivedNotification);
    }

    @org.junit.Test
    public void testEventClassAndNotifyOnce() throws Exception {
        EventHandler sep = buildAndInitSep(Builder4.class);
        TestResultListener results = (TestResultListener) new Mirror().on(sep).get().field("results");
        //results
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(100, 100, "EUR"));
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(190, 100, "EUR"));
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertTrue(results.receivedNotification);
        results.reset();
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertFalse(results.receivedNotification);
    }

    @org.junit.Test
    public void filterNodeAndNotifyOnce() throws Exception {
        EventHandler sep = buildAndInitSep(Builder5.class);
        FilterResultListener results = (FilterResultListener) new Mirror().on(sep).get().field("results");
        //results
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(100, 100, "EUR"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(190, 100, "EUR"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertTrue(results.receivedNotification);
        assertNotNull(results.wrappedInstance);
        results.reset();
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
    }

    @org.junit.Test
    public void filterEventClassAndNotifyOnce() throws Exception {
        EventHandler sep = buildAndInitSep(Builder6.class);
        FilterResultListener results = (FilterResultListener) new Mirror().on(sep).get().field("results");
        //results
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(100, 100, "EUR"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(190, 100, "EUR"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertTrue(results.receivedNotification);
        assertNotNull(results.wrappedInstance);
        results.reset();
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
    }

    @org.junit.Test
    public void testNumberNodeAndNotifyOnce() throws Exception {
        EventHandler sep = buildAndInitSep(Builder7.class);
        TestResultListener results = (TestResultListener) new Mirror().on(sep).get().field("results");
        //results
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(100, 100, "EUR"));
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(190, 100, "EUR"));
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(50, 300, "EUR"));
        assertTrue(results.receivedNotification);
        results.reset();
        sep.onEvent(new MyData(50, 300, "EUR"));
        assertFalse(results.receivedNotification);
    }

    @org.junit.Test
    public void testNodeArray() throws Exception {
        EventHandler sep = buildAndInitSep(BuilderArray1.class);
        TestResultListener results = (TestResultListener) new Mirror().on(sep).get().field("results");
        //results
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(100, 100, "EUR"));
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(190, 100, "EUR"));
        assertFalse(results.receivedNotification);
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertTrue(results.receivedNotification);
        results.reset();
        sep.onEvent(new MyData(5000, 100, "EUR"));
        assertFalse(results.receivedNotification);
    }

    @org.junit.Test
    public void testNodeEventWrapperArrayNotifyOnce() throws Exception {
        EventHandler sep = buildAndInitSep(BuilderArray2.class);
        FilterResultListener results = (FilterResultListener) new Mirror().on(sep).get().field("results");
        //results
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(100, 100, "UC"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(190, 100, "GU"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(5000, 100, "UY"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(5000, 100, "EU"));
        assertTrue(results.receivedNotification);
        assertNotNull(results.wrappedInstance);
        results.reset();
        sep.onEvent(new MyData(5000, 100, "EU"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
    }

    @org.junit.Test
    public void testNodeEventWrapperArrayNotifyAlways() throws Exception {
        EventHandler sep = buildAndInitSep(BuilderArray3.class);
        FilterResultListener results = (FilterResultListener) new Mirror().on(sep).get().field("results");
        //results
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(100, 100, "UC"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(190, 100, "GU"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(5000, 100, "UY"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(5000, 100, "EU"));
        assertTrue(results.receivedNotification);
        assertNotNull(results.wrappedInstance);
        results.reset();
        sep.onEvent(new MyData(5000, 100, "EU"));
        assertTrue(results.receivedNotification);
        assertNotNull(results.wrappedInstance);
    }

    @org.junit.Test
//    @Ignore
    public void testNumberArrayNotifyOnce() throws Exception {
        buildAndInitSep(BuilderArray4.class);
        FilterResultListener<MyDataChildNode> results = (FilterResultListener) new Mirror().on(sep).get().field("results");
        //results
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(100, 100, "UC"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(190, 100, "GU"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(5000, 100, "EU"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(50, 600, "EU"));
        assertTrue(results.receivedNotification);
        assertThat("EU", is(results.wrappedInstance.handler.filterId));
        assertNotNull(results.wrappedInstance);
        results.reset();
        sep.onEvent(new MyData(5000, 10000, "EU"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        results.reset();
        sep.onEvent(new MyData(5000, 10000, "UY"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
    }

    @org.junit.Test
//    @Ignore
    public void testNumberArrayNotifyOnceFilterHelper() throws Exception {
        buildAndInitSep(BuilderArray5.class);
        FilterResultListener<MyData> results = (FilterResultListener) new Mirror().on(sep).get().field("results");
        //results
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(100, 100, "UC"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(190, 100, "GU"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(10, 1000, "EU"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        sep.onEvent(new MyData(500, 600, "EU"));
        assertTrue(results.receivedNotification);
        assertThat("EU", is(results.wrappedInstance.filterString()));
        assertNotNull(results.wrappedInstance);
        results.reset();
        sep.onEvent(new MyData(5000, 10000, "EU"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
        results.reset();
        sep.onEvent(new MyData(5000, 10000, "UY"));
        assertFalse(results.receivedNotification);
        assertNull(results.wrappedInstance);
    }

    public static class Builder extends SEPConfig {

        public Builder() throws Exception {
            EventWrapper<MyData> selectMyData = EventSelect.select(MyData.class);
            Object test = selectMyData.filter(MyData::getIntVal, gt(200));
            addPublicNode(new TestResultListener(test), "results");
        }
    }

    public static class Builder2 extends SEPConfig {

        public Builder2() throws Exception {
            EventWrapper<MyData> selectMyData = select(MyData.class);
            Object test = selectMyData.filter(MyData::getIntVal, gt(200)).notifyOnChange(true);
            addPublicNode(new TestResultListener(test), "results");
        }
    }

    public static class Builder3 extends SEPConfig {

        public Builder3() throws Exception {
            MyDataHandler handler = addNode(new MyDataHandler());
            MyDataChildNode data = addNode(new MyDataChildNode(handler));
            Object test = stream(data).filter(MyDataChildNode::getIntVal, gt(200)).notifyOnChange(true);
            addPublicNode(new TestResultListener(test), "results");
        }
    }

    public static class Builder4 extends SEPConfig {

        public Builder4() throws Exception {
            Object test = select(MyData.class).filter(MyData::getIntVal, gt(200)).notifyOnChange(true);
            addPublicNode(new TestResultListener(test), "results");
        }
    }

    public static class Builder5 extends SEPConfig {

        public Builder5() throws Exception {
            MyDataHandler handler = addNode(new MyDataHandler());
            MyDataChildNode data = addNode(new MyDataChildNode(handler));
            Wrapper<MyDataChildNode> filter = stream(data).filter(MyDataChildNode::getIntVal, gt(200)).notifyOnChange(true);
            addPublicNode(new FilterResultListener(filter), "results");
        }
    }

    public static class Builder6 extends SEPConfig {

        public Builder6() throws Exception {
            Wrapper<MyData> filter = select(MyData.class).filter(MyData::getIntVal, gt(200)).notifyOnChange(true);
            addPublicNode(new FilterResultListener(filter), "results");
        }
    }

    public static class Builder7 extends SEPConfig {

        public Builder7() throws Exception {
            MyDataHandler handler = addNode(new MyDataHandler());
            MyDataChildNode data = addNode(new MyDataChildNode(handler));
            Object test = stream(data).filter(gt(200)).notifyOnChange(true);
            addPublicNode(new TestResultListener(test), "results");
        }
    }

    public static class BuilderArray1 extends SEPConfig {

        public BuilderArray1() throws Exception {
            MyDataHandler handler1 = addNode(new MyDataHandler());
            MyDataHandler handler2 = addNode(new MyDataHandler());
            MyDataChildNode data1 = addNode(new MyDataChildNode(handler1));
            MyDataChildNode data2 = addNode(new MyDataChildNode(handler2));
            Object test = MergingWrapper.merge(data1, data2).filter(MyDataChildNode::getIntVal, gt(200)).notifyOnChange(true);
            addPublicNode(new TestResultListener(test), "results");
        }
    }

    public static class BuilderArray2 extends SEPConfig {

        public BuilderArray2() throws Exception {
            EventWrapper<MyData>[] myDataArr = EventSelect.select(MyData.class, "EU", "EC");
            Wrapper<MyData> filter = MergingWrapper.merge(myDataArr).filter(MyData::getIntVal, gt(200)).notifyOnChange(true);  
            addPublicNode(new FilterResultListener(filter), "results");
        }
    }

    public static class BuilderArray3 extends SEPConfig {

        public BuilderArray3() throws Exception {
            EventWrapper<MyData>[] myDataArr = EventSelect.select(MyData.class, "EU", "EC");;
            Wrapper<MyData> filter = MergingWrapper.merge(myDataArr).filter(MyData::getIntVal, gt(200)).notifyOnChange(false); 
            addPublicNode(new FilterResultListener(filter), "results");
        }
    }

    public static class BuilderArray4 extends SEPConfig {

        public BuilderArray4() throws Exception {
            MyDataHandler handler1 = addNode(new MyDataHandler("EU"));
            MyDataHandler handler2 = addNode(new MyDataHandler("UY"));
            MyDataChildNode data1 = addNode(new MyDataChildNode(handler1));
            MyDataChildNode data2 = addNode(new MyDataChildNode(handler2));
            MyDataChildNode[] dataArr = new MyDataChildNode[]{data1, data2};
//            Wrapper<MyDataChildNode> filter = buildTest(Greater.class, dataArr).arg(200).notifyOnChange(true).buildFilter();
            Wrapper<MyDataChildNode> filter = MergingWrapper.merge(dataArr).filter(gt(200)).notifyOnChange(true);
            addPublicNode(new FilterResultListener(filter), "results");
        }
    }

    public static class BuilderArray5 extends SEPConfig {

        public BuilderArray5() throws Exception {
            Wrapper<MyData> filterOnce = merge(select(MyData.class, "EU", "UY"))
                    .filter(MyData::getIntVal, gt(200)).notifyOnChange(true);
            Wrapper<MyData> filterAll = merge(select(MyData.class))
                    .filter(MyData::getIntVal, gt(200));
//            Wrapper<MyData> filterOnce = filterOnce(MyDataEvent, new String[]{"EU", "UY"}, MyData::getIntVal, GreaterThan, 200);
//            Wrapper<MyData> filterAll = filter(MyDataEvent, MyData::getIntVal, GreaterThan, 200);
            addPublicNode(new FilterResultListener(filterOnce), "results");
        }
    }

}
