package com.fluxtion.compiler.generation.inject;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.node.InstanceSupplier;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class InjectFromContext extends MultipleSepTargetInProcessTest {
    public InjectFromContext(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void injectIntoContext() {
//        writeSourceFile = true;
        callInit(false);
        sep(c -> {
            c.addNode(new InjectDataFromContext("newKey"), "ctxtLookup");
        });
        InjectDataFromContext ctxtLookup = getField("ctxtLookup");
        Assert.assertNull(ctxtLookup.getContextValue());
        Map<Object, Object> ctxtMap = new HashMap<>();
        ctxtMap.put("newKey", "newValue");
        sep.setContextParameterMap(ctxtMap);
        //
        callInit(true);
        init();
        Assert.assertEquals("newValue", ctxtLookup.getContextValue());
    }

    @Test(expected = RuntimeException.class)
    public void injectIntoContextFailFast() {
        callInit(false);
        sep(c -> {
            c.addNode(new FailFastInjectDataFromContext("newKey"), "ctxtLookup");
        });
        //
        callInit(true);
        init();
        FailFastInjectDataFromContext ctxtLookup = getField("ctxtLookup");
        ctxtLookup.getContextValue();
    }

    @Test
    public void injectContextService() {
//        writeSourceFile = true;
        callInit(false);
        sep(c -> {
            c.addNode(new InjectContextByType(), "injectionHolder");
        });
        sep.injectInstance(new MyService("injectedService"));
        sep.injectInstance(new MyService("injectedInterface"), MyInterface.class);
        //
        callInit(true);
        init();
        InjectContextByType injectionHolder = getField("injectionHolder");
        Assert.assertEquals("injectedService", injectionHolder.myService.get().getName());
        Assert.assertEquals("injectedInterface", injectionHolder.myInterface.get().getName());
        onEvent("test");
    }

    @Test
    public void addLambdaAsInjectedService() {
        callInit(false);
        sep(c -> {
            c.addNode(new InjectContextByType(), "injectionHolder");
        });
        sep.injectInstance(new MyService("injectedService"));
        sep.injectInstance((MyInterface) () -> "myLambdaInterface", MyInterface.class);
        //
        callInit(true);
        init();
        InjectContextByType injectionHolder = getField("injectionHolder");
        Assert.assertEquals("injectedService", injectionHolder.myService.get().getName());
        Assert.assertEquals("myLambdaInterface", injectionHolder.myInterface.get().getName());
        onEvent("test");
    }

    @Test
    public void addNamedLambda() {
        callInit(false);
        sep(c -> {
            c.addNode(new InjectNamedInterfaceType(), "injectionHolder");
        });
        sep.injectNamedInstance((MyInterface) () -> "myLambdaInterface", MyInterface.class, "svc_A");
        //
        callInit(true);
        init();
        InjectNamedInterfaceType injectionHolder = getField("injectionHolder");
        Assert.assertEquals("myLambdaInterface", injectionHolder.myInterface.get().getName());
        onEvent("test");
    }

    @Test
    public void injectContextServiceByName() {
        writeSourceFile = true;
        callInit(false);
        sep(c -> {
            c.addNode(new InjectContextByNameAndType(), "injectionHolder");
        });
        sep.injectNamedInstance(new MyService("injectedService_1"), "svc_1");
        sep.injectNamedInstance(new MyService("injectedService_2"), "svc_2");
        sep.injectInstance(new MyService("injectedInterface"), MyInterface.class);
        //
        callInit(true);
        init();
        InjectContextByNameAndType injectionHolder = getField("injectionHolder");
        Assert.assertEquals("injectedService_1", injectionHolder.svc_1.get().getName());
        Assert.assertEquals("injectedService_2", injectionHolder.svc_2.get().getName());
        Assert.assertEquals("injectedInterface", injectionHolder.myInterface.get().getName());
        onEvent("test");
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
