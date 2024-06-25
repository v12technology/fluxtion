package com.fluxtion.runtime.service;

import com.fluxtion.runtime.annotations.feature.Preview;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@Preview
public class Service<T> {

    private final Class<T> serviceClass;
    private final String serviceName;
    private final T service;

    public <S extends T> Service(S service, Class<T> serviceClass, String serviceName) {
        this.serviceClass = serviceClass;
        this.serviceName = serviceName;
        this.service = service;
    }

    @SuppressWarnings("unchecked")
    public <S extends T> Service(S service, String serviceName) {
        this.serviceClass = (Class<T>) service.getClass();
        this.serviceName = serviceName;
        this.service = service;
    }

    public <S extends T> Service(S service, Class<T> serviceClass) {
        this(service, serviceClass, null);
    }

    @SuppressWarnings("unchecked")
    public <S extends T> Service(S service) {
        this(service, (Class<T>) service.getClass(), null);
    }
}
