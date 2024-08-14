package com.fluxtion.compiler.generation.service;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.runtime.ServiceDeregistered;
import com.fluxtion.runtime.annotations.runtime.ServiceRegistered;
import com.fluxtion.runtime.service.Service;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

public class ServiceTest extends MultipleSepTargetInProcessTest {
    public ServiceTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void svcTest() {
        sep(c -> {
            c.addNode(new ServiceListenerNode(), "myListener");
        });

        Service<MyService> service = new Service<>(new MyServiceImpl("no_name"), MyService.class);
        Service<MyService> serviceA = new Service<>(new MyServiceImpl("svc_A"), MyService.class, "svc_A");

        sep.registerService(service);
        sep.registerService(serviceA);

        ServiceListenerNode node = getField("myListener");
        Assert.assertEquals("no_name", node.name);
        Assert.assertEquals("svc_A", node.svc_A_name);

        sep.deRegisterService(service);
        sep.deRegisterService(serviceA);

        Assert.assertEquals("", node.name);
        Assert.assertEquals("", node.svc_A_name);
    }

    @Test
    public void multipleSvcListenerTest() {
        sep(c -> {
            c.addNode(new ServiceListenerNode(), "myListener");
            c.addNode(new ServiceListenerNode(), "myListener2");
        });

        Service<MyService> service = new Service<>(new MyServiceImpl("no_name"), MyService.class);
        Service<MyService> serviceA = new Service<>(new MyServiceImpl("svc_A"), MyService.class, "svc_A");

        sep.registerService(service);
        sep.registerService(serviceA);

        ServiceListenerNode node = getField("myListener");
        Assert.assertEquals("no_name", node.name);
        Assert.assertEquals("svc_A", node.svc_A_name);

        ServiceListenerNode node2 = getField("myListener2");
        Assert.assertEquals("no_name", node2.name);
        Assert.assertEquals("svc_A", node2.svc_A_name);

        sep.deRegisterService(service);
        sep.deRegisterService(serviceA);

        Assert.assertEquals("", node.name);
        Assert.assertEquals("", node.svc_A_name);

        Assert.assertEquals("", node2.name);
        Assert.assertEquals("", node2.svc_A_name);
    }


    @Test
    public void svcRegisterShortcutTest() {
        sep(c -> {
            c.addNode(new ServiceListenerNode(), "myListener");
        });

        MyServiceImpl noName = new MyServiceImpl("no_name");
        sep.registerService(noName, MyService.class);

        MyServiceImpl svcA = new MyServiceImpl("svc_A");
        sep.registerService(svcA, MyService.class, "svc_A");

        ServiceListenerNode node = getField("myListener");
        Assert.assertEquals("no_name", node.name);
        Assert.assertEquals("svc_A", node.svc_A_name);

        sep.deRegisterService(noName, MyService.class);
        sep.deRegisterService(svcA, MyService.class, "svc_A");

        Assert.assertEquals("", node.name);
        Assert.assertEquals("", node.svc_A_name);

    }

    @Test
    public void svcNamedCallbackRegisterShortcutTest() {
        sep(c -> {
            c.addNode(new NameServiceListenerNode(), "myListener");
        });

        MyServiceImpl noName = new MyServiceImpl("no_name");
        sep.registerService(noName, MyService.class);

        MyServiceImpl svcA = new MyServiceImpl("svc_A");
        sep.registerService(svcA, MyService.class, "svc_A");

        NameServiceListenerNode node = getField("myListener");

        Assert.assertEquals("no_name", node.name);
        Assert.assertEquals(MyService.class.getCanonicalName(), node.serviceName);
        Assert.assertEquals("svc_A", node.svc_A_name);
        Assert.assertEquals("svc_A", node.svc_A_ServiceName);


        sep.deRegisterService(noName, MyService.class);
        sep.deRegisterService(svcA, MyService.class, "svc_A");

        Assert.assertEquals("", node.name);
        Assert.assertEquals("", node.serviceName);
        Assert.assertEquals("", node.svc_A_name);
        Assert.assertEquals("", node.svc_A_ServiceName);
    }

    public static class ServiceListenerNode {

        private String name;
        private String svc_A_name;

        @ServiceRegistered
        public void registerMyService(MyService service) {
            name = service.getName();
        }

        @ServiceRegistered("svc_A")
        public void registerMyService2(MyService service) {
            svc_A_name = service.getName();
        }

        @ServiceDeregistered
        public void deregisterMyService(MyService service) {
            name = "";
        }

        @ServiceDeregistered("svc_A")
        public void deregisterMyService2(MyService service) {
            svc_A_name = "";
        }

        @OnEventHandler
        public boolean onMyEvent(String event) {
            return false;
        }
    }

    public static class NameServiceListenerNode {

        private String name;
        private String serviceName;

        private String svc_A_name;
        private String svc_A_ServiceName;

        @ServiceRegistered
        public void registerMyService(MyService service, String serviceName) {
            name = service.getName();
            this.serviceName = serviceName;
        }

        @ServiceRegistered("svc_A")
        public void registerMyService2(MyService service) {
            svc_A_name = service.getName();
        }

        @ServiceRegistered("svc_A")
        public void registerMyService2_named(MyService service, String serviceName) {
            svc_A_name = service.getName();
            this.svc_A_ServiceName = serviceName;
        }

        @ServiceDeregistered
        public void deregisterMyService(MyService service, String serviceName) {
            name = "";
            if (serviceName.equals(this.serviceName)) {
                this.serviceName = "";
            }
        }

        @ServiceDeregistered("svc_A")
        public void deregisterMyService2(MyService service, String serviceName) {
            svc_A_name = "";
            if (serviceName.equals(this.svc_A_ServiceName)) {
                this.svc_A_ServiceName = "";
            }
        }

        @OnEventHandler
        public boolean onMyEvent(String event) {
            return false;
        }
    }


    public interface MyService {
        String getName();
    }

    @Data
    public static class MyServiceImpl implements MyService {
        private final String name;
    }
}
