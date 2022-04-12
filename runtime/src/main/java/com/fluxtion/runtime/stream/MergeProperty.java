package com.fluxtion.runtime.stream;

import com.fluxtion.runtime.partition.LambdaReflection;
import lombok.Value;

/**
 *
 * @param <T> The target type
 * @param <R> The consumer property type on the target and type of the incoming {@link EventStream}
 */
@Value
public class MergeProperty<T, R> {
    EventStream<R> trigger;
    LambdaReflection.SerializableBiConsumer<T, R> setValue;
    boolean triggering;
    boolean mandatory;

    public void push(T target){
        setValue.accept(target, trigger.get());
    }
}
