/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.generator.targets;

import com.fluxtion.test.tracking.TraceEvent;
import static com.fluxtion.generator.targets.JavaGeneratorNames.*;
import org.junit.Test;
import com.fluxtion.api.lifecycle.StaticEventProcessor;

/**
 *
 * @author Greg Higgins
 */
public class JavaTargetMapDispatchTestIT {

    public JavaTargetMapDispatchTestIT() {

        //trace_mapdispatch_test1
    }

    @Test
    public void trace_mapdispatch_test1() throws Exception {
        //System.out.println("trace_mapdispatch_test1");
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(trace_mapdispatch_test1);
        int id = 10;
        TraceEvent.TraceEvent_sub1 te = new TraceEvent.TraceEvent_sub1(id);
        handler.onEvent(te);
        JavaTestGeneratorHelper.testTraceIdOrder(te.getTraceIdList(), "A" + id, "B" + id, "C" + id, "D" + id);
        //
        id = 34;
        te = new TraceEvent.TraceEvent_sub1(id);
        handler.onEvent(te);
        JavaTestGeneratorHelper.testTraceIdOrder(te.getTraceIdList(), "A" + id, "B" + id, "C" + id, "D" + id);
    }

}
