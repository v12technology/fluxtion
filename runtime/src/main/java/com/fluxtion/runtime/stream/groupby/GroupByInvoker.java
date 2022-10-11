package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.EventProcessorConfigService;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.partition.LambdaReflection.MethodReferenceReflection;

public class GroupByInvoker {

    @NoTriggerReference
    private final transient Object streamFunctionInstance;

    public GroupByInvoker(MethodReferenceReflection methodReferenceReflection) {
        if (methodReferenceReflection != null && methodReferenceReflection.captured().length > 0 && !methodReferenceReflection.isDefaultConstructor()) {
            streamFunctionInstance = EventProcessorConfigService.service().addOrReuse(methodReferenceReflection.captured()[0]);
        } else {
            streamFunctionInstance = null;
        }
    }
}
