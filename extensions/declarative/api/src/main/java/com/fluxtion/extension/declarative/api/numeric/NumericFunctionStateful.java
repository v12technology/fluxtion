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
package com.fluxtion.extension.declarative.api.numeric;

/**
 * A Marker interface for an aggregating numeric functions. If a class uses this
 * interface then it must implement only one public method.
 *
 * The first argument of the calculation method will be the previously returned
 * value. The types of the return and the first argument should match.
 *
 * @author greg higgins
 */
public interface NumericFunctionStateful extends NumericFunctionStateless {
    /**
     * A stateful function should be reset to initial state on receiving this 
     * call. For example an average calculation would zero all counts and sums.
     * 
     * @return the reset value for this calculation 
     */
    default double reset(){
        return 0;
    }
}
