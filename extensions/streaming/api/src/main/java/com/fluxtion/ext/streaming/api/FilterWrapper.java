package com.fluxtion.ext.streaming.api;

import com.fluxtion.ext.streaming.api.stream.ElseWrapper;

/**
 * A wrapper around a node that is created as the result of a filtering
 * operation.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public interface FilterWrapper<T> extends Wrapper<T> {

    /**
     * provides an else branch to a filter node in this stream.
     *
     * @return A wrapper on the else branch of a filtering operation
     */
    default Wrapper<T> elseStream() {
        return SepContext.service().add(new ElseWrapper(this));
    }

}
