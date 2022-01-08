/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler.generation.targets;

import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.test.event.TestEventNoId;
import com.fluxtion.test.event.TestEventNoIdHandler;
import com.fluxtion.test.tracking.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import java.lang.reflect.Field;

import static com.fluxtion.compiler.generation.targets.JavaGeneratorNames.*;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Greg Higgins
 */
public class JavaTargetTestIT {

    @Test
    public void test_1NoFilter() throws Exception {
        //System.out.println("test_1NoFilter");
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(Test1NoFilter);
        ((Lifecycle) handler).init();
        TestEventNoId event = new TestEventNoId();
        assertEquals(0, event.value);
        handler.onEvent(event);
        handler.onEvent(event);
        handler.onEvent(event);
        handler.onEvent(event);
        assertEquals(4, event.value);

        Field[] allFields = FieldUtils.getAllFields(handler.getClass());
        TestEventNoIdHandler eventHandler = (TestEventNoIdHandler) FieldUtils.readField(allFields[0], handler, true);
        assertEquals(4, eventHandler.count);
    }

    @Test
    public void test1NoIdEventFilter() throws Exception {
        //System.out.println("test1NoIdEventFilter");
        Object newInstance = JavaTestGeneratorHelper.sepInstance(Test1Filtered);
    }

    @Test
    public void trace_int_0_test1() throws Exception {
        //System.out.println("trace_int_0_test1");
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(trace_int_0_test1);
        StaticEventProcessor handlerInline = JavaTestGeneratorHelper.sepInstanceInline(trace_int_0_test1);
        //filter matches on 10, check trace
        TraceEvent_InFilter_0 event = new TraceEvent_InFilter_0(10);
        handler.onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "A0", "A1", "aggregator");
        //no filter match
        event = new TraceEvent_InFilter_0(11);
        handler.onEvent(event);
        assertEquals(0, event.traceList.size());
        //ensure root field exists
        JavaTestGeneratorHelper.testPublicField(handler, "aggregator");
    }

    @Test
    public void trace_int_0_test2() throws Exception {
        //System.out.println("trace_int_0_test2");
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(trace_int_0_test2);
        //ensure root field exists
        JavaTestGeneratorHelper.testPublicField(handler, "aggregator");
        //filter matches on 10, check trace
        TraceEvent_InFilter_0 event = new TraceEvent_InFilter_0(10);
        handler.onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "A0", "A1", "aggregator");
        //no filter match
        event = new TraceEvent_InFilter_0(11);
        handler.onEvent(event);
        assertEquals(0, event.traceList.size());
        //filter matches on 10, check trace
        event = new TraceEvent_InFilter_0(20);
        handler.onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "B0", "B1", "aggregator");
    }

    @Test
    public void trace_int_0_test3() throws Exception {
        //System.out.println("trace_int_0_test3");
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(trace_int_0_test3);
        //ensure root field exists
        JavaTestGeneratorHelper.testPublicField(handler, "aggregator");
        //filter matches on 10, check trace
        TraceEvent_InFilter_0 event = new TraceEvent_InFilter_0(10);
        handler.onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "A0", "A1", "aggregator", "D0");
        //filter matches on 10, check trace
        event = new TraceEvent_InFilter_0(20);
        handler.onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "B0", "B1", "aggregator", "D0");
        //no filter match
        event = new TraceEvent_InFilter_0(11);
        handler.onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "D0");

    }

    @Test
    public void trace_int_0_test4() throws Exception {
        //System.out.println("trace_int_0_test4");
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(trace_int_0_test4);
        //ensure root field exists
        JavaTestGeneratorHelper.testPublicField(handler, "aggregator");
        //filter matches on 10, check trace
        TraceEvent_InFilter_0 event = new TraceEvent_InFilter_0(10);
        handler.onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Extends_Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "A0", "A1", "aggregator", "D0");
        //filter matches on 10, check trace
        event = new TraceEvent_InFilter_0(20);
        handler.onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "B0", "B1", "aggregator", "D0");
        //no filter match
        event = new TraceEvent_InFilter_0(11);
        handler.onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "D0");

    }

    @Test
    public void trace_int_0_test5() throws Exception {
        //System.out.println("trace_int_0_test5");
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(trace_int_0_test5);
        //ensure root field exists
        JavaTestGeneratorHelper.testPublicField(handler, "aggregator");
        //filter matches on 10, check trace
        TraceEvent_InFilter_0 event = new TraceEvent_InFilter_0(10);
        handler.onEvent(event);

        JavaTestGeneratorHelper.testTraceIdContains(event.traceIdList, "A0", "A1", "aggregator", "ANY-1");
        //no filter matches on 20, check trace
        event = new TraceEvent_InFilter_0(20);
        handler.onEvent(event);
        JavaTestGeneratorHelper.testTraceIdContains(event.traceIdList, "DEF-0", "DEF-1", "aggregator", "ANY-1");
    }

    @Test
    public void trace_subclass_test1() throws Exception {
        //System.out.println("trace_subclass_test1");
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(trace_subclass_test1);
        //ensure root field exists
        JavaTestGeneratorHelper.testPublicField(handler, "aggregator");
        //filter matches on 10, check trace
        TraceEvent_InFilter_0 event = new TraceEvent_InFilter_0(10);
        handler.onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Extends_Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "A0", "A1", "aggregator", "D0");
        //filter matches on 10, check trace
        event = new TraceEvent_InFilter_0(20);
        handler.onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "B0", "B1", "aggregator", "D0");
        //no filter match
        event = new TraceEvent_InFilter_0(11);
        handler.onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "D0");
        //test subclasses
        Event te = new TraceEvent.TraceEvent_sub1(12);
        handler.onEvent(te);
        JavaTestGeneratorHelper.testClassOrder(((TraceEvent.TraceEvent_sub1) te).getTraceList(),
                TraceEventHolder.TraceEventHandler_sub1.class,
                TraceEventHolderChild.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(((TraceEvent.TraceEvent_sub1) te).getTraceIdList(), "sub1", "sub1.1");
        //test subclasses
        te = new TraceEvent.TraceEvent_sub2(12);
        handler.onEvent(te);
        JavaTestGeneratorHelper.testClassOrder(((TraceEvent.TraceEvent_sub2) te).getTraceList(),
                TraceEventHolder.TraceEventHandler_sub2.class,
                TraceEventHolderChild.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(((TraceEvent.TraceEvent_sub2) te).getTraceIdList(), "sub2", "sub2.1");

    }

    @Test
    public void trace_diamond_test1() throws Exception {
        //System.out.println("trace_diamond_test1");
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(trace_diamond_test1);
        Event te = new TraceEvent.TraceEvent_sub1(222);
        handler.onEvent(te);
//        JavaTestGeneratorHelper.testTraceIdOrder(((TraceEvent.TraceEvent_sub1)te).getTraceIdList(), "sub1", "sub1.1");
        te = new TraceEvent.TraceEvent_sub2(50);
        handler.onEvent(te);
        JavaTestGeneratorHelper.testTraceIdOrder(((TraceEvent.TraceEvent_sub2) te).getTraceIdList(), "B0", "AB1", "AB3");

    }

    @Test
    public void trace_dirty_test1() throws Exception {
        //System.out.println("trace_dirty_test1");
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(trace_dirty_test1);
        //
        TraceEvent.TraceEvent_sub1 te = new TraceEvent.TraceEvent_sub1(1);
        te.strValue = "A1";
        handler.onEvent(te);
        JavaTestGeneratorHelper.testTraceIdOrder(te.getTraceIdList(), "A0", "A1");
        //
        te.reset();
        te.strValue = "no match";
        handler.onEvent(te);
        JavaTestGeneratorHelper.testTraceIdContains(te.getTraceIdList(),
                "A0", "A1", "A2", "A3", "AB1", "AB3","AB4", "AB5");
        //
        TraceEvent.TraceEvent_sub2 te_2 = new TraceEvent.TraceEvent_sub2(2);
        te_2.strValue = "B1";
        handler.onEvent(te_2);
        JavaTestGeneratorHelper.testTraceIdOrder(te_2.getTraceIdList(), "B0", "B1");
        //
        te_2.reset();
        te_2.strValue = "no match";
        handler.onEvent(te_2);
        JavaTestGeneratorHelper.testTraceIdContains(te_2.getTraceIdList(),
                "B0", "B1", "AB1", "AB3","AB4", "AB5");
        
    }

    @Test
    public void trace_eventlifecycle_test1() throws Exception {
        //System.out.println("trace_eventlifecycle_test1");
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(trace_eventlifecycle_test1);
        //
        TraceEvent.TraceEvent_sub1 te = new TraceEvent.TraceEvent_sub1(1);
        handler.onEvent(te);
        JavaTestGeneratorHelper.testTraceIdOrder(te.getTraceIdList(), "A0", "A1", "A2", "A3");
        JavaTestGeneratorHelper.testTraceIdOrder(te.getTraceAfterEventIdList(), "A2");
        JavaTestGeneratorHelper.testTraceIdOrder(te.getTraceEventCompleteIdList(), "A3", "A1");
        //
        TraceEvent.TraceEvent_sub1 te_2 = new TraceEvent.TraceEvent_sub1(2);
        handler.onEvent(te_2);
        JavaTestGeneratorHelper.testTraceIdOrder(te_2.getTraceIdList(), "B0", "B1", "B2", "B3");
        JavaTestGeneratorHelper.testTraceIdOrder(te_2.getTraceAfterEventIdList(), "B3", "B1");
        JavaTestGeneratorHelper.testTraceIdOrder(te_2.getTraceEventCompleteIdList(), "B2");  
    }

    @Test
    public void trace_0_test1() throws Exception {
        //System.out.println("trace_0_test1");
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(trace_0_test1);
        //ensure root field exists
        JavaTestGeneratorHelper.testPublicField(handler, "aggregator");
        //filter matches on 10, check trace
        TraceEvent_0 event = new TraceEvent_0();
        handler.onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_0.class,
                Node_TraceEvent_0.class,
                Node_TraceEvent_Aggregator.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "A0", "A1", "aggregator");
    }

}
