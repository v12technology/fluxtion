package com.fluxtion.runtime.service;

import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.builder.FluxtionIgnore;
import com.fluxtion.runtime.annotations.feature.Preview;
import com.fluxtion.runtime.annotations.runtime.ServiceDeregistered;
import com.fluxtion.runtime.annotations.runtime.ServiceRegistered;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.node.SingleNamedNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages service registrations and de-registrations pushing services into nodes that have methods annotated with:
 * {@code @ServiceRegistryNode}
 * {@code @ServiceDeregisteredNode}
 */
@Preview
public class ServiceRegistryNode
        extends SingleNamedNode
        implements
        Auditor,
        @ExportService(propagate = false) ServiceListener {


    public static final String NODE_NAME = "serviceRegistry";
    @FluxtionIgnore
    private final Map<RegistrationKey, Callback> serviceCallbackMap = new HashMap<>();
    @FluxtionIgnore
    private final Map<RegistrationKey, Callback> serviceDeregisterCallbackMap = new HashMap<>();
    @FluxtionIgnore
    private final RegistrationKey tempKey = new RegistrationKey();

    public ServiceRegistryNode() {
        super(NODE_NAME);
    }

    @Override
    public void registerService(Service<?> service) {
        auditLog.info("registerService", service);
        tempKey.serviceClass(service.serviceClass())
                .serviceName(service.serviceName());
        Callback callBackMethod = serviceCallbackMap.get(tempKey);
        if (callBackMethod != null) {
            callBackMethod.invoke(service.service());
        }
    }

    @Override
    public void deRegisterService(Service<?> service) {
        auditLog.info("deRegisterService", service);
        tempKey.serviceClass(service.serviceClass())
                .serviceName(service.serviceName());
        Callback callBackMethod = serviceDeregisterCallbackMap.get(tempKey);
        if (callBackMethod != null) {
            callBackMethod.invoke(service.service());
        }
    }

    @Override
    public void init() {
        serviceCallbackMap.clear();
    }

    @Override
    public void nodeRegistered(Object node, String nodeName) {
        Class<?> clazz = node.getClass();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {

            ServiceRegistered registerAnnotation = method.getAnnotation(ServiceRegistered.class);
            if (registerAnnotation != null
                    && Modifier.isPublic(method.getModifiers())
                    && method.getParameterCount() == 1) {

                serviceCallbackMap.put(
                        new RegistrationKey(method.getParameterTypes()[0], registerAnnotation.value().isEmpty() ? null : registerAnnotation.value()),
                        new Callback(method, node));
            }

            ServiceDeregistered deregisterAnnotation = method.getAnnotation(ServiceDeregistered.class);
            if (deregisterAnnotation != null
                    && Modifier.isPublic(method.getModifiers())
                    && method.getParameterCount() == 1) {

                serviceDeregisterCallbackMap.put(
                        new RegistrationKey(method.getParameterTypes()[0], deregisterAnnotation.value().isEmpty() ? null : deregisterAnnotation.value()),
                        new Callback(method, node));
            }
        }
    }

    @Data
    @Accessors(chain = true, fluent = true)
    @AllArgsConstructor
    @NoArgsConstructor
    private static class RegistrationKey {
        Class<?> serviceClass;
        String serviceName;
    }

    @Data
    @Accessors(chain = true, fluent = true)
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Callback {
        Method method;
        Object node;

        @SneakyThrows
        void invoke(Object service) {
            method.invoke(node, service);
        }
    }
}
