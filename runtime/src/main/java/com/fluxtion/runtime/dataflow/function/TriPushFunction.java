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
import com.fluxtion.runtime.partition.LambdaReflection.SerializableQuadConsumer;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableTriConsumer;

public class TriPushFunction<T, A, B, C> extends MultiArgumentPushFunction<T> {

    @PushReference
    protected SerializableQuadConsumer<T, A, B, C> classPushMethod;
    @PushReference
    protected SerializableTriConsumer<A, B, C> instancePushMethod;
    protected FlowSupplier<A> source1;
    protected FlowSupplier<B> source2;
    protected FlowSupplier<C> source3;

    public TriPushFunction(@AssignToField("classPushMethod") SerializableQuadConsumer<T, A, B, C> classPushMethod,
                           @AssignToField("source1") FlowSupplier<A> source1,
                           @AssignToField("source2") FlowSupplier<B> source2,
                           @AssignToField("source3") FlowSupplier<C> source3) {
        super(classPushMethod, source1, source2, source3);
        this.classPushMethod = classPushMethod;
        this.source1 = source1;
        this.source2 = source2;
        this.source3 = source3;
    }

    public TriPushFunction(@AssignToField("instancePushMethod") SerializableTriConsumer<A, B, C> instancePushMethod,
                           @AssignToField("source1") FlowSupplier<A> source1,
                           @AssignToField("source2") FlowSupplier<B> source2,
                           @AssignToField("source3") FlowSupplier<C> source3) {
        super(instancePushMethod, source1, source2, source3);
        this.instancePushMethod = instancePushMethod;
        this.source1 = source1;
        this.source2 = source2;
        this.source3 = source3;
    }

    public void triggerOperation() {
        A a = source1.get();
        B b = source2.get();
        C c = source3.get();
        auditLog.info("push", auditInfo)
                .info("a", a)
                .info("b", b)
                .info("c", c);
        if (instancePushMethod != null) {
            instancePushMethod.accept(a, b, c);
        } else {
            classPushMethod.accept(pushTarget, a, b, c);
        }
    }
}
