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

import com.fluxtion.runtime.annotations.PushReference;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import com.fluxtion.runtime.partition.LambdaReflection;

public class BiPushFunction<T, A, B> extends MultiArgumentPushFunction<T> {

    @PushReference
    protected LambdaReflection.SerializableTriConsumer<T, A, B> classPushMethod;
    @PushReference
    protected LambdaReflection.SerializableBiConsumer<A, B> instancePushMethod;
    protected FlowSupplier<A> source1;
    protected FlowSupplier<B> source2;

    public BiPushFunction(
            @AssignToField("classPushMethod") LambdaReflection.SerializableTriConsumer<T, A, B> classPushMethod,
            @AssignToField("source1") FlowSupplier<A> source1,
            @AssignToField("source2") FlowSupplier<B> source2
    ) {
        super(classPushMethod, source1, source2);
        this.classPushMethod = classPushMethod;
        this.source1 = source1;
        this.source2 = source2;
    }

    public BiPushFunction(
            @AssignToField("instancePushMethod") LambdaReflection.SerializableBiConsumer<A, B> instancePushMethod,
            @AssignToField("source1") FlowSupplier<A> source1,
            @AssignToField("source2") FlowSupplier<B> source2
    ) {
        super(instancePushMethod, source1, source2);
        this.instancePushMethod = instancePushMethod;
        this.source1 = source1;
        this.source2 = source2;
    }

    public void triggerOperation() {
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
    }
}
