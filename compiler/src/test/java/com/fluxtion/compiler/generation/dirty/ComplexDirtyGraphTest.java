package com.fluxtion.compiler.generation.dirty;

import com.fluxtion.compiler.generation.targets.JavaTestGeneratorHelper;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.test.tracking.Node_DirtyFilter_TraceEvent;
import com.fluxtion.test.tracking.Node_TraceEventHolder_Aggregator_NoFiltering;
import com.fluxtion.test.tracking.TraceEvent;
import com.fluxtion.test.tracking.TraceEventHolder;
import org.junit.Test;

import java.util.ArrayList;

public class ComplexDirtyGraphTest extends CompiledAndInterpretedSepTest {

    public ComplexDirtyGraphTest(SepTestConfig sepTestConfig) {
        super(sepTestConfig);
    }

    @Test
    public void trace_dirty_test3() throws Exception {
//        addAuditor();
        sep(cfg ->{
            TraceEventHolder handler_A = cfg.addNode(new TraceEventHolder.TraceEventHandler_sub1("handler_A0", 1));
            TraceEventHolder handler_B = cfg.addNode(new TraceEventHolder.TraceEventHandler_sub2("handler_B0", 2));

            TraceEventHolder filter_A1 = cfg.addNode(new Node_DirtyFilter_TraceEvent("filter_A1", handler_A));
            TraceEventHolder filter_B1 = cfg.addNode(new Node_DirtyFilter_TraceEvent("filter_B1", handler_B));

            TraceEventHolder node_1 = cfg.addNode(new Node_TraceEventHolder_Aggregator_NoFiltering("node_1", handler_B));
            TraceEventHolder node_2 = cfg.addNode(new Node_TraceEventHolder_Aggregator_NoFiltering("node_2", filter_A1));
            TraceEventHolder node_3 = cfg.addNode(new Node_TraceEventHolder_Aggregator_NoFiltering("node_3", filter_A1, filter_B1));
            TraceEventHolder node_4 = cfg.addNode(new Node_TraceEventHolder_Aggregator_NoFiltering("node_4", node_1, node_3));
            TraceEventHolder node_5 = cfg.addNode(new Node_TraceEventHolder_Aggregator_NoFiltering("node_5", node_2));
            TraceEventHolder node_6 = cfg.addNode(new Node_TraceEventHolder_Aggregator_NoFiltering("node_6", node_3));
        });
        //a - filter match input string = "filter_A1"  "handler_A0", "filter_A1", "node_4"
        //a - no filter match input string = !"filter_A1"  "handler_A0", "filter_A1", "node_2", "node_3", "node_4", "node_5", "node_6"
        //
        //b - filter match input string = "filter_B1"  "handler_B0", "filter_B1", "node_1", "node_4"
        //b - no filter match input string = !"filter_B1"  "handler_B0", "filter_B1", "node_3", "node_1", "node_4", "node_6

        TraceEvent.TraceEvent_sub1 te = new TraceEvent.TraceEvent_sub1(1);
        te.strValue = "no match";
        onEvent(te);
        JavaTestGeneratorHelper.testTraceIdContains(te.getTraceIdList(),
                "handler_A0", "filter_A1", "node_2", "node_3", "node_4", "node_5", "node_6");
        //
        te.reset();
        te.strValue = "filter_A1";
        onEvent(te);
        ArrayList<String> traceIdList = te.getTraceIdList();
        System.out.println(traceIdList);
        JavaTestGeneratorHelper.testTraceIdOrder(te.getTraceIdList(), "handler_A0", "filter_A1");
        //
        TraceEvent.TraceEvent_sub2 te_2 = new TraceEvent.TraceEvent_sub2(2);
        te_2.strValue = "no mathc";
        onEvent(te_2);
        JavaTestGeneratorHelper.testTraceIdContains(te_2.getTraceIdList(),
                "handler_B0", "filter_B1", "node_3", "node_1", "node_4", "node_6");
        //
        te_2.reset();
        te_2.strValue = "filter_B1";
        onEvent(te_2);
        JavaTestGeneratorHelper.testTraceIdContains(te_2.getTraceIdList(),
                "handler_B0", "filter_B1", "node_1", "node_4");

    }
}
