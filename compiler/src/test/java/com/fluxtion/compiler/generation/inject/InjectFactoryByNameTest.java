package com.fluxtion.compiler.generation.inject;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.google.auto.service.AutoService;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

public class InjectFactoryByNameTest extends MultipleSepTargetInProcessTest {


    public InjectFactoryByNameTest(boolean compiledSep) {
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
        public void onEvent(String in) {
            matched = greenData.key.equals("green") && blueData.key.equals("blue");
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

    @AutoService(NodeFactory.class)
    public static class MyGenericServiceFactory implements NodeFactory<ServiceInjected> {
        @Override
        public ServiceInjected<?> createNode(Map<String, Object> config, NodeRegistry registry) {
            Field field = (Field) config.get(NodeFactory.FIELD_KEY);
            Type genericFieldType = field.getGenericType();
            final String typeName;
            if (genericFieldType instanceof ParameterizedType) {
                ParameterizedType aType = (ParameterizedType) genericFieldType;
                Type[] fieldArgTypes = aType.getActualTypeArguments();
                typeName = ((Class) fieldArgTypes[0]).getCanonicalName();
            } else {
                typeName = "";
            }
            return new ServiceInjected<>(typeName);
        }

    }

    @AutoService(NodeFactory.class)
    public static class MyUniqueDataGreenFactory implements NodeFactory<MyUniqueData> {
        @Override
        public MyUniqueData createNode(Map<String, Object> config, NodeRegistry registry) {
            return new MyUniqueData("green");
        }

        @Override
        public String factoryName() {
            return "green";
        }
    }

    @AutoService(NodeFactory.class)
    public static class MyUniqueDataBlueFactory implements NodeFactory<MyUniqueData> {
        @Override
        public MyUniqueData createNode(Map<String, Object> config, NodeRegistry registry) {
            return new MyUniqueData("blue");
        }

        @Override
        public String factoryName() {
            return "blue";
        }
    }
}
