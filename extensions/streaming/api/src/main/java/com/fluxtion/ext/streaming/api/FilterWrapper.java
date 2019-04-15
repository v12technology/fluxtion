package com.fluxtion.ext.streaming.api;

import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.api.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.stream.ElseWrapper;
import com.fluxtion.ext.streaming.api.stream.StreamOperator;

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
    default FilterWrapper<T> notifyOnChange(boolean notifyOnChange){
        return this;
    }

    @Override
    default <T, R, S extends R> FilterWrapper<T> push(SerializableFunction<T, S> supplier, SerializableConsumer< R> mapper) {
        StreamOperator.service().push(this, supplier.method(), mapper);
        return (FilterWrapper<T>) this;
    }

    @Override
    default FilterWrapper<T> forEach(SerializableConsumer<T> consumer) {
        return (FilterWrapper<T>) StreamOperator.service().forEach(consumer, this, null);
    }

    @Override
    default FilterWrapper<T> forEach(SerializableConsumer<T> consumer, String consumerId) {
        return (FilterWrapper<T>) StreamOperator.service().forEach(consumer, this, consumerId);
    }

//    @Override
//    <S> FilterWrapper<T> console(String prefix, SerializableFunction<T, S>... supplier);

    @Override
    default FilterWrapper<T> resetNotifier(Object resetNotifier) {
        return this;
    }

//    @Override
//    default FilterWrapper<T> notiferMerge(Object eventNotifier) {
//        return (FilterWrapper<T>)StreamOperator.service().notiferMerge(this, eventNotifier);
//    }
//
//    @Override
//    default FilterWrapper<T> notifierOverride(Object eventNotifier) {
//        return (FilterWrapper<T>)StreamOperator.service().notifierOverride(this, eventNotifier);
//    }

//    @Override
//    FilterWrapper<T> publishAndReset(Object notifier);

    @Override
    default FilterWrapper<T> immediateReset(boolean immediateReset) {
        return this;
    }

    @Override
    default FilterWrapper<T> alwaysReset(boolean alwaysReset) {
        return this;
    }
    
    @Override
    default FilterWrapper<T> id(String id) {
        return StreamOperator.service().nodeId(this, id);
    }
    
}
