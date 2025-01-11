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
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.PushReference;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import com.fluxtion.runtime.dataflow.Stateful;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.node.BaseNode;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiConsumer;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableTriConsumer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.fluxtion.runtime.partition.LambdaReflection.MethodReferenceReflection;

public class BiPushFunctionOLD<T, A, B> extends BaseNode implements TriggeredFlowFunction<T> {

    @PushReference
    protected SerializableTriConsumer<T, A, B> classPushMethod;
    @PushReference
    protected SerializableBiConsumer<A, B> instancePushMethod;
    @PushReference
    protected T pushTarget;


    private transient final boolean statefulFunction;
    @NoTriggerReference
    protected transient Stateful resetFunction;
    @SuppressWarnings("rawtypes")
    private List<FlowFunction> triggerList = new ArrayList<>();
    protected transient final String auditInfo;
    protected FlowSupplier<A> source1;
    protected FlowSupplier<B> source2;


    @SuppressWarnings("all")
    private BiPushFunctionOLD(MethodReferenceReflection methodReference) {
        Objects.requireNonNull(methodReference, "push methodReference cannot be null");
        if (methodReference.isDefaultConstructor()) {
            throw new IllegalArgumentException("push methodReference must not be defaultConstructor");
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

        auditInfo = methodReference.method().getDeclaringClass().getSimpleName() + "->" + methodReference.method().getName();
    }

    public BiPushFunctionOLD(
            @AssignToField("classPushMethod") SerializableTriConsumer<T, A, B> classPushMethod,
            @AssignToField("source1") FlowSupplier<A> source1,
            @AssignToField("source2") FlowSupplier<B> source2
    ) {
        this(classPushMethod);
        this.classPushMethod = classPushMethod;
        this.source1 = source1;
        this.source2 = source2;
    }

    public BiPushFunctionOLD(
            @AssignToField("instancePushMethod") SerializableBiConsumer<A, B> instancePushMethod,
            @AssignToField("source1") FlowSupplier<A> source1,
            @AssignToField("source2") FlowSupplier<B> source2
    ) {
        this(instancePushMethod);
        this.instancePushMethod = instancePushMethod;
        this.source1 = source1;
        this.source2 = source2;
    }

    @OnTrigger
    public boolean push() {
        A a = source1.get();
        B b = source2.get();
        auditLog.info("push", auditInfo)
                .info("a", a)
                .info("b", b);
        if (instancePushMethod != null) {
            instancePushMethod.accept(a, b);
        } else {
            classPushMethod.accept(pushTarget, a, b);
        }
        return true;
    }

    @Override
    public void setUpdateTriggerNode(Object updateTriggerNode) {

    }

    @Override
    public void setPublishTriggerNode(Object publishTriggerNode) {

    }

    @Override
    public void setResetTriggerNode(Object resetTriggerNode) {

    }

    @Override
    public void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode) {

    }

    @Override
    public boolean hasChanged() {
        return getContext().getDirtyStateMonitor().isDirty(this);
    }

    @Override
    public void parallel() {
    }

    @Override
    public boolean parallelCandidate() {
        return false;
    }

    @Override
    public T get() {
        return pushTarget;
    }
}
