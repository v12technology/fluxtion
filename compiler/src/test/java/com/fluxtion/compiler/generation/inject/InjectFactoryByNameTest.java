package com.fluxtion.compiler.generation.inject;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class InjectFactoryByNameTest extends MultipleSepTargetInProcessTest {


    public InjectFactoryByNameTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void multipleNamedFactoriesOfSameTypeTest() {
        sep(c -> {
            c.addNode(new ChildClass(), "child");
        });
        Assert.assertFalse(((ChildClass) getField("child")).matched);
        onEvent("test");
        Assert.assertTrue(((ChildClass) getField("child")).matched);
    }

    @Test
    public void accessClassOfInjectedFieldTest() {
        sep(c -> c.addNode(new GenericHolder(), "holder"));
        GenericHolder holder = getField("holder");
        Assert.assertEquals(holder.stringService.className, String.class.getCanonicalName());
        Assert.assertEquals(holder.dateService.className, Date.class.getCanonicalName());
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailOnMissingFactoryName() {
        sep(c -> c.addNode(new MissingFactory()));
    }

    public static class GenericHolder {
        @Inject
        public ServiceInjected<Date> dateService;

        @Inject
        public ServiceInjected<String> stringService;
    }

    public static class ChildClass {
        public boolean matched = false;

        @Inject(factoryName = "green")
        public MyUniqueData greenData;

        @Inject(factoryName = "blue")
        public MyUniqueData blueData;

        @OnEventHandler
        public boolean onEvent(String in) {
            matched = greenData.key.equals("green") && blueData.key.equals("blue");
            return true;
        }
    }

    public static class MissingFactory {
        @Inject(factoryName = "im_not_here_fatory")
        public MyUniqueData missingFactory;

        @OnEventHandler
        public boolean onEvent(String in) {
            return true;
        }
    }


    public static class MyUniqueData {
        public String key;

        public MyUniqueData(String key) {
            this.key = key;
        }

        public MyUniqueData() {
        }
    }

    public static class ServiceInjected<T> {
        private final String className;

        public ServiceInjected(String className) {
            this.className = className;
        }
    }

}
