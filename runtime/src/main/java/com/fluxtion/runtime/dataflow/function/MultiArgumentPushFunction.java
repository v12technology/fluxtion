/*
 * Copyright (c) 2025 gregory higgins.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */

package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.PushReference;
import com.fluxtion.runtime.annotations.builder.FluxtionIgnore;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import com.fluxtion.runtime.dataflow.Stateful;
import com.fluxtion.runtime.partition.LambdaReflection;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public abstract class MultiArgumentPushFunction<T> extends BaseFlowNode<T> {

    @PushReference
    protected T pushTarget;
    @Getter
    @FluxtionIgnore
    private final boolean statefulFunction;
    @NoTriggerReference
    protected transient Stateful<?> resetFunction;
    protected transient final String auditInfo;

    @SuppressWarnings("all")
    MultiArgumentPushFunction(LambdaReflection.MethodReferenceReflection methodReference, FlowSupplier<?>... flowSuppliers) {
        Objects.requireNonNull(methodReference, "push methodReference cannot be null");
        Objects.requireNonNull(flowSuppliers, "flowSuppliers cannot be null");
        if (methodReference.isDefaultConstructor()) {
            throw new IllegalArgumentException("push methodReference must not be defaultConstructor");
        }
        if (flowSuppliers.length == 0) {
            throw new IllegalArgumentException("flowSuppliers cannot be empty");
        }

        if (methodReference.captured().length == 0) {
            try {
                pushTarget = (T) methodReference.getContainingClass().getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException("no default constructor found for class:"
                        + methodReference.getContainingClass()
                        + " either add default constructor or pass in a node instance");
            }
        } else {
            pushTarget = (T) EventProcessorBuilderService.service().addOrReuse(methodReference.captured()[0]);
        }

        statefulFunction = Stateful.class.isAssignableFrom(pushTarget.getClass());
        if (statefulFunction) {
            resetFunction = (Stateful) methodReference.captured()[0];
        }

        for (FlowSupplier<?> flowSupplier : flowSuppliers) {
            getInputs().add(flowSupplier);
        }

        auditInfo = methodReference.method().getDeclaringClass().getSimpleName() + "->" + methodReference.method().getName();
    }


    @Override
    protected void resetOperation() {
        resetFunction.reset();
    }

    @Override
    public T get() {
        return pushTarget;
    }

}
