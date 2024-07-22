package com.fluxtion.runtime.server.config;

import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.server.dutycycle.ServerAgent;
import com.fluxtion.runtime.service.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.agrona.concurrent.Agent;

@Experimental
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceWorkerConfig<T extends Agent> {

    private T instance;
    private String serviceClass;
    private String name;
    //do work thread management
    private String agentGroup;

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public ServerAgent<T> toServiceAgent() {
        Class<T> serviceClazz = (Class<T>) (serviceClass == null ? instance.getClass() : Class.forName(serviceClass));
        serviceClass = serviceClazz.getCanonicalName();
        Service<T> svc = new Service<>(instance, serviceClazz, name == null ? serviceClass : name);
        return new ServerAgent<>(agentGroup, svc, instance);
    }
}
