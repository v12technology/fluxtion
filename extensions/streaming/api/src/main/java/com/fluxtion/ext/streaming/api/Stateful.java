/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.ext.streaming.api;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;

/**
 * Functions maybe stateless or stateful in Fluxtion. A stateful function can implement this interface to receive reset
 * notifications during graph processing. Optional combine and deduct operations are used to efficiently aggregate results
 * in windowed processing.
 *
 * @author greg higgins
 * @param <S>
 */
public interface Stateful<S> {

    @Initialise
    void reset();

    default void combine(S other) {
        throw new UnsupportedOperationException("combine not supported");
    }

    default void deduct(S other) {
        throw new UnsupportedOperationException("deduct not supported");
    }

    default <T> T currentValue() {
        return null;
    }

    public static interface StatefulNumber<S> extends Stateful<S> {

        default Number combine(S other, MutableNumber result) {
            throw new UnsupportedOperationException("combine not supported");
        }

        default Number deduct(S other, MutableNumber result) {
            throw new UnsupportedOperationException("deduct not supported");
        }

        default Number currentValue(MutableNumber result) {
            return null;
        }

    }

}
