/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.generator.targets;

import com.fluxtion.builder.node.SEPConfig;
import static com.fluxtion.generator.targets.JavaGeneratorNames.*;
import static com.fluxtion.generator.targets.JavaTestGeneratorHelper.generateClass;
import static com.fluxtion.generator.targets.JavaTestGeneratorHelper.generateClassInline;
import com.fluxtion.test.event.AnnotatedEventHandlerDirtyNotifier;
import com.fluxtion.test.event.DirtyNotifierNode;
import com.fluxtion.test.event.TestEventNoIdFilteredHandler;
import com.fluxtion.test.event.TestEventNoIdHandler;
import com.fluxtion.test.tracking.Extends_Handler_TraceEvent_InFilter_0;
import com.fluxtion.test.tracking.HandlerNoFilter_TraceEvent_InFilter_0;
import com.fluxtion.test.tracking.Handler_TraceEvent_0;
import com.fluxtion.test.tracking.Handler_TraceEvent_InFilter_0;
import com.fluxtion.test.tracking.Handler_UnMatchedFilter_TraceEvent_InFilter_0;
import com.fluxtion.test.tracking.Node_DirtyFilter_TraceEvent;
import com.fluxtion.test.tracking.Node_TraceEventHolder_Aggregator;
import com.fluxtion.test.tracking.Node_TraceEventHolder_Aggregator_NoFiltering;
import com.fluxtion.test.tracking.Node_TraceEvent_0;
import com.fluxtion.test.tracking.Node_TraceEvent_Aggregator;
import com.fluxtion.test.tracking.Node_TraceEvent_IntFilter_0;
import com.fluxtion.test.tracking.TraceEventHolder;
import com.fluxtion.test.tracking.TraceEventHolder.TraceEventHandler_sub1;
import com.fluxtion.test.tracking.TraceEventHolderChild;
import com.thoughtworks.qdox.model.JavaClass;
import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class JavaTargetTest {

    public JavaTargetTest() {
    }

    @Test
    public void test1NoFilter() throws Exception {
        //System.out.println("test_1NoFilter");
        SEPConfig cfg = new SEPConfig() {
            {
                addNode(new TestEventNoIdHandler());
            }
        };
                cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, Test1NoFilter);
        JavaClass genClassInline = generateClassInline(cfg, Test1NoFilter);
        assertEquals(1, genClass.getFields().length);
        assertEquals(1, genClassInline.getFields().length);
        assertEquals(TestEventNoIdHandler.class.getCanonicalName(), genClass.getFields()[0].getType().getFullyQualifiedName());
        assertEquals(TestEventNoIdHandler.class.getCanonicalName(), genClassInline.getFields()[0].getType().getFullyQualifiedName());
    }

    @Test
    public void test1NoIdEventFilter() throws Exception {
        //System.out.println("test1NoIdEventFilter");
        SEPConfig cfg = new SEPConfig() {
            {
                addNode(new TestEventNoIdFilteredHandler(25));
            }
        };
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, Test1Filtered);
        JavaClass genClassInline = generateClassInline(cfg, Test1Filtered);
        assertEquals(1, genClass.getFields().length);
        assertEquals(TestEventNoIdFilteredHandler.class.getCanonicalName(), genClass.getFields()[0].getType().getFullyQualifiedName());
        assertEquals(1, genClassInline.getFields().length);
        assertEquals(TestEventNoIdFilteredHandler.class.getCanonicalName(), genClassInline.getFields()[0].getType().getFullyQualifiedName());
    }

    @Test
    /**
     * call chains filtered by filter val 10
     */
    public void trace_int_0_test1() throws Exception {
        //System.out.println("trace_int_0_test1");
        SEPConfig cfg = new SEPConfig() {
            {
                Handler_TraceEvent_InFilter_0 handler = addNode(new Handler_TraceEvent_InFilter_0("A0", 10));
                Node_TraceEvent_IntFilter_0 node_1 = addNode(new Node_TraceEvent_IntFilter_0("A1", handler));
                addPublicNode(new Node_TraceEvent_Aggregator("aggregator", node_1), "aggregator");
            }
        };
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, trace_int_0_test1);
        JavaClass genClassInline = generateClassInline(cfg, trace_int_0_test1);
        assertEquals(3, genClass.getFields().length);
        assertEquals(Node_TraceEvent_Aggregator.class.getCanonicalName(), genClass.getFieldByName("aggregator").getType().getFullyQualifiedName());
        assertEquals(3, genClassInline.getFields().length);
        assertEquals(Node_TraceEvent_Aggregator.class.getCanonicalName(), genClassInline.getFieldByName("aggregator").getType().getFullyQualifiedName());
    }

    @Test
    /**
     * Two call chains filtered by filter val 10 and 20. Chains join at
     * aggregator
     */
    public void trace_int_0_test2() throws Exception {
        //System.out.println("trace_int_0_test2");
        SEPConfig cfg = new SEPConfig() {
            {
                Handler_TraceEvent_InFilter_0 handler_1 = addNode(new Handler_TraceEvent_InFilter_0("A0", 10));
                Node_TraceEvent_IntFilter_0 node_1 = addNode(new Node_TraceEvent_IntFilter_0("A1", handler_1));
                //
                Handler_TraceEvent_InFilter_0 handler_2 = addNode(new Handler_TraceEvent_InFilter_0("B0", 20));
                Node_TraceEvent_IntFilter_0 node_2 = addNode(new Node_TraceEvent_IntFilter_0("B1", handler_2));
                //
                addPublicNode(new Node_TraceEvent_Aggregator("aggregator", node_1, node_2), "aggregator");
            }
        };
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, trace_int_0_test2);
        JavaClass genClassInline = generateClassInline(cfg, trace_int_0_test2);
        assertEquals(5, genClass.getFields().length);
        assertEquals(Node_TraceEvent_Aggregator.class.getCanonicalName(), genClass.getFieldByName("aggregator").getType().getFullyQualifiedName());
        assertEquals(5, genClassInline.getFields().length);
        assertEquals(Node_TraceEvent_Aggregator.class.getCanonicalName(), genClassInline.getFieldByName("aggregator").getType().getFullyQualifiedName());
    }

    @Test
    /**
     * Two call chains filtered by filter val 10 and 20. Chains join at
     * aggregator
     */
    public void trace_int_0_test3() throws Exception {
        //System.out.println("trace_int_0_test3");
        SEPConfig cfg = new SEPConfig() {
            {
                Handler_TraceEvent_InFilter_0 handler_1 = addNode(new Handler_TraceEvent_InFilter_0("A0", 10));
                Node_TraceEvent_IntFilter_0 node_1 = addNode(new Node_TraceEvent_IntFilter_0("A1", handler_1));
                //
                Handler_TraceEvent_InFilter_0 handler_2 = addNode(new Handler_TraceEvent_InFilter_0("B0", 20));
                Node_TraceEvent_IntFilter_0 node_2 = addNode(new Node_TraceEvent_IntFilter_0("B1", handler_2));
                //
                HandlerNoFilter_TraceEvent_InFilter_0 allEventHandler = addNode(new HandlerNoFilter_TraceEvent_InFilter_0("D0"));
                //
                addPublicNode(new Node_TraceEvent_Aggregator("aggregator", node_1, node_2), "aggregator");
            }
        };
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, trace_int_0_test3);
        JavaClass genClassInline = generateClassInline(cfg, trace_int_0_test3);
        assertEquals(6, genClass.getFields().length);
        assertEquals(Node_TraceEvent_Aggregator.class.getCanonicalName(), genClass.getFieldByName("aggregator").getType().getFullyQualifiedName());
        assertEquals(6, genClassInline.getFields().length);
        assertEquals(Node_TraceEvent_Aggregator.class.getCanonicalName(), genClassInline.getFieldByName("aggregator").getType().getFullyQualifiedName());
    }

    @Test
    /**
     * Two call chains filtered by filter val 10 and 20. Chains join at
     * aggregator.
     */
    public void trace_int_0_test4() throws Exception {
        //System.out.println("trace_int_0_test4");
        SEPConfig cfg = new SEPConfig() {
            {
                Extends_Handler_TraceEvent_InFilter_0 handler_1 = addNode(new Extends_Handler_TraceEvent_InFilter_0("A0", 10));
                Node_TraceEvent_IntFilter_0 node_1 = addNode(new Node_TraceEvent_IntFilter_0("A1", handler_1));
                //
                Handler_TraceEvent_InFilter_0 handler_2 = addNode(new Handler_TraceEvent_InFilter_0("B0", 20));
                Node_TraceEvent_IntFilter_0 node_2 = addNode(new Node_TraceEvent_IntFilter_0("B1", handler_2));
                //
                HandlerNoFilter_TraceEvent_InFilter_0 allEventHandler = addNode(new HandlerNoFilter_TraceEvent_InFilter_0("D0"));
                //
                addPublicNode(new Node_TraceEvent_Aggregator("aggregator", node_1, node_2), "aggregator");
            }
        };
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, trace_int_0_test4);
        JavaClass genClassInline = generateClassInline(cfg, trace_int_0_test4);
        assertEquals(6, genClass.getFields().length);
        assertEquals(Node_TraceEvent_Aggregator.class.getCanonicalName(), genClass.getFieldByName("aggregator").getType().getFullyQualifiedName());
        assertEquals(6, genClassInline.getFields().length);
        assertEquals(Node_TraceEvent_Aggregator.class.getCanonicalName(), genClassInline.getFieldByName("aggregator").getType().getFullyQualifiedName());
    }

    @Test
    /**
     * Testing all match and no match filters.
     */
    public void trace_int_0_test5() throws Exception {
        //System.out.println("trace_int_0_test5");
        SEPConfig cfg = new SEPConfig() {
            {
                Handler_TraceEvent_InFilter_0 handler_1 = addNode(new Handler_TraceEvent_InFilter_0("A0", 10));
                TraceEventHolderChild node_1 = addNode(new TraceEventHolderChild("A1", handler_1));
                //
                Handler_UnMatchedFilter_TraceEvent_InFilter_0 def_1 = addNode(new Handler_UnMatchedFilter_TraceEvent_InFilter_0("DEF-0"));
                TraceEventHolderChild def_2 = addNode(new TraceEventHolderChild("DEF-1", def_1));
                //
                HandlerNoFilter_TraceEvent_InFilter_0 allEventHandler = addNode(new HandlerNoFilter_TraceEvent_InFilter_0("ANY-1"));
                //
                addPublicNode(new Node_TraceEventHolder_Aggregator("aggregator", node_1, def_2, allEventHandler), "aggregator");
            }
        };
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, trace_int_0_test5);
        JavaClass genClassInline = generateClassInline(cfg, trace_int_0_test5);
        assertEquals(6, genClass.getFields().length);
        assertEquals(Node_TraceEventHolder_Aggregator.class.getCanonicalName(), genClass.getFieldByName("aggregator").getType().getFullyQualifiedName());
        assertEquals(6, genClassInline.getFields().length);
        assertEquals(Node_TraceEventHolder_Aggregator.class.getCanonicalName(), genClassInline.getFieldByName("aggregator").getType().getFullyQualifiedName());
    }

    @Test
    /**
     * Two call chains filtered by filter val 10 and 20. Chains join at
     * aggregator.
     */
    public void trace_subclass_test1() throws Exception {
        //System.out.println("trace_subclass_test1");
        SEPConfig cfg = new SEPConfig() {
            {
                //
                TraceEventHolder subHandler1 = addNode(new TraceEventHolder.TraceEventHandler_sub1("sub1", 12));
                TraceEventHolder subNode_1 = addNode(new TraceEventHolderChild("sub1.1", subHandler1));
                //
                TraceEventHolder subHandler_2 = addNode(new TraceEventHolder.TraceEventHandler_sub2("sub2", 12));
                TraceEventHolder subNode_2 = addNode(new TraceEventHolderChild("sub2.1", subHandler_2));

                Extends_Handler_TraceEvent_InFilter_0 handler_1 = addNode(new Extends_Handler_TraceEvent_InFilter_0("A0", 10));
                Node_TraceEvent_IntFilter_0 node_1 = addNode(new Node_TraceEvent_IntFilter_0("A1", handler_1));
                //
                Handler_TraceEvent_InFilter_0 handler_2 = addNode(new Handler_TraceEvent_InFilter_0("B0", 20));
                Node_TraceEvent_IntFilter_0 node_2 = addNode(new Node_TraceEvent_IntFilter_0("B1", handler_2));
                //
                HandlerNoFilter_TraceEvent_InFilter_0 allEventHandler = addNode(new HandlerNoFilter_TraceEvent_InFilter_0("D0"));
                //
                addPublicNode(new Node_TraceEvent_Aggregator("aggregator", node_1, node_2), "aggregator");
            }
        };
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, trace_subclass_test1);
        JavaClass genClassInline = generateClassInline(cfg, trace_subclass_test1);
        assertEquals(10, genClass.getFields().length);
        assertEquals(Node_TraceEvent_Aggregator.class.getCanonicalName(), genClass.getFieldByName("aggregator").getType().getFullyQualifiedName());
        assertEquals(10, genClassInline.getFields().length);
        assertEquals(Node_TraceEvent_Aggregator.class.getCanonicalName(), genClassInline.getFieldByName("aggregator").getType().getFullyQualifiedName());
    }

    @Test
    /**
     * Two call chains filtered by filter val 10 and 20. Chains join at
     * aggregator.
     */
    public void trace_diamond_test1() throws Exception {
        //System.out.println("trace_diamond_test1");
        SEPConfig cfg = new SEPConfig() {
            {
                //
                TraceEventHolder handler_A = addNode(new TraceEventHolder.TraceEventHandler_sub1("A0", 222));
                TraceEventHolder node_A1 = addNode(new Node_TraceEventHolder_Aggregator("A1", handler_A));
                //
                TraceEventHolder handler_B = addNode(new TraceEventHolder.TraceEventHandler_sub2("B0", 50));
                TraceEventHolder node_AB2 = addNode(new Node_TraceEventHolder_Aggregator("AB1", node_A1, handler_B));
                TraceEventHolder node_A2 = addNode(new Node_TraceEventHolder_Aggregator("A2", node_A1));
                TraceEventHolder node_AB3 = addNode(new Node_TraceEventHolder_Aggregator("AB3", node_A2, node_AB2));

            }
        };
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, trace_diamond_test1);
        JavaClass genClassInline = generateClassInline(cfg, trace_diamond_test1);
        assertEquals(6, genClass.getFields().length);
        assertEquals(6, genClassInline.getFields().length);
    }

    @Test

    /**
     * Two call chains filtered by filter val 10 and 20. Chains join at
     * aggregator.
     */
    public void trace_dirty_test1() throws Exception {
        //System.out.println("trace_dirty_test1");
        SEPConfig cfg = new SEPConfig() {
            {
                //
                TraceEventHolder handler_A = addNode(new TraceEventHolder.TraceEventHandler_sub1("A0", 1));
                TraceEventHolder node_A1 = addNode(new Node_DirtyFilter_TraceEvent("A1", handler_A));
                TraceEventHolder node_A2 = addNode(new Node_TraceEventHolder_Aggregator("A2", node_A1));
                TraceEventHolder node_A3 = addNode(new Node_TraceEventHolder_Aggregator("A3", node_A2));
                //
                TraceEventHolder handler_B = addNode(new TraceEventHolder.TraceEventHandler_sub2("B0", 2));
                TraceEventHolder node_B1 = addNode(new Node_DirtyFilter_TraceEvent("B1", handler_B));
                //
                TraceEventHolder node_AB2 = addNode(new Node_TraceEventHolder_Aggregator("AB1", node_A1, node_B1));
                TraceEventHolder node_AB3 = addNode(new Node_TraceEventHolder_Aggregator("AB3", node_AB2));
                TraceEventHolder node_AB4 = addNode(new Node_TraceEventHolder_Aggregator("AB4", node_AB3));
                TraceEventHolder node_AB5 = addNode(new Node_TraceEventHolder_Aggregator("AB5", node_AB4));

            }
        };
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, trace_dirty_test1, true);
        JavaClass genClassInline = generateClassInline(cfg, trace_dirty_test1, true);
//        assertEquals(7, genClass.getFields().length);
//        assertEquals(7, genClassInline.getFields().length);
    }

    @Test
    public void trace_dirty_test3() throws Exception {
        //System.out.println(trace_dirty_test2);
        SEPConfig cfg = new SEPConfig() {
            {
                //
                TraceEventHolder handler_A = addNode(new TraceEventHolder.TraceEventHandler_sub1("handler_A0", 1));
                TraceEventHolder handler_B = addNode(new TraceEventHolder.TraceEventHandler_sub2("handler_B0", 2));

                TraceEventHolder filter_A1 = addNode(new Node_DirtyFilter_TraceEvent("filter_A1", handler_A));
                TraceEventHolder filter_B1 = addNode(new Node_DirtyFilter_TraceEvent("filter_B1", handler_B));

                TraceEventHolder node_1 = addNode(new Node_TraceEventHolder_Aggregator_NoFiltering("node_1", handler_B));
                TraceEventHolder node_2 = addNode(new Node_TraceEventHolder_Aggregator_NoFiltering("node_2", filter_A1));
                TraceEventHolder node_3 = addNode(new Node_TraceEventHolder_Aggregator_NoFiltering("node_3", filter_A1, filter_B1));
                TraceEventHolder node_4 = addNode(new Node_TraceEventHolder_Aggregator_NoFiltering("node_4", node_1, node_3));
                TraceEventHolder node_5 = addNode(new Node_TraceEventHolder_Aggregator_NoFiltering("node_5", node_2));
                TraceEventHolder node_6 = addNode(new Node_TraceEventHolder_Aggregator_NoFiltering("node_6", node_3));

            }
        };
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, trace_dirty_test2, true);
        JavaClass genClassInline = generateClassInline(cfg, trace_dirty_test2, true);

        //a - filter match input string = "filter_A1"  "handler_A0", "filter_A1", "node_4"
        //a - no filter match input string = !"filter_A1"  "handler_A0", "filter_A1", "node_2", "node_3", "node_4", "node_5", "node_6"
        //
        //b - filter match input string = "filter_B1"  "handler_B0", "filter_B1", "node_1", "node_4"
        //b - no filter match input string = !"filter_B1"  "handler_B0", "filter_B1", "node_3", "node_1", "node_4", "node_6
    }

    @Test
    public void testDirtyFilterOnEventHandler() throws Exception {
        SEPConfig cfg = new SEPConfig() {
            AnnotatedEventHandlerDirtyNotifier eh = addNode(new AnnotatedEventHandlerDirtyNotifier());
            DirtyNotifierNode dirty_1 = addNode(new DirtyNotifierNode("dirty_1", eh));
        };
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, testDirtyFilterOnEventHandler, true);
    }

    @Test
    /**
     * Two call chains filtered by filter val 10 and 20. Chains join at
     * aggregator.
     */
    public void trace_eventlifecycle_test1() throws Exception {
        //System.out.println("trace_eventlifecycle_test1");
        SEPConfig cfg = new SEPConfig() {
            {
                //
                TraceEventHolder handler_A = addNode(new TraceEventHolder.TraceEventHandler_sub1("A0", 1));
                TraceEventHolder node_A1 = addNode(new Node_TraceEventHolder_Aggregator.EventCompleteTrace("A1", handler_A));
                TraceEventHolder node_A2 = addNode(new Node_TraceEventHolder_Aggregator.AfterEventTrace("A2", node_A1));
                TraceEventHolder node_A3 = addNode(new Node_TraceEventHolder_Aggregator.EventCompleteTrace("A3", node_A2));
                //
                TraceEventHolder handler_B = addNode(new TraceEventHolder.TraceEventHandler_sub1("B0", 2));
                TraceEventHolder node_B1 = addNode(new Node_TraceEventHolder_Aggregator.AfterEventTrace("B1", handler_B));
                TraceEventHolder node_B2 = addNode(new Node_TraceEventHolder_Aggregator.EventCompleteTrace("B2", node_B1));
                TraceEventHolder node_B3 = addNode(new Node_TraceEventHolder_Aggregator.AfterEventTrace("B3", node_B2));
                //
            }
        };
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, trace_eventlifecycle_test1);
        JavaClass genClassInline = generateClassInline(cfg, trace_eventlifecycle_test1);
        assertEquals(8, genClass.getFields().length);
        assertEquals(8, genClassInline.getFields().length);
    }

    @Test
    /**
     * Two call chains filtered by filter val 10 and 20. Chains join at
     * aggregator.
     */
    public void trace_mapdispatch_test1() throws Exception {
        //System.out.println("trace_mapdispatch_test1");
        int filterSize = 40;
        SEPConfig cfg = new SEPConfig() {
            {
                for (int i = 0; i < filterSize; i++) {
                    TraceEventHolder handler_A = addNode(new TraceEventHolder.TraceEventHandler_sub1("A" + i, i));
                    TraceEventHolder node_A1 = addNode(new Node_TraceEventHolder_Aggregator("B" + i, handler_A));
                    TraceEventHolder node_A2 = addNode(new Node_TraceEventHolder_Aggregator("C" + i, node_A1));
                    TraceEventHolder node_A3 = addNode(new Node_TraceEventHolder_Aggregator("D" + i, node_A2));
                }

            }
        };
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, trace_mapdispatch_test1);
        JavaClass genClassInline = generateClassInline(cfg, trace_mapdispatch_test1);

        long count = Arrays.stream(genClass.getFields())
                .filter((f) -> f.getType().getFullyQualifiedName().equalsIgnoreCase(TraceEventHandler_sub1.class.getName()))
                .count();
        assertEquals(filterSize, count);
        count = Arrays.stream(genClassInline.getFields())
                .filter((f) -> f.getType().getFullyQualifiedName().equalsIgnoreCase(TraceEventHandler_sub1.class.getName()))
                .count();
        assertEquals(filterSize, count);
    }

    @Test
    /**
     * Testing no filtering on either event handler and event
     */
    public void trace_0_test1() throws Exception {
        //System.out.println("trace_0_test1");
        SEPConfig cfg = new SEPConfig() {
            {
                Handler_TraceEvent_0 handler = addNode(new Handler_TraceEvent_0("A0"));
                Node_TraceEvent_0 node_1 = addNode(new Node_TraceEvent_0("A1", handler));
                addPublicNode(new Node_TraceEvent_Aggregator("aggregator", node_1), "aggregator");
            }
        };
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, trace_0_test1);
        JavaClass genClassInline = generateClassInline(cfg, trace_0_test1);
        assertEquals(3, genClass.getFields().length);
        assertEquals(Node_TraceEvent_Aggregator.class.getCanonicalName(), genClass.getFieldByName("aggregator").getType().getFullyQualifiedName());
        assertEquals(3, genClassInline.getFields().length);
        assertEquals(Node_TraceEvent_Aggregator.class.getCanonicalName(), genClassInline.getFieldByName("aggregator").getType().getFullyQualifiedName());
    }

}
