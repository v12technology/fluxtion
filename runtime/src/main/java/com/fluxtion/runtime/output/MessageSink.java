/*
 * SPDX-FileCopyrightText: © 2024 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package com.fluxtion.runtime.output;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A sink for an EventProcessor. Implement this interface and register with :
 * <p>
 * {@link com.fluxtion.runtime.EventProcessor#registerService(Object, Class, String)}
 *
 * @param <T> the type of message published to the Sink
 */
public interface MessageSink<T> extends Consumer<T> {

    default void setValueMapper(Function<? super T, ?> valueMapper) {
        throw new UnsupportedOperationException("setValueMapper not implemented");
    }
}