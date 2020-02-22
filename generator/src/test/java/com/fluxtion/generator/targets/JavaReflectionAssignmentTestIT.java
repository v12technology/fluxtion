/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.generator.targets;

import static com.fluxtion.generator.targets.JavaGeneratorNames.test_privateAssignment;
import static com.fluxtion.generator.targets.JavaGeneratorNames.test_privateDispatch;
import com.fluxtion.test.event.TimeEvent;
import com.fluxtion.test.nodes.privatemembers.TimeRecorder;
import com.fluxtion.test.tracking.Handler_TraceEvent_PrivateMembers;
import com.fluxtion.test.tracking.Handler_TraceEvent_PrivateMembers.Currency;
import static org.junit.Assert.*;
import org.junit.Test;
import com.fluxtion.api.lifecycle.StaticEventProcessor;

/**
 * Test assignment of private variables by reflection
 * 
 * @author Greg Higgins
 */
public class JavaReflectionAssignmentTestIT {
    

    
    @Test
    public void test_privateAssignment() throws Exception {
        //System.out.println(test_privateAssignment);
//                Handler_TraceEvent_PrivateMembers parent = addPublicNode(new Handler_TraceEvent_PrivateMembers("p", "t", 89, -200,'o', null), "testParent");
//                addPublicNode(new Handler_TraceEvent_PrivateMembers("private", "transient", 100, -200,'k', parent), "testNode");
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(test_privateAssignment);
		Handler_TraceEvent_PrivateMembers testParent = (Handler_TraceEvent_PrivateMembers) handler.getClass().getField("testParent").get(handler);
		Handler_TraceEvent_PrivateMembers result = (Handler_TraceEvent_PrivateMembers) handler.getClass().getField("testNode").get(handler);
		Handler_TraceEvent_PrivateMembers arrRef_1 = (Handler_TraceEvent_PrivateMembers) handler.getClass().getField("arrRef_1").get(handler);
        assertEquals("private", result.getPrivate_str());
        assertEquals(100, result.getPrivate_int());
        assertEquals('k', result.getPrivate_char());
        assertEquals(0, result.getTransient_int());
        assertEquals(Currency.EUR, result.getCurrencyEnum());
        assertEquals(testParent, result.getParent());
        assertNull(testParent.getParent());
        assertNull(result.getTransient_str());
        //arrays
        assertArrayEquals(new int[]{1,2,3,4}, result.getPrivate_int_array());
        assertArrayEquals(new Handler_TraceEvent_PrivateMembers[]{arrRef_1}, result.getArrayRef());
    }
    
    @Test
    public void test_dispatchWithReflectionAssignment() throws Exception{
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(test_privateDispatch);
		TimeRecorder recorder = (TimeRecorder) handler.getClass().getField("timerecorder").get(handler);
        
        assertEquals(100, recorder.getRecordedTime());
        
        TimeEvent timeEvent = new TimeEvent();
        timeEvent.time = 250;
        handler.onEvent(timeEvent);
        assertEquals(350, recorder.getRecordedTime());
    }
}
