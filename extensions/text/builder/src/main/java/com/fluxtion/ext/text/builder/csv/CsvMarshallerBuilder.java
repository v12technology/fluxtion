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
package com.fluxtion.ext.text.builder.csv;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.api.partition.LambdaReflection.SerializableBiConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import static com.fluxtion.builder.generation.GenerationContext.SINGLETON;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.log.LogControlEvent;
import com.fluxtion.ext.streaming.api.log.LogService;
import com.fluxtion.ext.text.api.annotation.CheckSum;
import com.fluxtion.ext.text.api.annotation.ConvertField;
import com.fluxtion.ext.text.api.annotation.ConvertToCharSeq;
import com.fluxtion.ext.text.api.annotation.CsvMarshaller;
import com.fluxtion.ext.text.api.annotation.DefaultFieldValue;
import com.fluxtion.ext.text.api.annotation.OptionalField;
import com.fluxtion.ext.text.api.annotation.TrimField;
import com.fluxtion.ext.text.api.csv.Converters;
import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.ext.text.api.event.RegisterEventHandler;
import com.fluxtion.ext.text.api.util.CharStreamer;
import com.fluxtion.ext.text.api.util.marshaller.CsvRecordMarshaller;
import com.fluxtion.ext.text.builder.util.StringDriver;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Checksum;
import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.reflect.dsl.MethodReflector;
import org.apache.velocity.VelocityContext;

/**
 * A utility class for building delimited marshallers. This class is used at
 * compile time by Fluxtion tool to generate a parser for processing incoming
 * char events that will populate a target instance with field values.</p>
 *
 * The user specifies the target class of the parser, which columns to map,
 * process customisations for each column and configurations for the whole
 * parser. An instance of the target is crested using {@link #build()} and
 * placed in a {@link Wrapper} node. A record terminator initiates a push of
 * parsed fields into the target instance, and notification to child nodes the
 * target instance is ready for processing.
 * </p>
 *
 * To map columns use the
 * {@link #map(java.lang.Class)}, {@link #map(java.lang.String, LambdaReflection.SerializableBiConsumer)}
 * and {@link #map(int, LambdaReflection.SerializableBiConsumer)} functions. The
 * supplied method reference will be invoked with the value from the field on a
 * line terminator.<p>
 *
 * The target type of the map function is detected and a converter is built into
 * the solutions. Default converters are provided for primitive types, String,
 * CharSequence and StringBuilder.</p>
 *
 * To provide a custom converter provide a method reference to the convert
 * function {@link #converter(int, LambdaReflection.SerializableFunction)}. A
 * custom converter is a function that accepts single CharSequence argument and
 * returns a value.</p>
 *
 * An exmple to map world cities from a csv file.</p>
 * This example:
 * <ul>
 * <li>Specifies WorldCity as the target type
 * <li>extracts, columns 0,1,5,6 converts to the correct type and invokes the
 * target method specified in the map function.
 * <li>An instance of WorldCity will be created and re-used for each parse.
 * <li>Column 1 is trimmed of whitepace.
 * <li>1 headerline is specified to be ignored.
 * <li>A custom converter is on columnn 5, that always returns 1.
 * </ul>
 *
 * <pre>
 *
 *
 *             Wrapper&lt;WorldCity&gt; city = csvMarshaller(WorldCity.class)
 *                  .map(0, WorldCity::setCountry)
 *                  .map(1, WorldCity::setCity)
 *                  .trim(1)
 *                  .map(5, WorldCity::setLongitude)
 *                  .converter(5,  CsvMarshallerBuilderTest::always_1)
 *                  .map(6, WorldCity::setLatitudeCharSequence)
 *                  .headerLines(1)
 *                  .build();
 * </pre>
 *
 * Lifecycle methods can be invoked on the target class to help with processing.
 * Two methods are available:</p>
 *
 * <ul>
 * <li>OnEvent annotated method is invoked after the parse, before any child
 * dependencies have access to the target instance. Calculation for derived
 * values in the target bean can be placed in this method.
 * <li>OnEventComplete annotated is invoked after an event processing cycle has
 * run, and all child dependencies have completed their processing. Target
 * instances are re-used between event cycles, this method can be used to clear
 * any state from an instance. Clearing state is useful if derived values are
 * calculated in the OnEvent method
 * </ul>
 *
 *
 * @author Greg Higgins
 * @param <T> target of csvMarshaller
 *
 */
public class CsvMarshallerBuilder<T> extends RecordParserBuilder<CsvMarshallerBuilder<T>, T> {

    private boolean mapBean = true;
    private String checkSumField;
    private boolean trimWhitespace = false;
    private boolean ignoreQuotes = false;
    protected boolean asciiOnlyHeader = true;

    public static <S> CsvMarshallerBuilder<S> csvMarshaller(Class<S> target) {
        return csvMarshaller(target, 1);
    }

    public static <S> CsvMarshallerBuilder<S> csvMarshaller(Class<S> target, int headerLInes) {
        CsvMarshallerBuilder<S> csvMarshallerBuilder = new CsvMarshallerBuilder<>(target, headerLInes);
        return csvMarshallerBuilder;
    }

    private CsvMarshallerBuilder(Class<T> target, int headerLines) {
        super(target, headerLines, false);
        if (headerLines > 0) {
            map(targetClazz);
        }
    }

    public <S extends CharSequence, U> CsvMarshallerBuilder<T> map(int colIndex, SerializableBiConsumer<T, U> targetFunction, SerializableFunction<S, U> converterFunction) {
        map(colIndex, targetFunction);
        return converter(colIndex, converterFunction);
    }

    public <U> CsvMarshallerBuilder<T> map(int colIndex, SerializableBiConsumer<T, U> targetFunction) {
        if (mapBean) {
            srcMappingList.clear();
        }
        Method targetMethod = targetFunction.method(SINGLETON.getClassLoader());
        CsvPushFunctionInfo info = new CsvPushFunctionInfo(importMap);
        info.setTarget(targetClazz, targetMethod, targetId);
        info.setDuplicateField(srcMappingList.stream().anyMatch(s -> s.getFieldIndex() == colIndex));
        info.setSourceColIndex(colIndex);
        srcMappingList.add(info);
        mapBean = false;
        return this;
    }

    public <S extends CharSequence, U> CsvMarshallerBuilder<T> map(String colName, SerializableBiConsumer<T, U> targetFunction, SerializableFunction<S, U> converterFunction) {
        map(colName, targetFunction);
        return converter(colName, converterFunction);
    }

    public <U> CsvMarshallerBuilder<T> map(String colName, SerializableBiConsumer<T, U> targetFunction) {
        if (mapBean) {
            srcMappingList.clear();
        }
        Method targetMethod = targetFunction.method(SINGLETON.getClassLoader());
        mapNamedFieldToMethod(targetMethod, colName);
        mapBean = false;
        return this;
    }

    public <S extends CharSequence, U> CsvMarshallerBuilder<T> map(String colName, SerializableBiConsumer<T, U> targetFunction, boolean optional, SerializableFunction<S, U> converterFunction) {
        map(colName, targetFunction, optional);
        return converter(colName, converterFunction);
    }

    public <U> CsvMarshallerBuilder<T> map(String colName, SerializableBiConsumer<T, U> targetFunction, boolean optional) {
        if (mapBean) {
            srcMappingList.clear();
        }
        Method targetMethod = targetFunction.method(SINGLETON.getClassLoader());
        mapNamedFieldToMethod(targetMethod, colName, optional);
        mapBean = false;
        return this;
    }

    private CsvMarshallerBuilder<T> map(Class clazz) {
        CsvMarshaller annotation = (CsvMarshaller) clazz.getAnnotation(CsvMarshaller.class);
        if (annotation != null) {
            trimWhitespace = annotation.trim();
            acceptPartials(annotation.acceptPartials());
            headerLines(annotation.headerLines());
            includeEventPublisher(annotation.addEventPublisher());
            mappingRow(annotation.mappingRow());
            processEscapeSequences(annotation.processEscapeSequences());
            skipCommentLines(annotation.skipCommentLines());
            asciiOnlyHeader(annotation.asciiOnlyHeader());
            skipEmptyLines(annotation.skipEmptyLines());
            reuseTarget(!annotation.newBeanPerRecord());
            ignoreQuotes(annotation.ignoreQuotes());
            tokenConfig(new CharTokenConfig(annotation.lineEnding(), annotation.fieldSeparator(), annotation.ignoredChars()));
        }

        try {
            for (PropertyDescriptor md : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
                Mirror m = new Mirror();
                Field field;// = clazz.getDeclaredField(md.getName());
                if (md.getWriteMethod() != null) {
                    field = m.on(clazz).reflect().field(md.getName());
                    field.setAccessible(true);
                    String fieldName = md.getName();
                    boolean optional = field.getAnnotation(OptionalField.class) != null;
                    boolean checkSum = field.getAnnotation(CheckSum.class) != null;
                    if (checkSum) {
                        checkSumField = md.getWriteMethod().getName();
                    } else if (!Modifier.isTransient(field.getModifiers())) {
                        mapNamedFieldToMethod(md.getWriteMethod(), fieldName, fieldName.equals("eventTime") || optional);
                        if (md.getReadMethod() != null ) {
                            CsvOutInfo outInfo = new CsvOutInfo();
                            outInfo.setHeadername(fieldName);
                            outInfo.setSrcMethod(md.getReadMethod().getName());
                            final ConvertToCharSeq marshaller = field.getAnnotation(ConvertToCharSeq.class);
                            if(marshaller!=null){
                                String[] converterString = marshaller.value().split("#");
                                MethodReflector method = m.on(converterString[0]).reflect().method(converterString[1]);
                                importMap.addStaticImport(method.withAnyArgs().getDeclaringClass());
                                outInfo.setConverterMethod(method.withAnyArgs().getName());
                            }
                            outSrcList.add(outInfo);
                        }
                    }
                    if (trimWhitespace) {
                        trim(fieldName);
                    }
                    final TrimField trim = field.getAnnotation(TrimField.class);
                    if (trim != null) {
                        colInfo(fieldName).setTrim(trim.value());
                    }
                    final ConvertField converter = field.getAnnotation(ConvertField.class);
                    if (converter != null) {
                        String[] converterString = converter.value().split("#");
                        MethodReflector method = m.on(converterString[0]).reflect().method(converterString[1]);
                        converterMethod(fieldName, method.withAnyArgs());
                    }
                    final OptionalField defaultOptionalValue = field.getAnnotation(OptionalField.class);
                    if (defaultOptionalValue != null && !defaultOptionalValue.defaultValue().isEmpty()) {
                        defaultValue(fieldName, defaultOptionalValue.defaultValue());
                    }
                    final DefaultFieldValue defaultValue = field.getAnnotation(DefaultFieldValue.class);
                    if (defaultValue != null) {
                        defaultValue(fieldName, defaultValue.value());
                    }
                }
//                if (md.getReadMethod() != null ) {
//                    field = m.on(clazz).reflect().field(md.getName());
//                    field.setAccessible(true);
//                    String fieldName = md.getName();
//                    if(!Modifier.isTransient(field.getModifiers())){
//                        CsvOutInfo outInfo = new CsvOutInfo();
//                        outInfo.setHeadername(fieldName);
//                        outInfo.setSrcMethod(md.getReadMethod().getName());
//                        outSrcList.add(outInfo);
//                    }
//                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("could not map java bean to csv header " + ex.getMessage(), ex);
        }
        return this;
    }

    @Override
    public RowProcessor<T> build() {
        if (mapBean) {
//            map(targetClazz);
        }
        importMap.addImport(CsvRecordMarshaller.class);
        importMap.addImport(RegisterEventHandler.class);
        importMap.addImport(StringDriver.class);
        importMap.addImport(CharStreamer.class);
        importMap.addImport(File.class);
        importMap.addImport(IOException.class);
        importMap.addImport(StaticEventProcessor.class);
        importMap.addStaticImport(Converters.class);
        final RowProcessor<T> rowProcessor = super.build();
        return rowProcessor;
    }

    public CsvMarshallerBuilder<T> mappingRow(int lines) {
        this.mappingRow = lines;
        importMap.addImport(Arrays.class);
        importMap.addImport(List.class);
        return this;
    }

    public CsvMarshallerBuilder<T> trim(String colName) {
        colInfo(colName).setTrim(true);
        return this;
    }

    public CsvMarshallerBuilder<T> ignoreQuotes(boolean ignoreQuotes) {
        this.ignoreQuotes = ignoreQuotes;
        return this;
    }

    public CsvMarshallerBuilder<T> asciiOnlyHeader(boolean asciiOnlyHeader) {
        this.asciiOnlyHeader = asciiOnlyHeader;
        return this;
    }

    protected void mapNamedFieldToMethod(Method targetMethod, String colName) {
        mapNamedFieldToMethod(targetMethod, colName, false);
    }

    protected void mapNamedFieldToMethod(Method targetMethod, String colName, boolean optional) {
        CsvPushFunctionInfo info = new CsvPushFunctionInfo(importMap);
        info.setMandatory(!optional);
        info.setTarget(targetClazz, targetMethod, targetId);
        info.setSourceFieldName(colName);
        info.setDuplicateField(srcMappingList.stream().anyMatch(s -> s.getFieldName() != null && s.getFieldName().equals(colName)));
        srcMappingList.add(info);
        if (mappingRow < 1) {
            mappingRow(1);
        }
        if (headerLines < 1) {
            headerLines(1);
        }
    }

    @Override
    protected void updateContext(VelocityContext ctx) {
        ctx.put("csv", true);
        ctx.put("ignoreQuotes", ignoreQuotes);
        ctx.put("asciiOnlyHeader", asciiOnlyHeader);
        importMap.addImport(LogService.class);
        importMap.addImport(LogControlEvent.class);
        if (checkSumField != null) {
            ctx.put("checksum", true);
            ctx.put("checksumField", checkSumField);
            importMap.addImport(java.util.zip.CRC32.class);
            importMap.addImport(Checksum.class);
        }
    }

}
