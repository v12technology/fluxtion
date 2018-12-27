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
package com.fluxtion.extension.declarative.funclib.api.ascii;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.extension.declarative.funclib.api.event.CharEvent;

/**
 *
 * @author Greg Higgins
 */
public class Csv2Double extends Csv2Value {


    public Csv2Double(int fieldNumber, String terminatorChars, int headerLines) {
        super(fieldNumber, terminatorChars, headerLines);
    }

    public Csv2Double(int fieldNumber) {
        super(fieldNumber);
    }

    public Csv2Double() {
    }

    @EventHandler(filterId = '.')
    public boolean onDecimalPoint(CharEvent event) {
        pointIncrement = 1;
        return false;
    }

    @Override
    protected void calculateValues() {
        doubleValue = sign * intermediateVal / Math.pow(10, pointPos);
        intValue = (int) doubleValue;
        longValue = (long) doubleValue;
    }

}
