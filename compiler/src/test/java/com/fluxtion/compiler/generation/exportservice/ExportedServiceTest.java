package com.fluxtion.compiler.generation.exportservice;

import com.fluxtion.compiler.builder.dataflow.DataFlow;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.NoPropagateFunction;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.callback.Callback;
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import com.fluxtion.runtime.node.NamedNode;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.LongAdder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExportedServiceTest extends MultipleSepTargetInProcessTest {

    public ExportedServiceTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void exportVoidReturn() {
        sep(new MyExportingServiceNode());
        init();
        MyService mySvc = sep.getExportedService();
        mySvc.testAdd(23, 50);
        MyExportingServiceNode myNode = getField("myService");
        Assert.assertEquals(73, myNode.result);
    }

    @Test
    public void serviceIsExported() {
        sep(new MyExportingServiceNode());
        init();
        Assert.assertFalse(sep.exportsService(MyMissingService.class));
        Assert.assertTrue(sep.exportsService(MyService.class));
    }

    @Test
    public void serviceGetExportedByClass() {
        sep(new MyExportingServiceNode());
        init();
        Assert.assertNull(sep.getExportedService(MyMissingService.class));
        Assert.assertNotNull(sep.getExportedService(MyService.class));
    }

    @Test
    public void serviceDefaultGetExportedByClass() {
        sep(new MyExportingServiceNode());
        init();
        Assert.assertNotNull(sep.getExportedService(MyMissingService.class, new MyMissingService() {
        }));
        Assert.assertNotNull(sep.getExportedService(MyService.class));
    }

    @Test
    public void consumeExportedByClass() {
        LongAdder counter = new LongAdder();
        sep(new MyExportingServiceNode());
        init();
        sep.consumeServiceIfExported(MyMissingService.class, svc -> counter.increment());
        Assert.assertEquals(0, counter.intValue());
        sep.consumeServiceIfExported(MyService.class, svc -> counter.increment());
        Assert.assertEquals(1, counter.intValue());
    }

    @Test
    public void exportBooleanReturn() {
        sep(new MyExportingTriggerServiceNode());
        init();
        MyTriggeringService mySvc = sep.getExportedService();
        mySvc.testAdd(23, 50);
        MyExportingTriggerServiceNode myNode = getField("myService");
        Assert.assertEquals(73, myNode.result);
    }

    @Test
    public void exportVoidAndAlwaysTrigger() {
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

    @Test
    public void serviceWithCallBack() {
        sep(new ServiceWithCallback());
        MyTriggeringService mySvc = sep.getExportedService();
        ServiceWithCallback svcNode = getField("myService");
        Assert.assertEquals(0, svcNode.triggerCount);

        mySvc.triggerPositive(10);
        Assert.assertEquals(1, svcNode.triggerCount);

        mySvc.triggerPositive(-10);
        Assert.assertEquals(1, svcNode.triggerCount);
    }

    @Test
    public void noPropagateFunctionTest() {
        sep(c -> DataFlow.subscribeToNode(new NoPropagateSomeMethodsMySvc())
                .mapToInt(Mappers.count()).id("count"));

        MyTriggeringService triggeringService = sep.getExportedService();
        triggeringService.triggerPositive(10);
        assertThat(getStreamed("count"), is(1));

        triggeringService.testAdd(10, 10);
        assertThat(getStreamed("count"), is(1));

        triggeringService.testSubtract(10, 10);
        assertThat(getStreamed("count"), is(2));
    }

    @Test
    public void noPropagateAnyFunctionTest() {
        sep(c -> {
            NoPropagateAnyMethodsMySvc noPropagateAnyMethodsMySvc = new NoPropagateAnyMethodsMySvc();
            noPropagateAnyMethodsMySvc.triggerObject = new StringHandler();
            DataFlow.subscribeToNode(noPropagateAnyMethodsMySvc)
                    .mapToInt(Mappers.count()).id("count");
        });
        onEvent("test");
        assertThat(getStreamed("count"), is(0));

        MyService triggeringService = sep.getExportedService();
        triggeringService.testAdd(10, 10);
        assertThat(getStreamed("count"), is(0));
        onEvent("test");
        assertThat(getStreamed("count"), is(1));


        triggeringService.testSubtract(10, 10);
        assertThat(getStreamed("count"), is(1));
        onEvent("test");
        assertThat(getStreamed("count"), is(1));

        triggeringService.testAdd(10, 10);
        assertThat(getStreamed("count"), is(1));
        onEvent("test");
        assertThat(getStreamed("count"), is(2));
    }

    @Test
    public void noPropagateWholeServiceTest() {
        sep(c -> {
            NoPropagateMySvc noPropagateAnyMethodsMySvc = new NoPropagateMySvc();
            noPropagateAnyMethodsMySvc.triggerObject = new StringHandler();
            DataFlow.subscribeToNode(noPropagateAnyMethodsMySvc)
                    .mapToInt(Mappers.count()).id("count");
        });
        onEvent("test");
        assertThat(getStreamed("count"), is(0));

        MyService triggeringService = sep.getExportedService();
        triggeringService.testAdd(10, 10);
        assertThat(getStreamed("count"), is(0));
        onEvent("test");
        assertThat(getStreamed("count"), is(1));


        triggeringService.testSubtract(10, 10);
        assertThat(getStreamed("count"), is(1));
        onEvent("test");
        assertThat(getStreamed("count"), is(1));

        triggeringService.testAdd(10, 10);
        assertThat(getStreamed("count"), is(1));
        onEvent("test");
        assertThat(getStreamed("count"), is(2));
    }

    public interface MyTriggeringService extends MyService {
        boolean triggerPositive(int x);

    }

    public interface MyService {

        void testAdd(int a, int b);

        void testSubtract(int a, int b);
    }

    public interface MyMissingService {
    }

    public static class StringHandler {
        @OnEventHandler
        public boolean onString(String id) {
            return true;
        }
    }

    public static class MyExportingServiceNode implements @ExportService MyService, NamedNode {
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

    public static class MyExportingTriggerServiceNode implements @ExportService MyTriggeringService, NamedNode {
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

        @OnTrigger
        public boolean propagateParentNotification() {
            return true;
        }
    }

    public static class NoPropagateSomeMethodsMySvc implements @ExportService MyTriggeringService {

        @Override
        @NoPropagateFunction
        public void testAdd(int a, int b) {

        }

        @Override
        public void testSubtract(int a, int b) {

        }

        @Override
        public boolean triggerPositive(int x) {
            return x > 0;
        }
    }

    public static class NoPropagateAnyMethodsMySvc implements @ExportService MyService {
        public Object triggerObject;
        int sum = 0;

        @Override
        @NoPropagateFunction
        public void testAdd(int a, int b) {
            sum = a + b;
        }

        @Override
        @NoPropagateFunction
        public void testSubtract(int a, int b) {
            sum = a - b;
        }

        @OnTrigger
        public boolean trigger() {
            return sum > 0;
        }
    }

    public static class NoPropagateMySvc implements @ExportService(propagate = false) MyService {
        public Object triggerObject;
        int sum = 0;

        @Override
        public void testAdd(int a, int b) {
            sum = a + b;
        }

        @Override
        public void testSubtract(int a, int b) {
            sum = a - b;
        }

        @OnTrigger
        public boolean trigger() {
            return sum > 0;
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

    public static class ServiceWithCallback implements @ExportService MyTriggeringService, NamedNode {
        int result;
        int triggerCount;
        @Inject
        public Callback callback;

        @Override
        public void testAdd(int a, int b) {
            result = a + b;
        }

        @Override
        public void testSubtract(int a, int b) {
            result = a - b;
        }

        @Override
        public boolean triggerPositive(int x) {
            boolean b = x > 0;
            if (b) {
                callback.fireCallback();
            }
            return b;
        }

        @OnTrigger
        public boolean triggered() {
            triggerCount++;
            return true;
        }

        @Override
        public String getName() {
            return "myService";
        }
    }
}
