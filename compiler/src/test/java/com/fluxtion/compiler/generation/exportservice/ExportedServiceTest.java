package com.fluxtion.compiler.generation.exportservice;

import com.fluxtion.compiler.builder.dataflow.DataFlow;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
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
        sep(new MyExportingServiceNode());
        init();
        MyService mySvc = sep.getExportedService();
        mySvc.testAdd(23, 50);
        MyExportingServiceNode myNode = getField("myService");
        Assert.assertEquals(73, myNode.result);
    }

    @Test
    public void exportBooleanReturn() {
//        writeSourceFile = true;
        sep(new MyExportingTriggerServiceNode());
        init();
        MyTriggeringService mySvc = sep.getExportedService();
        mySvc.testAdd(23, 50);
        MyExportingTriggerServiceNode myNode = getField("myService");
        Assert.assertEquals(73, myNode.result);
    }

    @Test
    public void exportVoidAndAlwaysTrigger() {
//        writeSourceFile = true;
        sep(new MyResultHolder());
        init();
        MyService mySvc = sep.getExportedService();
        MyResultHolder myResultHolder = getField("myResultHolder");
        mySvc.testAdd(23, 50);
        MyExportingServiceNode myNode = getField("myService");
        Assert.assertEquals(73, myNode.result);
        Assert.assertEquals(1, myResultHolder.triggerCount);

        mySvc.testSubtract(23, 8);
        Assert.assertEquals(15, myNode.result);
        Assert.assertEquals(2, myResultHolder.triggerCount);
    }

    @Test
    public void exportBooleanTriggerWhenPositive() {
//        writeSourceFile = true;
        sep(new MyResultHolderTrigger());
        init();
        MyTriggeringService mySvc = sep.getExportedService();
        MyResultHolderTrigger myResultHolder = getField("myResultHolder");
        mySvc.testAdd(23, 50);
        MyExportingTriggerServiceNode myNode = getField("myService");
        Assert.assertEquals(73, myNode.result);
        Assert.assertEquals(1, myResultHolder.triggerCount);

        mySvc.testSubtract(23, 8);
        Assert.assertEquals(15, myNode.result);
        Assert.assertEquals(2, myResultHolder.triggerCount);

        mySvc.triggerPositive(10);
        Assert.assertEquals(3, myResultHolder.triggerCount);

        mySvc.triggerPositive(-10);
        Assert.assertEquals(3, myResultHolder.triggerCount);
    }

    @Test
    public void exportServiceAndParentNotification() {
        writeSourceFile = true;
        sep(c -> {
            MyResultHolderTrigger resultHolderTrigger = c.addNode(new MyResultHolderTrigger());
            resultHolderTrigger.myExportingServiceNode.triggerObject = DataFlow.subscribe(String.class).flowSupplier();
        });
        init();
        MyTriggeringService mySvc = sep.getExportedService();
        MyResultHolderTrigger myResultHolder = getField("myResultHolder");
        mySvc.testAdd(23, 50);
        MyExportingTriggerServiceNode myNode = getField("myService");
        Assert.assertEquals(73, myNode.result);
        Assert.assertEquals(1, myResultHolder.triggerCount);

        mySvc.testSubtract(23, 8);
        Assert.assertEquals(15, myNode.result);
        Assert.assertEquals(2, myResultHolder.triggerCount);

        mySvc.triggerPositive(10);
        Assert.assertEquals(3, myResultHolder.triggerCount);

        mySvc.triggerPositive(-10);
        Assert.assertEquals(3, myResultHolder.triggerCount);

        onEvent("Hello");
        Assert.assertEquals(4, myResultHolder.triggerCount);
    }

    public interface MyTriggeringService extends MyService {
        boolean triggerPositive(int x);

    }

    public interface MyService {

        void testAdd(int a, int b);

        void testSubtract(int a, int b);
    }

    public static class MyExportingServiceNode extends ExportFunctionNode implements @ExportService MyService, NamedNode {
        int result;

        @Override
        public void testAdd(int a, int b) {
            result = a + b;
        }

        @Override
        public void testSubtract(int a, int b) {
            result = a - b;
        }

        @Override
        public String getName() {
            return "myService";
        }
    }

    public static class MyExportingTriggerServiceNode extends ExportFunctionNode implements @ExportService MyTriggeringService, NamedNode {
        int result;

        public Object triggerObject;

        @Override
        public void testAdd(int a, int b) {
            result = a + b;
        }

        @Override
        public void testSubtract(int a, int b) {
            result = a - b;
        }

        @Override
        public String getName() {
            return "myService";
        }

        @Override
        public boolean triggerPositive(int x) {
            return x > 0;
        }

        @Override
        protected boolean propagateParentNotification() {
            return true;
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
            return true;
        }

        @Override
        public String getName() {
            return "myResultHolder";
        }
    }

    public static class MyResultHolderTrigger implements NamedNode {
        private final MyExportingTriggerServiceNode myExportingServiceNode;
        private int triggerCount;

        public MyResultHolderTrigger() {
            this(new MyExportingTriggerServiceNode());
        }

        public MyResultHolderTrigger(MyExportingTriggerServiceNode myExportingServiceNode) {
            this.myExportingServiceNode = myExportingServiceNode;
        }

        @OnTrigger
        public boolean triggered() {
            triggerCount++;
            return true;
        }

        @Override
        public String getName() {
            return "myResultHolder";
        }
    }
}
