package com.fluxtion.compiler.generation.exportservice;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.ExportFunction;
import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.callback.ExportFunctionNode;
import com.fluxtion.runtime.node.NamedNode;
import org.junit.Assert;
import org.junit.Test;

public class ExportedServiceTest extends MultipleSepTargetInProcessTest {

    public ExportedServiceTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void exportVoidReturn() {
//        writeSourceFile = true;
        sep(c -> {
            c.addNode(new MyExportingServiceNode());
            c.addInterfaceImplementation(MyService.class);
        });
        init();
        MyService mySvc = sep.asInterface();
        mySvc.testAdd(23, 50);
        MyExportingServiceNode myNode = getField("myService");
        Assert.assertEquals(73, myNode.result);
    }

    @Test
    public void exportVoidAndAlwaysTrigger() {
        sep(c -> {
            c.addNode(new MyResultHolder());
            c.addInterfaceImplementation(MyService.class);
        });
        init();
        MyService mySvc = sep.asInterface();
        MyResultHolder myResultHolder = getField("myResultHolder");
        mySvc.testAdd(23, 50);
        MyExportingServiceNode myNode = getField("myService");
        Assert.assertEquals(73, myNode.result);
        Assert.assertEquals(1, myResultHolder.triggerCount);
    }

    public interface MyService {

        void testAdd(int a, int b);
    }

    public static class MyExportingServiceNode extends ExportFunctionNode implements @ExportService MyService, NamedNode {
        int result;

        @ExportFunction
        @Override
        public void testAdd(int a, int b) {
            result = a + b;
            System.out.println("resilt:" + result);
        }

        @Override
        public String getName() {
            return "myService";
        }
    }

    public static class MyResultHolder implements NamedNode {
        private final MyExportingServiceNode myExportingServiceNode;
        private int triggerCount;

        public MyResultHolder() {
            this(new MyExportingServiceNode());
        }

        public MyResultHolder(MyExportingServiceNode myExportingServiceNode) {
            this.myExportingServiceNode = myExportingServiceNode;
        }

        @OnTrigger
        public boolean triggered() {
            triggerCount++;
            System.out.println("triggerCount:" + triggerCount);
            return true;
        }

        @Override
        public String getName() {
            return "myResultHolder";
        }
    }
}
