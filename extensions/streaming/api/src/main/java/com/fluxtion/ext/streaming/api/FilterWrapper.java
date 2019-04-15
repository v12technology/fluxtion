package com.fluxtion.ext.streaming.api;

import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.api.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.stream.ElseWrapper;

/**
 * A wrapper around a node that is created as the result of a filtering
 * operation.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T>
 */
public interface FilterWrapper<T> extends Wrapper<T>, Test {

    /**
     * provides an else branch to a filter node in this stream.
     *
     * @return A wrapper on the else branch of a filtering operation
     */
    default Wrapper<T> elseStream() {
        return SepContext.service().add(new ElseWrapper(this));
    }

    @Override
    FilterWrapper<T> notifyOnChange(boolean notifyOnChange);

    @Override
    <T, R, S extends R> FilterWrapper<T> push(SerializableFunction<T, S> supplier, LambdaReflection.SerializableConsumer< R> mapper);

    @Override
    FilterWrapper<T> forEach(SerializableConsumer<T> consumer);

    @Override
    FilterWrapper<T> forEach(SerializableConsumer<T> consumer, String consumerId);

    @Override
    <S> FilterWrapper<T> console(String prefix, SerializableFunction<T, S>... supplier);

    @Override
    FilterWrapper<T> resetNotifier(Object resetNotifier);

    @Override
    FilterWrapper<T> notiferMerge(Object eventNotifier);

    @Override
    FilterWrapper<T> notifierOverride(Object eventNotifier);

    @Override
    FilterWrapper<T> publishAndReset(Object notifier);

    @Override
    FilterWrapper<T> immediateReset(boolean immediateReset);

    @Override
    FilterWrapper<T> alwaysReset(boolean alwaysReset);
    
    @Override
    FilterWrapper<T> id(String id);
    
}
