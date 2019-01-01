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

import com.fluxtion.ext.futext.builder.ascii.AsciiHelper;
import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.ext.declarative.api.numeric.BufferValue;
import com.fluxtion.ext.futext.builder.ascii.AnyCharMatchFilterFactory;
import com.fluxtion.ext.futext.builder.ascii.AsciiMatchFilterFactory;
import com.fluxtion.ext.futext.builder.util.StringDriver;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.runtime.lifecycle.EventHandler;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class Csv2ByteArrayTest extends BaseSepTest {

    public static final String VAR_BUFFER = "tradeName";
    public static final String CHARSEQ_BUFFER = "charSeq";

    @Test
    public void testSalesCsvProcessor() throws Exception {
        System.out.println("test::testSalesCsvProcessor");
        final EventHandler sep =  buildAndInitSep(CsvBuilderTest.class);
        BufferValue aggSales = getField(VAR_BUFFER);
        BufferValue charSeq = getField(CHARSEQ_BUFFER);
        StringDriver.streamChars(  "fred,1234,trader: greg H,EURUSD\n", sep, false);
        Assert.assertEquals("trader: greg H", aggSales.asString());
        Assert.assertEquals("trader: greg H", charSeq.asString());
    }
    

    public static class CsvBuilderTest extends SEPConfig {
        {
            declarativeConfig = BaseSepTest.factorySet(AsciiMatchFilterFactory.class, AnyCharMatchFilterFactory.class);
            BufferValue readBytesDelimited = AsciiHelper.readBytesCsv(2);
            BufferValue readBytesDelimitedInotSeq = AsciiHelper.readCharSeqCsv(2);
            addPublicNode(readBytesDelimited, VAR_BUFFER);
            addPublicNode(readBytesDelimitedInotSeq, CHARSEQ_BUFFER);
        }
    }
    
}
