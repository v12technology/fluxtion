package com.fluxtion.compiler.generation.inject;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.node.InstanceSupplier;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class InjectFromContext extends MultipleSepTargetInProcessTest {
    public InjectFromContext(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void injectIntoContext() {
//        writeSourceFile = true;
        enableInitCheck(false);
        sep(c -> {
            c.addNode(new InjectDataFromContext("newKey"), "ctxtLookup");
        });
        InjectDataFromContext ctxtLookup = getField("ctxtLookup");
        Assert.assertNull(ctxtLookup.getContextValue());
        Map<Object, Object> ctxtMap = new HashMap<>();
        ctxtMap.put("newKey", "newValue");
        sep.setContextParameterMap(ctxtMap);
        //
        enableInitCheck(true);
        init();
        Assert.assertEquals("newValue", ctxtLookup.getContextValue());
    }

    @Test
    public void injectIntoContextTwice() {
//        writeSourceFile = true;
        enableInitCheck(false);
        sep(c -> {
            c.addNode(new InjectDataFromContext("newKey"), "ctxtLookup");
            c.addNode(new InjectDataFromContext("newKey"), "ctxtLookup_2");
        });
        InjectDataFromContext ctxtLookup = getField("ctxtLookup");
        Assert.assertNull(ctxtLookup.getContextValue());
        Map<Object, Object> ctxtMap = new HashMap<>();
        ctxtMap.put("newKey", "newValue");
        sep.setContextParameterMap(ctxtMap);
        //
        enableInitCheck(true);
        init();
        Assert.assertEquals("newValue", ctxtLookup.getContextValue());
    }

    @Test(expected = RuntimeException.class)
    public void injectIntoContextFailFast() {
        enableInitCheck(false);
        sep(c -> {
            c.addNode(new FailFastInjectDataFromContext("newKey"), "ctxtLookup");
        });
        //
        enableInitCheck(true);
        init();
        FailFastInjectDataFromContext ctxtLookup = getField("ctxtLookup");
        ctxtLookup.getContextValue();
    }

    @Test
    public void injectContextService() {
//        writeSourceFile = true;
        enableInitCheck(false);
        sep(c -> {
            c.addNode(new InjectContextByType(), "injectionHolder");
        });
        sep.injectInstance(new MyService("injectedService"));
        sep.injectInstance(new MyService("injectedInterface"), MyInterface.class);
        //
        enableInitCheck(true);
        init();
        InjectContextByType injectionHolder = getField("injectionHolder");
        Assert.assertEquals("injectedService", injectionHolder.myService.get().getName());
        Assert.assertEquals("injectedInterface", injectionHolder.myInterface.get().getName());
        onEvent("test");
    }

    @Test
    public void addLambdaAsInjectedService() {
        enableInitCheck(false);
        sep(c -> {
            c.addNode(new InjectContextByType(), "injectionHolder");
        });
        sep.injectInstance(new MyService("injectedService"));
        sep.injectInstance((MyInterface) () -> "myLambdaInterface", MyInterface.class);
        //
        enableInitCheck(true);
        init();
        InjectContextByType injectionHolder = getField("injectionHolder");
        Assert.assertEquals("injectedService", injectionHolder.myService.get().getName());
        Assert.assertEquals("myLambdaInterface", injectionHolder.myInterface.get().getName());
        onEvent("test");
    }

    @Test
    public void addNamedLambda() {
//        writeSourceFile = true;
        enableInitCheck(false);
        sep(c -> {
            c.addNode(new InjectNamedInterfaceType(), "injectionHolder");
        });
        sep.injectNamedInstance(() -> "myLambdaInterface", MyInterface.class, "svc_A");
        //
        enableInitCheck(true);
        init();
        InjectNamedInterfaceType injectionHolder = getField("injectionHolder");
        Assert.assertEquals("myLambdaInterface", injectionHolder.myInterface.get().getName());
        onEvent("test");
    }

    @Test
    public void injectContextServiceByName() {
        enableInitCheck(false);
        sep(c -> {
            c.addNode(new InjectContextByNameAndType(), "injectionHolder");
        });
        sep.injectNamedInstance(new MyService("injectedService_1"), "svc_1");
        sep.injectNamedInstance(new MyService("injectedService_2"), "svc_2");
        sep.injectInstance(new MyService("injectedInterface"), MyInterface.class);
        //
        enableInitCheck(true);
        init();
        InjectContextByNameAndType injectionHolder = getField("injectionHolder");
        Assert.assertEquals("injectedService_1", injectionHolder.svc_1.get().getName());
        Assert.assertEquals("injectedService_2", injectionHolder.svc_2.get().getName());
        Assert.assertEquals("injectedInterface", injectionHolder.myInterface.get().getName());
        onEvent("test");
    }

    @Test
    public void lookupFromContextByNameAndType() {
        enableInitCheck(false);
        sep(c -> {
            c.addNode(new LookupInjectedServices(), "injectionHolder");
        });
        sep.injectNamedInstance(new MyService("injectedService_1"), "svc_1");
        sep.injectNamedInstance(new MyService("injectedService_2"), "svc_2");
        sep.injectInstance(new MyService("injectedInterface"), MyInterface.class);
        //
        enableInitCheck(true);
        init();
        LookupInjectedServices injectionHolder = getField("injectionHolder");
        Assert.assertEquals("injectedService_1", injectionHolder.svc_1.getName());
        Assert.assertEquals("injectedService_2", injectionHolder.svc_2.getName());
        Assert.assertEquals("injectedInterface", injectionHolder.myInterface.getName());
        onEvent("test");
    }

    @Test(expected = RuntimeException.class)
    public void failFastOnLookup() {
        sep(c -> {
            c.addNode(new LookupInjectedServices(), "injectionHolder");
        });
        enableInitCheck(true);
        init();
    }

    @Test
    public void allowNullOnLookup() {
        sep(c -> {
            c.addNode(new AllowNullLookup(), "injectionHolder");
        });
        enableInitCheck(true);
        init();

        AllowNullLookup injectionHolder = getField("injectionHolder");
        Assert.assertNull("should be null service", injectionHolder.svc_1);
    }


    public interface MyInterface {
        String getName();
    }

    public static class InjectDataFromContext {

        private final InstanceSupplier<String> dateSupplier;

        public InjectDataFromContext(String key) {
            this(InstanceSupplier.build(key));
        }

        public InjectDataFromContext(InstanceSupplier<String> dateSupplier) {
            this.dateSupplier = dateSupplier;
        }

        @OnEventHandler
        public boolean update(String in) {
            return true;
        }

        public String getContextValue() {
            return dateSupplier.get();
        }
    }

    public static class FailFastInjectDataFromContext {

        private final InstanceSupplier<String> dateSupplier;

        public FailFastInjectDataFromContext(String key) {
            this(InstanceSupplier.buildFailFast(key));
        }

        public FailFastInjectDataFromContext(InstanceSupplier<String> dateSupplier) {
            this.dateSupplier = dateSupplier;
        }

        @OnEventHandler
        public boolean update(String in) {
            return true;
        }

        public String getContextValue() {
            return dateSupplier.get();
        }
    }

    public static class LookupInjectedServices {

        public MyService svc_1;
        public MyService svc_2;
        public MyInterface myInterface;
        @Inject
        public EventProcessorContext context;

        @Initialise
        public void init() {
            svc_1 = context.getInjectedInstance(MyService.class, "svc_1");
            svc_2 = context.getInjectedInstance(MyService.class, "svc_2");
            myInterface = context.getInjectedInstance(MyInterface.class);
        }

        @OnEventHandler
        public boolean updated(String in) {
            return true;
        }
    }

    public static class AllowNullLookup {
        public MyService svc_1;
        @Inject
        public EventProcessorContext context;

        @Initialise
        public void init() {
            svc_1 = context.getInjectedInstanceAllowNull(MyService.class, "svc_1");
        }

        @OnEventHandler
        public boolean updated(String in) {
            return true;
        }
    }

    public static class InjectContextByType {
        @Inject
        public InstanceSupplier<MyService> myService;
        @Inject
        public InstanceSupplier<MyInterface> myInterface;

        @OnEventHandler
        public boolean updated(String in) {
            return true;
        }
    }

    public static class InjectNamedInterfaceType {
        @Inject(instanceName = "svc_A")
        public InstanceSupplier<MyInterface> myInterface;

        @OnEventHandler
        public boolean updated(String in) {
            return true;
        }
    }

    public static class InjectContextByNameAndType {
        @Inject(instanceName = "svc_1")
        public InstanceSupplier<MyService> svc_1;
        @Inject(instanceName = "svc_2")
        public InstanceSupplier<MyService> svc_2;
        @Inject
        public InstanceSupplier<MyInterface> myInterface;

        @OnEventHandler
        public boolean updated(String in) {
            return true;
        }
    }

    public static class MyService implements MyInterface {
        private final String name;

        public MyService(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
