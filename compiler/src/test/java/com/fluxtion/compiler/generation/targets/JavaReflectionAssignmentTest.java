/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler.generation.targets;

import com.fluxtion.compiler.builder.node.SEPConfig;
import com.fluxtion.test.nodes.privatemembers.TimeHandler;
import com.fluxtion.test.nodes.privatemembers.TimeRecorder;
import com.fluxtion.test.tracking.Handler_TraceEvent_PrivateMembers;
import com.thoughtworks.qdox.model.JavaClass;
import org.junit.Test;

import static com.fluxtion.compiler.generation.targets.JavaGeneratorNames.test_privateAssignment;
import static com.fluxtion.compiler.generation.targets.JavaGeneratorNames.test_privateDispatch;
import static com.fluxtion.compiler.generation.targets.JavaTestGeneratorHelper.generateClass;


/**
 * Test assignment of private variables by reflection
 *
 * @author Greg Higgins
 */
public class JavaReflectionAssignmentTest {

    @Test
    public void testPrivateMemberAssignment() throws Exception {
        //System.out.println(test_privateAssignment);
        int[] intArrEmpty = new int[0];
        int[] intArr = new int[]{1, 2, 3, 4};
        SEPConfig cfg = new SEPConfig() {
            {
                Handler_TraceEvent_PrivateMembers parent = addPublicNode(new Handler_TraceEvent_PrivateMembers(null, null, 89, -200, 'o', null, null, null, null), "testParent");
                Handler_TraceEvent_PrivateMembers arrRef_1 = addPublicNode(new Handler_TraceEvent_PrivateMembers(null, null, 1, 2, '5', null, null, null, null), "arrRef_1");
                addPublicNode(
                        new Handler_TraceEvent_PrivateMembers("private",
                                "transient", 100, -200, 'k',
                                parent,
                                Handler_TraceEvent_PrivateMembers.Currency.EUR,
                                intArr,
                                new Handler_TraceEvent_PrivateMembers[]{arrRef_1}
                        ), "testNode");
            }
        };
        cfg.assignPrivateMembers = true;
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, test_privateAssignment, true);
    }

    @Test
    public void dispatchWithPrivateMembers() throws Exception {
        SEPConfig cfg = new SEPConfig() {
            {
                TimeHandler timeHandler = addNode(new TimeHandler());
                addPublicNode(new TimeRecorder(timeHandler, 100), "timerecorder");
            }
        };
        cfg.assignPrivateMembers = true;
        cfg.generateDescription = false;
        JavaClass genClass = generateClass(cfg, test_privateDispatch, true);
    }
}
