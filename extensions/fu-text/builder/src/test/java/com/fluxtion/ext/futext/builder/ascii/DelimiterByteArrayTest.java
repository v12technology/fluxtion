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
package com.fluxtion.ext.futext.builder.ascii;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.api.group.GroupBy;
import com.fluxtion.ext.declarative.api.numeric.MutableInt;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.wordSplitter;
import com.fluxtion.ext.futext.builder.util.StringDriver;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.api.lifecycle.EventHandler;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static com.fluxtion.ext.futext.builder.math.WordFrequency.wordFrequency;

/**
 *
 * @author Greg Higgins
 */
public class DelimiterByteArrayTest extends BaseSepTest {

    public static final String VAR_BUFFER = "wordCount";

    @Test
    public void testByteBufferDelimiter() throws Exception {
        final EventHandler sep = buildAndInitSep(CsvBuilderTest.class);
        GroupBy<MutableInt> aggSales = getField(VAR_BUFFER);
        StringDriver.streamChars(".greg, fred... greg. greg?\nfred \"greg\" E\nbob ", sep, true);
        final Map<?, Wrapper<MutableInt>> wordMap = aggSales.getMap();
        assertEquals(4, wordMap.get("greg").event().getValue());
        assertEquals(2, wordMap.get("fred").event().getValue());
        assertEquals(1, wordMap.get("E").event().getValue());
        assertEquals(1, wordMap.get("bob").event().getValue());
        System.out.println(wordMap);
    }

    public static class CsvBuilderTest extends SEPConfig {{
            addPublicNode(wordFrequency(wordSplitter()), VAR_BUFFER);
    }}

}
