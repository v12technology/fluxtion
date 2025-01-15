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

package com.fluxtion.runtime.dataflow.aggregate.function;

import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.dataflow.*;
import com.fluxtion.runtime.dataflow.aggregate.AggregateDoubleFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateIntFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateLongFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.BucketedSlidingWindow.BucketedSlidingWindowedDoubleFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.BucketedSlidingWindow.BucketedSlidingWindowedIntFunction;
import com.fluxtion.runtime.dataflow.function.AbstractFlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

public class FixSizedSlidingWindow
        <T, R, S extends FlowFunction<T>, F extends AggregateFlowFunction<T, R, F>>
        extends AbstractFlowFunction<T, R, S>
        implements TriggeredFlowFunction<R> {

    private final LambdaReflection.SerializableSupplier<F> windowFunctionSupplier;
    private final int buckets;
    protected transient final BucketedSlidingWindow<T, R, F> windowFunction;
    private R value;


    public FixSizedSlidingWindow(
            S inputEventStream,
            SerializableSupplier<F> windowFunctionSupplier,
            int buckets) {
        super(inputEventStream, null);
        this.windowFunctionSupplier = windowFunctionSupplier;
        this.buckets = buckets;
        this.windowFunction = new BucketedSlidingWindow<>(windowFunctionSupplier, buckets);
    }

    @Override
    public R get() {
        return value;
    }

    protected void cacheWindowValue() {
        value = windowFunction.get();
    }

    protected void aggregateInputValue(S inputEventStream) {
        windowFunction.aggregate(inputEventStream.get());
        windowFunction.roll();
        if (windowFunction.isAllBucketsFilled()) {
            cacheWindowValue();
            publishOverrideTriggered = !overridePublishTrigger & !overrideUpdateTrigger;
            inputStreamTriggered_1 = true;
            inputStreamTriggered = true;
        }
    }

    @OnParentUpdate
    public void inputUpdated(S inputEventStream) {
        aggregateInputValue(inputEventStream);
        inputStreamTriggered_1 = false;
        inputStreamTriggered = false;
    }

    @OnTrigger
    public boolean triggered() {
        return fireEventUpdateNotification();
    }

    @Override
    protected void resetOperation() {
        windowFunction.init();
        value = null;
    }

    @Override
    public boolean isStatefulFunction() {
        return true;
    }

    public static class FixSizedSlidingIntWindow<F extends AggregateIntFlowFunction<F>>
            extends FixSizedSlidingWindow<Integer, Integer, IntFlowFunction, F>
            implements IntFlowFunction {

        private int value;
        private transient final BucketedSlidingWindowedIntFunction<F> primitiveSlidingFunction;

        public FixSizedSlidingIntWindow(
                IntFlowFunction inputEventStream,
                SerializableSupplier<F> windowFunctionSupplier,
                int buckets) {
            super(inputEventStream, windowFunctionSupplier, buckets);
            primitiveSlidingFunction = new BucketedSlidingWindowedIntFunction<>(windowFunctionSupplier, buckets);
        }

        @OnParentUpdate
        public void inputUpdated(IntFlowFunction inputEventStream) {
            aggregateInputValue(inputEventStream);
            inputStreamTriggered_1 = false;
            inputStreamTriggered = false;
        }

        @Override
        public Integer get() {
            return value;
        }

        @Override
        public int getAsInt() {
            return value;
        }

        protected void cacheWindowValue() {
            value = primitiveSlidingFunction.getAsInt();
        }

        protected void aggregateInputValue(IntFlowFunction inputEventStream) {
            primitiveSlidingFunction.aggregateInt(inputEventStream.getAsInt());
            primitiveSlidingFunction.roll();
            if (primitiveSlidingFunction.isAllBucketsFilled()) {
                cacheWindowValue();
                publishOverrideTriggered = !overridePublishTrigger & !overrideUpdateTrigger;
                inputStreamTriggered_1 = true;
                inputStreamTriggered = true;
            }
        }

        @Override
        protected void resetOperation() {
            windowFunction.init();
            value = 0;
        }
    }

    public static class FixSizedSlidingDoubleWindow<F extends AggregateDoubleFlowFunction<F>>
            extends FixSizedSlidingWindow<Double, Double, DoubleFlowFunction, F>
            implements DoubleFlowFunction {

        private double value;
        private transient final BucketedSlidingWindowedDoubleFunction<F> primitiveSlidingFunction;

        public FixSizedSlidingDoubleWindow(
                DoubleFlowFunction inputEventStream,
                SerializableSupplier<F> windowFunctionSupplier,
                int buckets) {
            super(inputEventStream, windowFunctionSupplier, buckets);
            primitiveSlidingFunction = new BucketedSlidingWindowedDoubleFunction<>(windowFunctionSupplier, buckets);
        }

        @OnParentUpdate
        public void inputUpdated(DoubleFlowFunction inputEventStream) {
            aggregateInputValue(inputEventStream);
            inputStreamTriggered_1 = false;
            inputStreamTriggered = false;
        }

        @Override
        public Double get() {
            return value;
        }

        @Override
        public double getAsDouble() {
            return value;
        }

        protected void cacheWindowValue() {
            value = primitiveSlidingFunction.getAsDouble();
        }

        protected void aggregateInputValue(DoubleFlowFunction inputEventStream) {
            primitiveSlidingFunction.aggregateDouble(inputEventStream.getAsDouble());
            primitiveSlidingFunction.roll();
            if (primitiveSlidingFunction.isAllBucketsFilled()) {
                cacheWindowValue();
                publishOverrideTriggered = !overridePublishTrigger & !overrideUpdateTrigger;
                inputStreamTriggered_1 = true;
                inputStreamTriggered = true;
            }
        }

        @Override
        protected void resetOperation() {
            windowFunction.init();
            value = 0;
        }
    }
    
    public static class FixSizedSlidingLongWindow<F extends AggregateLongFlowFunction<F>>
            extends FixSizedSlidingWindow<Long, Long, LongFlowFunction, F>
            implements LongFlowFunction {

        private long value;
        private transient final BucketedSlidingWindow.BucketedSlidingWindowedLongFunction<F> primitiveSlidingFunction;

        public FixSizedSlidingLongWindow(
                LongFlowFunction inputEventStream,
                SerializableSupplier<F> windowFunctionSupplier,
                int buckets) {
            super(inputEventStream, windowFunctionSupplier, buckets);
            primitiveSlidingFunction = new BucketedSlidingWindow.BucketedSlidingWindowedLongFunction<>(windowFunctionSupplier, buckets);
        }

        @OnParentUpdate
        public void inputUpdated(LongFlowFunction inputEventStream) {
            aggregateInputValue(inputEventStream);
            inputStreamTriggered_1 = false;
            inputStreamTriggered = false;
        }

        @Override
        public Long get() {
            return value;
        }

        @Override
        public long getAsLong() {
            return value;
        }

        protected void cacheWindowValue() {
            value = primitiveSlidingFunction.getAsLong();
        }

        protected void aggregateInputValue(LongFlowFunction inputEventStream) {
            primitiveSlidingFunction.aggregateLong(inputEventStream.getAsLong());
            primitiveSlidingFunction.roll();
            if (primitiveSlidingFunction.isAllBucketsFilled()) {
                cacheWindowValue();
                publishOverrideTriggered = !overridePublishTrigger & !overrideUpdateTrigger;
                inputStreamTriggered_1 = true;
                inputStreamTriggered = true;
            }
        }

        @Override
        protected void resetOperation() {
            windowFunction.init();
            value = 0;
        }
    }
}
