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
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.test.nodegen;

import com.fluxtion.api.annotations.OnEvent;

/**
 * Prototype for generated derived average calculations.
 *
 * @author Greg Higgins
 */
public class AveragingNode {

    /**
     * The instance holding the data to be averaged. Key - DATA_SOURCE The field
     * holding the numeric value is: Key - DATA_SOURCE_FIELD
     */
    public Object dataSource;

    /**
     * the output field, can be referenced by child instances. Key -
     * OUTPUT_AVERAGE
     */
    public double average;

    @OnEvent
    public void onUpdate() {
        //to be replaced by the generated class
    }

}

