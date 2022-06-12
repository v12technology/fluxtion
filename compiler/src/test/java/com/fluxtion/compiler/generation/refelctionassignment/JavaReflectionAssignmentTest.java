/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler.generation.refelctionassignment;

import com.fluxtion.compiler.generation.util.CompiledOnlySepTest;
import com.fluxtion.test.event.TimeEvent;
import com.fluxtion.test.nodes.privatemembers.TimeHandler;
import com.fluxtion.test.nodes.privatemembers.TimeRecorder;
import com.fluxtion.test.tracking.Handler_TraceEvent_PrivateMembers;
import com.fluxtion.test.tracking.Handler_TraceEvent_PrivateMembers.Currency;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test assignment of private variables by reflection
 *
 * @author Greg Higgins
 */
public class JavaReflectionAssignmentTest extends CompiledOnlySepTest {

    public JavaReflectionAssignmentTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void test_privateAssignment() {
        sep(cfg -> {
            int[] intArr = new int[]{1, 2, 3, 4};
            Handler_TraceEvent_PrivateMembers parent = cfg.addPublicNode(new Handler_TraceEvent_PrivateMembers(null, null, 89, -200, 'o', null, null, null, null), "testParent");
            Handler_TraceEvent_PrivateMembers arrRef_1 = cfg.addPublicNode(new Handler_TraceEvent_PrivateMembers(null, null, 1, 2, '5', null, null, null, null), "arrRef_1");
            cfg.addPublicNode(
                    new Handler_TraceEvent_PrivateMembers("private",
                            "transient", 100, -200, 'k',
                            parent,
                            Handler_TraceEvent_PrivateMembers.Currency.EUR,
                            intArr,
                            new Handler_TraceEvent_PrivateMembers[]{arrRef_1}
                    ), "testNode");
            cfg.setAssignPrivateMembers(true);
        });

        Handler_TraceEvent_PrivateMembers testParent = getField("testParent");
        Handler_TraceEvent_PrivateMembers result = getField("testNode");
        Handler_TraceEvent_PrivateMembers arrRef_1 = getField("arrRef_1");
        assertEquals("private", result.getPrivate_str());
        assertEquals(100, result.getPrivate_int());
        assertEquals('k', result.getPrivate_char());
        assertEquals(0, result.getTransient_int());
        assertEquals(Currency.EUR, result.getCurrencyEnum());
        assertEquals(testParent, result.getParent());
        assertNull(testParent.getParent());
        assertNull(result.getTransient_str());
        //arrays
        assertArrayEquals(new int[]{1, 2, 3, 4}, result.getPrivate_int_array());
        assertArrayEquals(new Handler_TraceEvent_PrivateMembers[]{arrRef_1}, result.getArrayRef());
    }

    @Test
    public void test_dispatchWithReflectionAssignment() {
        sep(cfg -> {
            TimeHandler timeHandler = cfg.addNode(new TimeHandler());
            cfg.addPublicNode(new TimeRecorder(timeHandler, 100), "timerecorder");
            cfg.setAssignPrivateMembers(true);
        });
        TimeRecorder recorder = getField("timerecorder");
        assertEquals(100, recorder.getRecordedTime());
        TimeEvent timeEvent = new TimeEvent();
        timeEvent.time = 250;
        onEvent(timeEvent);
        assertEquals(350, recorder.getRecordedTime());
    }
}
