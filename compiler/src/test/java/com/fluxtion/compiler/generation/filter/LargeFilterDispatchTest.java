/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler.generation.filter;

import com.fluxtion.compiler.generation.targets.JavaTestGeneratorHelper;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.test.tracking.Node_TraceEventHolder_Aggregator;
import com.fluxtion.test.tracking.TraceEvent;
import com.fluxtion.test.tracking.TraceEventHolder;
import org.junit.Test;

/**
 * @author Greg Higgins
 */
public class LargeFilterDispatchTest extends MultipleSepTargetInProcessTest {


    public LargeFilterDispatchTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void trace_mapdispatch_test1() throws Exception {
        sep(c -> {
            int filterSize = 40;
            for (int i = 0; i < filterSize; i++) {
                TraceEventHolder handler_A = (new TraceEventHolder.TraceEventHandler_sub1("A" + i, i));
                TraceEventHolder node_A1 = (new Node_TraceEventHolder_Aggregator("B" + i, handler_A));
                TraceEventHolder node_A2 = (new Node_TraceEventHolder_Aggregator("C" + i, node_A1));
                c.addNode(new Node_TraceEventHolder_Aggregator("D" + i, node_A2));
            }
        });

        int id = 10;
        TraceEvent.TraceEvent_sub1 te = new TraceEvent.TraceEvent_sub1(id);
        onEvent(te);
        JavaTestGeneratorHelper.testTraceIdOrder(te.getTraceIdList(), "A" + id, "B" + id, "C" + id, "D" + id);
        //
        id = 34;
        te = new TraceEvent.TraceEvent_sub1(id);
        onEvent(te);
        JavaTestGeneratorHelper.testTraceIdOrder(te.getTraceIdList(), "A" + id, "B" + id, "C" + id, "D" + id);
    }

}
