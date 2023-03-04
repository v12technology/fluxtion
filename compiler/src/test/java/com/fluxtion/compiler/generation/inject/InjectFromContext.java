package com.fluxtion.compiler.generation.inject;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.node.ContextValueSupplier;
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
        sep.registerContextInstance(new MyService("injectedService"));
        sep.registerContextInstance(new MyService("injectedInterface"), MyInterface.class);
        //
        callInit(true);
        init();
        InjectContextByType injectionHolder = getField("injectionHolder");
        Assert.assertEquals("injectedService", injectionHolder.myService.get().getName());
        Assert.assertEquals("injectedInterface", injectionHolder.myInterface.get().getName());
        onEvent("test");
    }


    public static class InjectDataFromContext {

        private final ContextValueSupplier<String> dateSupplier;

        public InjectDataFromContext(String key) {
            this(ContextValueSupplier.build(key));
        }

        public InjectDataFromContext(ContextValueSupplier<String> dateSupplier) {
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

        private final ContextValueSupplier<String> dateSupplier;

        public FailFastInjectDataFromContext(String key) {
            this(ContextValueSupplier.buildFailFast(key));
        }

        public FailFastInjectDataFromContext(ContextValueSupplier<String> dateSupplier) {
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
        public ContextValueSupplier<MyService> myService;
        @Inject
        public ContextValueSupplier<MyInterface> myInterface;

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

    public interface MyInterface {
        String getName();
    }
}
