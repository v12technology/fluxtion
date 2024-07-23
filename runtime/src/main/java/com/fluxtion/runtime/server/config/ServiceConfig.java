package com.fluxtion.runtime.server.config;

import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.service.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

@Experimental
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true, fluent = true)
public class ServiceConfig<T> {

    private T instance;
    private String serviceClass;
    private String name;

    public ServiceConfig(T instance, Class<T> serviceClass, String name) {
        this(instance, serviceClass.getCanonicalName(), name);
    }

    public void setInstance(T instance) {
        this.instance = instance;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getInstance() {
        return instance;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public String getName() {
        return name;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public Service<T> toService() {
        Class<T> serviceClazz = (Class<T>) (serviceClass == null ? instance.getClass() : Class.forName(serviceClass));
        serviceClass = serviceClazz.getCanonicalName();
        return new Service<>(instance, serviceClazz, name == null ? serviceClass : name);
    }
}
