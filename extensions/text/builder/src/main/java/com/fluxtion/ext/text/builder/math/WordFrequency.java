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
package com.fluxtion.ext.text.builder.math;

import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import com.fluxtion.ext.streaming.builder.group.Frequency;
import com.fluxtion.ext.text.api.ascii.ByteBufferDelimiter;
import com.fluxtion.ext.text.api.event.CharEvent;
import static com.fluxtion.ext.text.builder.ascii.AsciiHelper.wordSplitter;

/**
 * Utility class to calculate word frequency in a stream of {@link CharEvent}
 * .
 * @author gregp
 */
public interface WordFrequency {

  static GroupBy<ByteBufferDelimiter, MutableNumber> wordFrequency(ByteBufferDelimiter buffer) {
        return Frequency.frequency(buffer, ByteBufferDelimiter::asString);
    }

  static GroupBy<ByteBufferDelimiter, MutableNumber> wordFrequency() {
        return Frequency.frequency(wordSplitter(), ByteBufferDelimiter::asString);
    }

}
