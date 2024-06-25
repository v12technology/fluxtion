package com.fluxtion.runtime.service;

import com.fluxtion.runtime.annotations.feature.Preview;

/**
 * Export this service to receive notification callbaxcks when any services are registered or de-registered in the
 * container
 */

@Preview
public interface ServiceListener {

    void registerService(Service<?> service);

    void deRegisterService(Service<?> service);
}
