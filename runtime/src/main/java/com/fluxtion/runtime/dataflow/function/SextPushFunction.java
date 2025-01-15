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
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSeptConsumer;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSextConsumer;

public class SextPushFunction<T, A, B, C, D, E, F> extends MultiArgumentPushFunction<T> {

    @PushReference
    protected SerializableSeptConsumer<T, A, B, C, D, E, F> classPushMethod;
    @PushReference
    protected SerializableSextConsumer<A, B, C, D, E, F> instancePushMethod;
    protected FlowSupplier<A> source1;
    protected FlowSupplier<B> source2;
    protected FlowSupplier<C> source3;
    protected FlowSupplier<D> source4;
    protected FlowSupplier<E> source5;
    protected FlowSupplier<F> source6;

    public SextPushFunction(@AssignToField("classPushMethod") SerializableSeptConsumer<T, A, B, C, D, E, F> classPushMethod,
                            @AssignToField("source1") FlowSupplier<A> source1,
                            @AssignToField("source2") FlowSupplier<B> source2,
                            @AssignToField("source3") FlowSupplier<C> source3,
                            @AssignToField("source4") FlowSupplier<D> source4,
                            @AssignToField("source5") FlowSupplier<E> source5,
                            @AssignToField("source6") FlowSupplier<F> source6) {
        super(classPushMethod, source1, source2, source3, source4, source5, source6);
        this.classPushMethod = classPushMethod;
        this.source1 = source1;
        this.source2 = source2;
        this.source3 = source3;
        this.source4 = source4;
        this.source5 = source5;
        this.source6 = source6;
    }

    public SextPushFunction(@AssignToField("instancePushMethod") SerializableSextConsumer<A, B, C, D, E, F> instancePushMethod,
                            @AssignToField("source1") FlowSupplier<A> source1,
                            @AssignToField("source2") FlowSupplier<B> source2,
                            @AssignToField("source3") FlowSupplier<C> source3,
                            @AssignToField("source4") FlowSupplier<D> source4,
                            @AssignToField("source5") FlowSupplier<E> source5,
                            @AssignToField("source6") FlowSupplier<F> source6) {
        super(instancePushMethod, source1, source2, source3, source4, source5, source6);
        this.instancePushMethod = instancePushMethod;
        this.source1 = source1;
        this.source2 = source2;
        this.source3 = source3;
        this.source4 = source4;
        this.source5 = source5;
        this.source6 = source6;
    }

    public void triggerOperation() {
        A a = source1.get();
        B b = source2.get();
        C c = source3.get();
        D d = source4.get();
        E e = source5.get();
        F f = source6.get();
        auditLog.info("push", auditInfo)
                .info("a", a)
                .info("b", b)
                .info("c", c)
                .info("d", d)
                .info("e", e)
                .info("f", f);
        if (instancePushMethod != null) {
            instancePushMethod.accept(a, b, c, d, e, f);
        } else {
            classPushMethod.accept(pushTarget, a, b, c, d, e, f);
        }
    }
}
