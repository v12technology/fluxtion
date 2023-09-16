package com.fluxtion.compiler.generation.eventdispatch;

import com.fluxtion.compiler.generation.targets.JavaTestGeneratorHelper;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.test.event.TestEventNoId;
import com.fluxtion.test.event.TestEventNoIdFilteredHandler;
import com.fluxtion.test.event.TestEventNoIdHandler;
import com.fluxtion.test.tracking.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventDispatchTraceTest extends CompiledAndInterpretedSepTest {
    public EventDispatchTraceTest(SepTestConfig sepTestConfig) {
        super(sepTestConfig);
    }

    @Test
    public void tracePipelineTest() {
        sep(c -> {
            Handler_TraceEvent_0 handler = (new Handler_TraceEvent_0("A0"));
            Node_TraceEvent_0 node_1 = (new Node_TraceEvent_0("A1", handler));
            c.addPublicNode(new Node_TraceEvent_Aggregator("aggregator", node_1), "aggregator");
        });
        //filter matches on 10, check trace
        TraceEvent_0 event = new TraceEvent_0();
        onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_0.class,
                Node_TraceEvent_0.class,
                Node_TraceEvent_Aggregator.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "A0", "A1", "aggregator");
    }

    @Test
    public void simpleEventNoFilterTest() {
        sep(c -> c.addNode(new TestEventNoIdHandler(), "handler"));
        TestEventNoId event = new TestEventNoId();
        assertEquals(0, event.value);
        onEvent(event);
        onEvent(event);
        onEvent(event);
        onEvent(event);
        assertEquals(4, event.value);
        TestEventNoIdHandler eventHandler = getField("handler");
        assertEquals(4, eventHandler.count);
    }

    @Test
    public void simpleEventWithFilterTest() {
        sep(c -> c.addNode(new TestEventNoIdFilteredHandler(25), "handler"));
        TestEventNoId event = new TestEventNoId();
    }

    @Test
    public void pipelineWithFilterTest() {
        sep(c -> {
            Handler_TraceEvent_InFilter_0 handler = new Handler_TraceEvent_InFilter_0("A0", 10);
            Node_TraceEvent_IntFilter_0 node_1 = new Node_TraceEvent_IntFilter_0("A1", handler);
            c.addPublicNode(new Node_TraceEvent_Aggregator("aggregator", node_1), "aggregator");
        });
        Node_TraceEvent_Aggregator aggregator = getField("aggregator");
        TraceEvent_InFilter_0 event = new TraceEvent_InFilter_0(10);
        onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "A0", "A1", "aggregator");
        //no filter match
        event = new TraceEvent_InFilter_0(11);
        onEvent(event);
        assertEquals(0, event.traceList.size());
    }

    @Test
    public void graphWithFilterTest() {
        sep(c -> {
            Handler_TraceEvent_InFilter_0 handler_1 = new Handler_TraceEvent_InFilter_0("A0", 10);
            Node_TraceEvent_IntFilter_0 node_1 = new Node_TraceEvent_IntFilter_0("A1", handler_1);
            //
            Handler_TraceEvent_InFilter_0 handler_2 = new Handler_TraceEvent_InFilter_0("B0", 20);
            Node_TraceEvent_IntFilter_0 node_2 = new Node_TraceEvent_IntFilter_0("B1", handler_2);
            //
            c.addPublicNode(new Node_TraceEvent_Aggregator("aggregator", node_1, node_2), "aggregator");
        });
        Node_TraceEvent_Aggregator aggregator = getField("aggregator");
        //filter matches on 10, check trace
        TraceEvent_InFilter_0 event = new TraceEvent_InFilter_0(10);
        onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "A0", "A1", "aggregator");
        //no filter match
        event = new TraceEvent_InFilter_0(11);
        onEvent(event);
        assertEquals(0, event.traceList.size());
        //filter matches on 10, check trace
        event = new TraceEvent_InFilter_0(20);
        onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "B0", "B1", "aggregator");
    }

    @Test
    public void graphWithFilterAndNoFilterTest() {
        sep(c -> {
            Handler_TraceEvent_InFilter_0 handler_1 = (new Handler_TraceEvent_InFilter_0("A0", 10));
            Node_TraceEvent_IntFilter_0 node_1 = (new Node_TraceEvent_IntFilter_0("A1", handler_1));
            //
            Handler_TraceEvent_InFilter_0 handler_2 = (new Handler_TraceEvent_InFilter_0("B0", 20));
            Node_TraceEvent_IntFilter_0 node_2 = (new Node_TraceEvent_IntFilter_0("B1", handler_2));
            //
            HandlerNoFilter_TraceEvent_InFilter_0 allEventHandler = c.addNode(new HandlerNoFilter_TraceEvent_InFilter_0("D0"));
            //
            c.addPublicNode(new Node_TraceEvent_Aggregator("aggregator", node_1, node_2), "aggregator");
        });
        Node_TraceEvent_Aggregator aggregator = getField("aggregator");
        //filter matches on 10, check trace
        TraceEvent_InFilter_0 event = new TraceEvent_InFilter_0(10);
        onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "A0", "A1", "aggregator", "D0");
        //filter matches on 10, check trace
        event = new TraceEvent_InFilter_0(20);
        onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "B0", "B1", "aggregator", "D0");
        //no filter match
        event = new TraceEvent_InFilter_0(11);
        onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "D0");
    }

    @Test
    public void graphWithFilterAndNoFilterAndInheritanceTest() {
        sep(c -> {
            Extends_Handler_TraceEvent_InFilter_0 handler_1 = (new Extends_Handler_TraceEvent_InFilter_0("A0", 10));
            Node_TraceEvent_IntFilter_0 node_1 = (new Node_TraceEvent_IntFilter_0("A1", handler_1));
            //
            Handler_TraceEvent_InFilter_0 handler_2 = (new Handler_TraceEvent_InFilter_0("B0", 20));
            Node_TraceEvent_IntFilter_0 node_2 = (new Node_TraceEvent_IntFilter_0("B1", handler_2));
            //
            HandlerNoFilter_TraceEvent_InFilter_0 allEventHandler = c.addNode(new HandlerNoFilter_TraceEvent_InFilter_0("D0"));
            //
            c.addPublicNode(new Node_TraceEvent_Aggregator("aggregator", node_1, node_2), "aggregator");
        });
        Node_TraceEvent_Aggregator aggregator = getField("aggregator");
        //filter matches on 10, check trace
        TraceEvent_InFilter_0 event = new TraceEvent_InFilter_0(10);
        onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Extends_Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "A0", "A1", "aggregator", "D0");
        //filter matches on 10, check trace
        event = new TraceEvent_InFilter_0(20);
        onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "B0", "B1", "aggregator", "D0");
        //no filter match
        event = new TraceEvent_InFilter_0(11);
        onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "D0");
    }

    @Test
    public void graphWithFilterAndNoFilterMatchingTest() {
        sep(c -> {
            Handler_TraceEvent_InFilter_0 handler_1 = (new Handler_TraceEvent_InFilter_0("A0", 10));
            TraceEventHolderChild node_1 = (new TraceEventHolderChild("A1", handler_1));
            //
            Handler_UnMatchedFilter_TraceEvent_InFilter_0 def_1 = (new Handler_UnMatchedFilter_TraceEvent_InFilter_0("DEF-0"));
            TraceEventHolderChild def_2 = (new TraceEventHolderChild("DEF-1", def_1));
            //
            HandlerNoFilter_TraceEvent_InFilter_0 allEventHandler = (new HandlerNoFilter_TraceEvent_InFilter_0("ANY-1"));
            //
            c.addPublicNode(new Node_TraceEventHolder_Aggregator("aggregator", node_1, def_2, allEventHandler), "aggregator");

        });

        //filter matches on 10, check trace
        TraceEvent_InFilter_0 event = new TraceEvent_InFilter_0(10);
        onEvent(event);

        JavaTestGeneratorHelper.testTraceIdContains(event.traceIdList, "A0", "A1", "aggregator", "ANY-1");
        //no filter matches on 20, check trace
        event = new TraceEvent_InFilter_0(20);
        onEvent(event);
        JavaTestGeneratorHelper.testTraceIdContains(event.traceIdList, "DEF-0", "DEF-1", "aggregator", "ANY-1");
    }

    @Test
    public void subclassEventTest() {
        sep(c -> {
            TraceEventHolder subHandler1 = (new TraceEventHolder.TraceEventHandler_sub1("sub1", 12));
            TraceEventHolder subNode_1 = c.addNode(new TraceEventHolderChild("sub1.1", subHandler1));
            //
            TraceEventHolder subHandler_2 = (new TraceEventHolder.TraceEventHandler_sub2("sub2", 12));
            TraceEventHolder subNode_2 = c.addNode(new TraceEventHolderChild("sub2.1", subHandler_2));

            Extends_Handler_TraceEvent_InFilter_0 handler_1 = (new Extends_Handler_TraceEvent_InFilter_0("A0", 10));
            Node_TraceEvent_IntFilter_0 node_1 = c.addNode(new Node_TraceEvent_IntFilter_0("A1", handler_1));
            //
            Handler_TraceEvent_InFilter_0 handler_2 = (new Handler_TraceEvent_InFilter_0("B0", 20));
            Node_TraceEvent_IntFilter_0 node_2 = c.addNode(new Node_TraceEvent_IntFilter_0("B1", handler_2));
            //
            HandlerNoFilter_TraceEvent_InFilter_0 allEventHandler = c.addNode(new HandlerNoFilter_TraceEvent_InFilter_0("D0"));
            //
            c.addPublicNode(new Node_TraceEvent_Aggregator("aggregator", node_1, node_2), "aggregator");
        });

        //filter matches on 10, check trace
        TraceEvent_InFilter_0 event = new TraceEvent_InFilter_0(10);
        onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Extends_Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "A0", "A1", "aggregator", "D0");
        //filter matches on 10, check trace
        event = new TraceEvent_InFilter_0(20);
        onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                Handler_TraceEvent_InFilter_0.class,
                Node_TraceEvent_IntFilter_0.class,
                Node_TraceEvent_Aggregator.class,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "B0", "B1", "aggregator", "D0");
        //no filter match
        event = new TraceEvent_InFilter_0(11);
        onEvent(event);
        JavaTestGeneratorHelper.testClassOrder(event.traceList,
                HandlerNoFilter_TraceEvent_InFilter_0.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(event.traceIdList, "D0");
        //test subclasses
        Event te = new TraceEvent.TraceEvent_sub1(12);
        onEvent(te);
        JavaTestGeneratorHelper.testClassOrder(((TraceEvent.TraceEvent_sub1) te).getTraceList(),
                TraceEventHolder.TraceEventHandler_sub1.class,
                TraceEventHolderChild.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(((TraceEvent.TraceEvent_sub1) te).getTraceIdList(), "sub1", "sub1.1");
        //test subclasses
        te = new TraceEvent.TraceEvent_sub2(12);
        onEvent(te);
        JavaTestGeneratorHelper.testClassOrder(((TraceEvent.TraceEvent_sub2) te).getTraceList(),
                TraceEventHolder.TraceEventHandler_sub2.class,
                TraceEventHolderChild.class
        );
        JavaTestGeneratorHelper.testTraceIdOrder(((TraceEvent.TraceEvent_sub2) te).getTraceIdList(), "sub2", "sub2.1");
    }

    @Test
    public void diamondTest() {
        sep(c -> {
            TraceEventHolder handler_A = (new TraceEventHolder.TraceEventHandler_sub1("A0", 222));
            TraceEventHolder node_A1 = (new Node_TraceEventHolder_Aggregator("A1", handler_A));
            //
            TraceEventHolder handler_B = (new TraceEventHolder.TraceEventHandler_sub2("B0", 50));
            TraceEventHolder node_AB2 = (new Node_TraceEventHolder_Aggregator("AB1", node_A1, handler_B));
            TraceEventHolder node_A2 = (new Node_TraceEventHolder_Aggregator("A2", node_A1));
            TraceEventHolder node_AB3 = c.addNode(new Node_TraceEventHolder_Aggregator("AB3", node_A2, node_AB2));
        });
        Event te = new TraceEvent.TraceEvent_sub1(222);
        onEvent(te);
        JavaTestGeneratorHelper.testTraceIdContains(((TraceEvent.TraceEvent_sub1) te).getTraceIdList(), "A0", "A1", "AB1", "A2", "AB3");
        te = new TraceEvent.TraceEvent_sub2(50);
        onEvent(te);
        JavaTestGeneratorHelper.testTraceIdOrder(((TraceEvent.TraceEvent_sub2) te).getTraceIdList(), "B0", "AB1", "AB3");

    }

    @Test
    /**
     * Two call chains filtered by filter val 10 and 20. Chains join at
     * aggregator.
     */
    public void trace_dirty_test1() throws Exception {
        sep(c -> {
            TraceEventHolder handler_A = (new TraceEventHolder.TraceEventHandler_sub1("A0", 1));
            TraceEventHolder node_A1 = (new Node_DirtyFilter_TraceEvent("A1", handler_A));
            TraceEventHolder node_A2 = (new Node_TraceEventHolder_Aggregator("A2", node_A1));
            TraceEventHolder node_A3 = c.addNode(new Node_TraceEventHolder_Aggregator("A3", node_A2));
            //
            TraceEventHolder handler_B = (new TraceEventHolder.TraceEventHandler_sub2("B0", 2));
            TraceEventHolder node_B1 = (new Node_DirtyFilter_TraceEvent("B1", handler_B));
            //
            TraceEventHolder node_AB2 = (new Node_TraceEventHolder_Aggregator("AB1", node_A1, node_B1));
            TraceEventHolder node_AB3 = (new Node_TraceEventHolder_Aggregator("AB3", node_AB2));
            TraceEventHolder node_AB4 = (new Node_TraceEventHolder_Aggregator("AB4", node_AB3));
            TraceEventHolder node_AB5 = c.addNode(new Node_TraceEventHolder_Aggregator("AB5", node_AB4));
        });

        TraceEvent.TraceEvent_sub1 te = new TraceEvent.TraceEvent_sub1(1);
        te.strValue = "A1";
        onEvent(te);
        JavaTestGeneratorHelper.testTraceIdOrder(te.getTraceIdList(), "A0", "A1");
        //
        te.reset();
        te.strValue = "no match";
        onEvent(te);
        JavaTestGeneratorHelper.testTraceIdContains(te.getTraceIdList(),
                "A0", "A1", "A2", "A3", "AB1", "AB3", "AB4", "AB5");
        //
        TraceEvent.TraceEvent_sub2 te_2 = new TraceEvent.TraceEvent_sub2(2);
        te_2.strValue = "B1";
        onEvent(te_2);
        JavaTestGeneratorHelper.testTraceIdOrder(te_2.getTraceIdList(), "B0", "B1");
        //
        te_2.reset();
        te_2.strValue = "no match";
        onEvent(te_2);
        JavaTestGeneratorHelper.testTraceIdContains(te_2.getTraceIdList(),
                "B0", "B1", "AB1", "AB3", "AB4", "AB5");
    }

    @Test
    public void eventLifeCycleTest() {
        sep(c -> {
            TraceEventHolder handler_A = (new TraceEventHolder.TraceEventHandler_sub1("A0", 1));
            TraceEventHolder node_A1 = (new Node_TraceEventHolder_Aggregator.EventCompleteTrace("A1", handler_A));
            TraceEventHolder node_A2 = (new Node_TraceEventHolder_Aggregator.AfterEventTrace("A2", node_A1));
            TraceEventHolder node_A3 = c.addNode(new Node_TraceEventHolder_Aggregator.EventCompleteTrace("A3", node_A2));
            //
            TraceEventHolder handler_B = (new TraceEventHolder.TraceEventHandler_sub1("B0", 2));
            TraceEventHolder node_B1 = (new Node_TraceEventHolder_Aggregator.AfterEventTrace("B1", handler_B));
            TraceEventHolder node_B2 = (new Node_TraceEventHolder_Aggregator.EventCompleteTrace("B2", node_B1));
            TraceEventHolder node_B3 = c.addNode(new Node_TraceEventHolder_Aggregator.AfterEventTrace("B3", node_B2));
        });
        TraceEvent.TraceEvent_sub1 te = new TraceEvent.TraceEvent_sub1(1);
        onEvent(te);
        JavaTestGeneratorHelper.testTraceIdOrder(te.getTraceIdList(), "A0", "A1", "A2", "A3");
        JavaTestGeneratorHelper.testTraceIdOrder(te.getTraceAfterEventIdList(), "A2");
        JavaTestGeneratorHelper.testTraceIdOrder(te.getTraceEventCompleteIdList(), "A3", "A1");
        //
        TraceEvent.TraceEvent_sub1 te_2 = new TraceEvent.TraceEvent_sub1(2);
        onEvent(te_2);
        JavaTestGeneratorHelper.testTraceIdOrder(te_2.getTraceIdList(), "B0", "B1", "B2", "B3");
        JavaTestGeneratorHelper.testTraceIdOrder(te_2.getTraceAfterEventIdList(), "B3", "B1");
        JavaTestGeneratorHelper.testTraceIdOrder(te_2.getTraceEventCompleteIdList(), "B2");
    }
}
