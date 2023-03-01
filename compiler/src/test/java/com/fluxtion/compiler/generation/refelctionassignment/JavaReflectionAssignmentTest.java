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

    public static class Parent {
        private final String private_str;
        private final int private_int;
        private final char private_char;

        public Parent(String private_str, int private_int, char private_char) {
            this.private_str = private_str;
            this.private_int = private_int;
            this.private_char = private_char;
        }

        public String getPrivate_str() {
            return private_str;
        }

        public int getPrivate_int() {
            return private_int;
        }

        public char getPrivate_char() {
            return private_char;
        }
    }

    @Test
    public void test_privateAssignment() {
        sep(cfg -> {
            int[] intArr = new int[]{1, 2, 3, 4};
            Parent parent = cfg.addPublicNode(new Parent("testParent", 10, 'o'), "testParent");
            Parent arrRef_1 = cfg.addPublicNode(new Parent("testParent", 10, 'o'), "arrRef_1");
            cfg.addPublicNode(
                    new Handler_TraceEvent_PrivateMembers("private",
                            "transient", 100, -200, 'k',
                            parent,
                            Handler_TraceEvent_PrivateMembers.Currency.EUR,
                            intArr,
                            new Parent[]{arrRef_1}
                    ), "testNode");
            cfg.setAssignPrivateMembers(true);
        });

        Parent testParent = getField("testParent");
        Handler_TraceEvent_PrivateMembers result = getField("testNode");
        Parent arrRef_1 = getField("arrRef_1");
        assertEquals("private", result.getPrivate_str());
        assertEquals(100, result.getPrivate_int());
        assertEquals('k', result.getPrivate_char());
        assertEquals(0, result.getTransient_int());
        assertEquals(Currency.EUR, result.getCurrencyEnum());
        assertEquals(testParent, result.getParent());
        assertNull(result.getTransient_str());
        //arrays
        assertArrayEquals(new int[]{1, 2, 3, 4}, result.getPrivate_int_array());
        assertArrayEquals(new Parent[]{arrRef_1}, result.getArrayRef());
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
