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

import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.futext.builder.util.TextInprocessTest;
import com.fluxtion.ext.text.api.annotation.ColumnName;
import com.fluxtion.ext.text.api.annotation.ConvertField;
import com.fluxtion.ext.text.api.annotation.ConvertToCharSeq;
import com.fluxtion.ext.text.api.annotation.CsvMarshaller;
import com.fluxtion.ext.text.api.annotation.DefaultFieldValue;
import com.fluxtion.ext.text.api.annotation.OptionalField;
import com.fluxtion.ext.text.api.annotation.TrimField;
import static com.fluxtion.ext.text.api.ascii.Conversion.atoi;
import com.fluxtion.ext.text.api.csv.RowProcessor;
import static com.fluxtion.ext.text.builder.csv.CsvMarshallerBuilder.csvMarshaller;
import static com.fluxtion.ext.text.builder.csv.CsvToBeanBuilder.buildRowProcessor;
import com.fluxtion.generator.compiler.InprocessSepCompiler;
import com.fluxtion.generator.compiler.DirOptions;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class AnnotatedBeanCsvTest extends TextInprocessTest {

    @Test
    public void testDefaultValue() {
        sep(c -> {
            c.addPublicNode(csvMarshaller(DefaultValueSample.class).build(), "output");
        });
        DefaultValueSample sample = getWrappedField("output");
        stream("intValue\n\n");
        Assert.assertThat(sample.getIntValue(), is(-1));
        stream("2\n");
        Assert.assertThat(sample.getIntValue(), is(2));

    }

    @Test
    public void testDefaultOptionalValue() {
        sep(c -> {
            c.addPublicNode(csvMarshaller(DefaultWithOptional.class).build(), "output");
        });
        DefaultWithOptional sample = getWrappedField("output");
        stream("requiredInt\n3\n");
        Assert.assertThat(sample.getOptionalInt(), is(-1));
        Assert.assertThat(sample.getOptionalInt2(), is(-2));
        Assert.assertThat(sample.getRequiredInt(), is(3));
        stream("2\n");
        Assert.assertThat(sample.getOptionalInt(), is(-1));
        Assert.assertThat(sample.getOptionalInt2(), is(-2));
        Assert.assertThat(sample.getRequiredInt(), is(2));
    }


    @Test
    public void testNamedColumn() {
        sep(c -> {
            c.addPublicNode(csvMarshaller(OverrideColName.class).build(), "output");
        });
        OverrideColName sample = getWrappedField("output");
        stream("f_name,current_age\n"
                + "greg,25\n");
        Assert.assertThat(sample.getName(), is("greg"));
        Assert.assertThat(sample.getAge(), is(25));
    }
    
    @Test
    public void testTrimmedValue() {
        sep(c -> {
            c.addPublicNode(csvMarshaller(TrimSample.class).build(), "output");
        });
        TrimSample sample = getWrappedField("output");
        stream("stringValue\n   TEST   \n");
        Assert.assertThat(sample.getStringValue(), is("TEST"));
    }

    @Test
    public void testConverter() {
        sep(c -> {
            c.addPublicNode(csvMarshaller(ConverterSample.class).build(), "output");
        });
        ConverterSample sample = getWrappedField("output");
        stream("stringValue\nTEST\n");
        Assert.assertThat(sample.getStringValue(), is("CONVERTED_TEST"));
    }

    public static String convert(CharSequence in) {
        return "CONVERTED_" + in;
    }
    
    @Test
    public void testCustomFieldMarshaller() throws IOException{
        RowProcessor<MarshallerCustomised> processor = buildRowProcessor(MarshallerCustomised.class, 
                pckName(), 
                DirOptions.TEST_DIR_OUTPUT
        );
        StringBuilder sb = new StringBuilder();
        MarshallerCustomised instance = new MarshallerCustomised();
        instance.setStringValue("hello");
        processor.toCsv(instance, sb);
        Assert.assertEquals("OVERWRITTEN_hello\n", sb.toString());
    }
    
    public static void marshall(String field, Appendable msgSink){
        try {
            msgSink.append("OVERWRITTEN_" + field);
        } catch (IOException ex) {
            Logger.getLogger(AnnotatedBeanCsvTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static int times10(CharSequence in) {
        return 10 * atoi(in);
    }

    @Test
    public void testClassAnnotationsValue() {
        sep(c -> {
            c.addPublicNode(csvMarshaller(BeanSample.class).build(), "output");
        });
        BeanSample sample = getWrappedField("output");
        stream("stringValue\njunk\n   TEST   \n");
        Assert.assertThat(sample.getStringValue(), is("TEST"));
    }
    
    @Test
    public void testMultipleAnnotationsValue() {
        sep(c -> {
            c.addPublicNode(csvMarshaller(MultipleAnnotationsBeanSample.class).build(), "output");
        });
        MultipleAnnotationsBeanSample sample = getWrappedField("output");
        stream("stringValue|intValue\njunk\n   TEST   |  56\n");
        Assert.assertThat(sample.getStringValue(), is("   TEST   "));
        Assert.assertThat(sample.getIntValue(), is(560));
        stream("ff|\n");
        Assert.assertThat(sample.getIntValue(), is(-10));
    }
    
    @Test
    public void testEnumValue() {
        sep(c -> {
            c.addPublicNode(csvMarshaller(EnumData.class).build(), "output");
        });
        EnumData sample = getWrappedField("output");
        stream("myState\nOPEN\n");
        Assert.assertThat(sample.getMyState(), is(State.OPEN));
    }

    @Data
    public static class DefaultValueSample {

        @DefaultFieldValue("-1")
        protected int intValue;

    }
    
    @Data
    public static class OverrideColName{
    
        @ColumnName("f_name")
        private String name;
        @ColumnName("current_age")
        private int age;
        
    }

    @Data
    public static class TrimSample {

        @TrimField
        protected String stringValue;

    }

    @Data
    public static class ConverterSample {

        @ConvertField("com.fluxtion.ext.futext.builder.csv.AnnotatedBeanCsvTest#convert")
        protected String stringValue;

    }
    
    @Data
    @CsvMarshaller()
    public static class MarshallerCustomised{
        
        @ConvertToCharSeq("com.fluxtion.ext.futext.builder.csv.AnnotatedBeanCsvTest#marshall")
        protected String stringValue;
    
    }
    
    @Data
    @CsvMarshaller(headerLines = 2, trim = true)
    public static class BeanSample {

        protected String stringValue;

    }

    @Data
    @CsvMarshaller(headerLines = 2, trim = true, fieldSeparator = '|')
    public static class MultipleAnnotationsBeanSample {

        @TrimField(false)
        protected String stringValue;

        @ConvertField("com.fluxtion.ext.futext.builder.csv.AnnotatedBeanCsvTest#times10")
        @DefaultFieldValue("-1")
        protected int intValue;
        
    }
    
    @Data
    public static class DefaultWithOptional{
    
        @OptionalField
        @DefaultFieldValue("-1")
        protected int optionalInt;
    
        @OptionalField(defaultValue = "-2")
        protected int optionalInt2;
        
        protected int requiredInt;
        
    }
    
    public enum State{
        OPEN, CLOSE;
    }
    
    @Data
    public static class EnumData{
        protected State myState;
    
    }

}
