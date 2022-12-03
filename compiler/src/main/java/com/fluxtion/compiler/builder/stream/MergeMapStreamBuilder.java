package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.stream.MergeMapEventStream;
import com.fluxtion.runtime.stream.MergeProperty;

import java.util.ArrayList;
import java.util.List;

public class MergeMapStreamBuilder<T> {

    private final LambdaReflection.SerializableSupplier<T> resultFactory;
    private final List<MergeProperty<T, ?>> required = new ArrayList<>();

    private MergeMapStreamBuilder(LambdaReflection.SerializableSupplier<T> resultFactory) {
        this.resultFactory = resultFactory;
    }

    public static <T> MergeMapStreamBuilder<T> of(LambdaReflection.SerializableSupplier<T> resultFactory) {
        return new MergeMapStreamBuilder<T>(resultFactory);
    }

    public <R> MergeMapStreamBuilder<T> required(EventStreamBuilder<R> trigger, LambdaReflection.SerializableBiConsumer<T, R> setValue) {
        required.add(new MergeProperty<T, R>(trigger.eventStream, setValue, true, true));
        return this;
    }

    public <R> MergeMapStreamBuilder<T> requiredNoTrigger(EventStreamBuilder<R> trigger, LambdaReflection.SerializableBiConsumer<T, R> setValue) {
        required.add(new MergeProperty<T, R>(trigger.eventStream, setValue, false, true));
        return this;
    }

    public MergeMapEventStream<T> build() {
        MergeMapEventStream<T> stream = new MergeMapEventStream<>(resultFactory);
        required.forEach(stream::registerTrigger);
        return stream;
    }

}
