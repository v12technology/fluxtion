/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.generator.targets;

import com.fluxtion.generator.targets.JavaGenHelper;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.builder.generation.FilterDescription;
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.generator.Generator;
import com.fluxtion.generator.model.InvokerFilterTarget;
import com.fluxtion.test.event.AnnotatedTestEventHandler;
import com.fluxtion.test.event.AnnotatedTimeHandlerNoFilter;
import com.fluxtion.test.event.CharEvent;
import com.fluxtion.test.event.EventHandlerCb;
import com.fluxtion.test.event.RootCB;
import com.fluxtion.test.event.TimeEvent;
import com.fluxtion.test.nodes.Calculator;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author greg
 */
public class JavaGenHelperTest {

    public JavaGenHelperTest() {
    }

    /**
     * Test of generateMapDisaptch method, of class JavaGenHelper.
     */
    @Test
    @Ignore
    public void testNullInvokwerList() {
        //System.out.println("testNullInvokerList");
        ArrayList<InvokerFilterTarget> filteredInvokerList = null;
        String expResult = "";
        String result = JavaGenHelper.generateMapDisaptch(filteredInvokerList, new ArrayList());
        assertEquals(expResult, result);
    }

    @Test
    @Ignore
    public void testSingleMethod() throws NoSuchMethodException {
		//System.out.println("testSingleMethod");
        //use the Calculaor.calcComplete()
        Method calcMethod = Calculator.class.getMethod("calcComplete");
        assertNotNull(calcMethod);
        //filter
        FilterDescription filterDescription = new FilterDescription(CharEvent.class, 65);
        FilterDescription filterDescriptionStr = new FilterDescription(CharEvent.class, "fred");
        FilterDescription filterDescriptionTime = new FilterDescription(TimeEvent.class, "time");

        //System.out.println("generateMapDisaptch");
        ArrayList<InvokerFilterTarget> filteredInvokerList = new ArrayList<>();

        InvokerFilterTarget target = new InvokerFilterTarget();
        target.methodBody = "//int stuff";
        target.methodName = JavaGenHelper.generateFilteredDispatchMethodName(filterDescription);
        target.filterDescription = filterDescription;
        target.intMapName = JavaGenHelper.generateFilteredDispatchMap(filterDescription);

        InvokerFilterTarget target2 = new InvokerFilterTarget();
        target2.methodBody = "//string stuff";
        target2.methodName = JavaGenHelper.generateFilteredDispatchMethodName(filterDescriptionStr);
        target2.filterDescription = filterDescriptionStr;
        target2.stringMapName = JavaGenHelper.generateFilteredDispatchMap(filterDescriptionStr);

        InvokerFilterTarget targetTime = new InvokerFilterTarget();
        targetTime.methodBody = "//string stuff";
        targetTime.methodName = JavaGenHelper.generateFilteredDispatchMethodName(filterDescriptionTime);
        targetTime.filterDescription = filterDescriptionTime;
        targetTime.stringMapName = JavaGenHelper.generateFilteredDispatchMap(filterDescriptionTime);

        filteredInvokerList.add(target);
        filteredInvokerList.add(target2);
        filteredInvokerList.add(targetTime);

        String expResult = JavaGenHelper.generateMapDisaptch(filteredInvokerList, new ArrayList<>());

        //System.out.println("expResult:\n" + expResult);
//        //System.out.println("result:\n" + result);
        
//        assertEquals(expResult, result);

    }

    @Test
    @Ignore
    public void testHugeFilter() throws IOException, Exception {
		//System.out.println("testHugeFilter");
        SEPConfig cfg = new SEPConfig();
        cfg.generateDescription = false;
        EventHandlerCb e1 = cfg.addNode(new EventHandlerCb("e1", 1));
        AnnotatedTimeHandlerNoFilter noFilterEh = cfg.addNode(new AnnotatedTimeHandlerNoFilter());
        for (int i = 1; i < 100; i++) {
            AnnotatedTestEventHandler th = cfg.addNode(new AnnotatedTestEventHandler(i));
        }
//        InitCB i1 = cfg.addNode(new InitCB("i1", e1, th));
//        RootCB eRoot = cfg.addPublicNode( new RootCB("eRoot", i1, th, noFilterEh), "root");
        RootCB eRoot = cfg.addPublicNode( new RootCB("eRoot", noFilterEh), "root");

        cfg.templateFile = "javaTemplate.vsl";
        cfg.supportDirtyFiltering = true;
        //HUGE
        GenerationContext.setupStaticContext("com.fluxtion.test.template.java", "TestJava_Huge", new File("target/generated-test-sources/java/"), new File("target/generated-test-sources/resources/"));
        Generator generator = new Generator();
        generator.templateSep(cfg);

    }

}
