/*
 * Copyright (c) 2020, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.ext.futext.builder.csv;

import com.fluxtion.ext.futext.builder.util.TextInprocessTest;
import com.fluxtion.ext.text.api.annotation.CsvMarshaller;
import com.fluxtion.ext.text.api.csv.RowProcessor;
import static com.fluxtion.ext.text.builder.csv.CsvMarshallerBuilder.csvMarshaller;
import java.io.IOException;
import lombok.Data;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class SerialiseTest extends TextInprocessTest{
    
    
    @Test
    public void testSerialiseToCsv() throws IOException{
        
        
        sep(c -> {
            c.addPublicNode(csvMarshaller(BeanSample.class).build(), "output");
        });
        RowProcessor<BeanSample> processor = getField("output");
        
        BeanSample input = new BeanSample();
        input.setIntValue(200);
        input.setStringValue("test");
        StringBuilder sb = new StringBuilder();
        processor.toCsv(input, sb);
        String output = processor.csvHeaders() + "\n" + sb.toString();
        stream(output);
        
        BeanSample sample = getWrappedField("output");
        Assert.assertThat(sample.getIntValue(), is(200));
        Assert.assertThat(sample.getStringValue(), is("test"));
        assertTrue(sample!=input);
        assertTrue(sample.equals(input));
    }
    
    
    @Data
    @CsvMarshaller()
    public static class BeanSample {

        protected String stringValue;
        protected int intValue;

    }
}
