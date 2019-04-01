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
package com.fluxtion.ext.futext.builder.math;

import com.fluxtion.ext.declarative.api.group.GroupBy;
import com.fluxtion.ext.futext.api.ascii.ByteBufferDelimiter;
import com.fluxtion.ext.declarative.api.numeric.MutableNumber;
import com.fluxtion.ext.declarative.builder.group.Frequency;

/**
 * 
 * @author gregp
 */
public interface WordFrequency {

    public static <K, T> GroupBy<MutableNumber> wordFrequency(ByteBufferDelimiter buffer) {
        return Frequency.frequency(buffer, ByteBufferDelimiter::asString);
    }

}
