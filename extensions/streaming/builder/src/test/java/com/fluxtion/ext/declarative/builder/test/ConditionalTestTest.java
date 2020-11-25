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

import com.fluxtion.ext.declarative.builder.helpers.FilterResultListener;
import com.fluxtion.ext.declarative.builder.helpers.MyData;
import com.fluxtion.ext.declarative.builder.helpers.MyDataChildNode;
import com.fluxtion.ext.declarative.builder.helpers.MyDataHandler;
import com.fluxtion.ext.declarative.builder.helpers.TestResultListener;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.MergingWrapper;
import static com.fluxtion.ext.streaming.api.MergingWrapper.merge;
import com.fluxtion.ext.streaming.api.Wrapper;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.gt;
import com.fluxtion.ext.streaming.api.stream.StringPredicates;
import com.fluxtion.ext.streaming.builder.factory.EventSelect;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.stream.StreamOperatorService.stream;
import net.vidageek.mirror.dsl.Mirror;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Greg Higgins
 */
public class ConditionalTestTest extends StreamInprocessTest {

    public int zero(CharSequence f) {
        return 0;
    }

    ///WORK IN PROGRESS
    public int biMethod(int a, int b) {
        return 1;
    }

    ///WORK IN PROGRESS
    public static int biMethodStatic(int a, int b) {
        return 1;
    }

    @org.junit.Test
    public void testStringsEqual() throws Exception {
        sep(c -> {
            Wrapper<MyData> selectMyData = EventSelect.select(MyData.class);
            c.addPublicNode(new TestResultListener(selectMyData.filter(MyData::getStringVal, StringPredicates.is("EURJPY"))), "results_1");
            c.addPublicNode(new TestResultListener(selectMyData.filter(MyData::getStringVal, StringPredicates.is("EURUSD"))), "results_2");
            c.addPublicNode(new FilterResultListener(selectMyData.filter(MyData::getStringVal, StringPredicates.is("EURJPY"))), "filter_1");

        });
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

    @org.junit.Test
    public void testSelectAndAlwaysNotify() throws Exception {
        sep(c -> {
            Wrapper<MyData> selectMyData = EventSelect.select(MyData.class);
            Object test = selectMyData.filter(MyData::getIntVal, gt(200));
            c.addPublicNode(new TestResultListener(test), "results");
        });
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
        sep(c -> {
            Wrapper<MyData> selectMyData = select(MyData.class);
            Object test = selectMyData.filter(MyData::getIntVal, gt(200)).notifyOnChange(true);
            c.addPublicNode(new TestResultListener(test), "results");
        });
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
        sep(c -> {
            MyDataHandler handler = c.addNode(new MyDataHandler());
            MyDataChildNode data = c.addNode(new MyDataChildNode(handler));
            Object test = stream(data).filter(MyDataChildNode::getIntVal, gt(200)).notifyOnChange(true);
            c.addPublicNode(new TestResultListener(test), "results");
        });
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
        sep(c -> {
            Object test = select(MyData.class).filter(MyData::getIntVal, gt(200)).notifyOnChange(true);
            c.addPublicNode(new TestResultListener(test), "results");
        });
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
        sep(c -> {
            MyDataHandler handler = c.addNode(new MyDataHandler());
            MyDataChildNode data = c.addNode(new MyDataChildNode(handler));
            Wrapper<MyDataChildNode> filter = stream(data).filter(MyDataChildNode::getIntVal, gt(200)).notifyOnChange(true);
            c.addPublicNode(new FilterResultListener(filter), "results");
        });
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
        sep(c -> {
            Wrapper<MyData> filter = select(MyData.class).filter(MyData::getIntVal, gt(200)).notifyOnChange(true);
            c.addPublicNode(new FilterResultListener(filter), "results");
        });
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
        sep(c -> {
            MyDataHandler handler = c.addNode(new MyDataHandler());
            MyDataChildNode data = c.addNode(new MyDataChildNode(handler));
            Object test = stream((Number) data).filter(gt(200)).notifyOnChange(true);
            c.addPublicNode(new TestResultListener(test), "results");
        });
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
        sep(c -> {
            MyDataHandler handler1 = c.addNode(new MyDataHandler());
            MyDataHandler handler2 = c.addNode(new MyDataHandler());
            MyDataChildNode data1 = c.addNode(new MyDataChildNode(handler1));
            MyDataChildNode data2 = c.addNode(new MyDataChildNode(handler2));
            Object test = MergingWrapper.merge(data1, data2).filter(MyDataChildNode::getIntVal, gt(200)).notifyOnChange(true);
            c.addPublicNode(new TestResultListener(test), "results");
        });
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
    public void testNodeWrapperArrayNotifyOnce() throws Exception {
        sep(c -> {
            Wrapper<MyData>[] myDataArr = EventSelect.select(MyData.class, "EU", "EC");
            Wrapper<MyData> filter = MergingWrapper.merge(myDataArr).filter(MyData::getIntVal, gt(200)).notifyOnChange(true);
            c.addPublicNode(new FilterResultListener(filter), "results");
        });
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
    public void testNodeWrapperArrayNotifyAlways() throws Exception {
        sep(c -> {
            Wrapper<MyData>[] myDataArr = EventSelect.select(MyData.class, "EU", "EC");
            Wrapper<MyData> filter = MergingWrapper.merge(myDataArr).filter(MyData::getIntVal, gt(200)).notifyOnChange(false);
            c.addPublicNode(new FilterResultListener(filter), "results");
        });
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
        sep(c -> {
            MyDataHandler handler1 = c.addNode(new MyDataHandler("EU"));
            MyDataHandler handler2 = c.addNode(new MyDataHandler("UY"));
            MyDataChildNode data1 = c.addNode(new MyDataChildNode(handler1));
            MyDataChildNode data2 = c.addNode(new MyDataChildNode(handler2));
            MyDataChildNode[] dataArr = new MyDataChildNode[]{data1, data2};
//            Wrapper<MyDataChildNode> filter = buildTest(Greater.class, dataArr).arg(200).notifyOnChange(true).buildFilter();
            Wrapper<MyDataChildNode> filter = MergingWrapper.merge(dataArr).filter(MyDataChildNode::doubleValue, gt(200)).notifyOnChange(true);
            c.addPublicNode(new FilterResultListener(filter), "results");
        });
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
        sep(c -> {
            Wrapper<MyData> filterOnce = merge(select(MyData.class, "EU", "UY"))
                .filter(MyData::getIntVal, gt(200)).notifyOnChange(true);
            Wrapper<MyData> filterAll = merge(select(MyData.class))
                .filter(MyData::getIntVal, gt(200));
//            Wrapper<MyData> filterOnce = filterOnce(MyDataEvent, new String[]{"EU", "UY"}, MyData::getIntVal, GreaterThan, 200);
//            Wrapper<MyData> filterAll = filter(MyDataEvent, MyData::getIntVal, GreaterThan, 200);
            c.addPublicNode(new FilterResultListener(filterOnce), "results");
        });
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

}
