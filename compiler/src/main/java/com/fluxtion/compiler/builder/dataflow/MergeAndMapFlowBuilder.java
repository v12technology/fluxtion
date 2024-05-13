package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.function.MergeMapFlowFunction;
import com.fluxtion.runtime.dataflow.function.MergeProperty;
import com.fluxtion.runtime.partition.LambdaReflection;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder that merges and maps several {@link FlowFunction}'s into a single event stream of type T
 *
 * @param <T> The output type of the merged stream
 */
public class MergeAndMapFlowBuilder<T> {

    private final LambdaReflection.SerializableSupplier<T> resultFactory;
    private final List<MergeProperty<T, ?>> required = new ArrayList<>();

    private MergeAndMapFlowBuilder(LambdaReflection.SerializableSupplier<T> resultFactory) {
        this.resultFactory = resultFactory;
    }

    public static <T> MergeAndMapFlowBuilder<T> of(LambdaReflection.SerializableSupplier<T> resultFactory) {
        return new MergeAndMapFlowBuilder<T>(resultFactory);
    }

    public <R> MergeAndMapFlowBuilder<T> required(FlowBuilder<R> trigger, LambdaReflection.SerializableBiConsumer<T, R> setValue) {
        required.add(new MergeProperty<T, R>(trigger.eventStream, setValue, true, true));
        return this;
    }

    public <R> MergeAndMapFlowBuilder<T> requiredNoTrigger(FlowBuilder<R> trigger, LambdaReflection.SerializableBiConsumer<T, R> setValue) {
        required.add(new MergeProperty<T, R>(trigger.eventStream, setValue, false, true));
        return this;
    }

    public MergeMapFlowFunction<T> build() {
        MergeMapFlowFunction<T> stream = new MergeMapFlowFunction<>(resultFactory);
        required.forEach(stream::registerTrigger);
        return stream;
    }

    /**
     * Merges and maps several {@link FlowFunction}'s into a single event stream of type T
     *
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    public FlowBuilder<T> dataFlow() {
        return new FlowBuilder<>(build());
    }
}
