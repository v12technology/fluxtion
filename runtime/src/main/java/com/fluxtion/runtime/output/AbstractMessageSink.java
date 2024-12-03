/*
 * SPDX-FileCopyrightText: © 2024 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package com.fluxtion.runtime.output;

import java.util.function.Function;

public abstract class AbstractMessageSink<T> implements MessageSink<T> {

    private Function<? super T, ?> valueMapper = Function.identity();

    @Override
    public final void accept(T t) {
        sendToSink(valueMapper.apply(t));
    }

    @Override
    public void setValueMapper(Function<? super T, ?> valueMapper) {
        this.valueMapper = valueMapper;
    }

    abstract protected void sendToSink(Object value);
}
