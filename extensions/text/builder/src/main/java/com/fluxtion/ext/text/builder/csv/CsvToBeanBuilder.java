/*
 * Copyright (C) 2019 V12 Technology Ltd.
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
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.ext.text.api.annotation.CsvMarshaller;
import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.ext.text.api.util.CsvRecordStream;
import com.fluxtion.ext.text.api.util.marshaller.DispatchingCsvMarshaller;
import static com.fluxtion.ext.text.builder.csv.CsvMarshallerBuilder.csvMarshaller;
import com.fluxtion.generator.compiler.InprocessSepCompiler;
import com.fluxtion.generator.compiler.DirOptions;
import static com.fluxtion.generator.compiler.InprocessSepCompiler.sepInstance;
import com.fluxtion.generator.compiler.OutputRegistry;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * Creates multiple SEP processors marshalling CSV to bean instances of multiple
 * types. Generates all marshallers and supporting classes, can be used inline
 * or as part of the build process via the {@link CsvAnnotationBeanBuilder} The
 * bean classes configured will all push their marshalled instances to a single
 * {@link DispatchingCsvMarshaller} for multiplexed dispatch. A single instance
 * of this class can be used to generate multiple SEP processors, compared to
 * using multiple {@link SEPConfig} classes.
 *
 * @author V12 Technology Ltd.
 */
public class CsvToBeanBuilder {

    private final String pckg;
    private HashMap<Class, StaticEventProcessor> clazz2Handler;
    private List<RowProcessor> processors;
    private String resorcesDir;
    private String generatedDir;
    private boolean addEventPublisher;

    public CsvToBeanBuilder(String pckg) {
        this.pckg = pckg;
        addEventPublisher = true;
//        this.resorcesDir = OutputRegistry.RESOURCE_DIR;
//        this.generatedDir = OutputRegistry.JAVA_GEN_DIR;
        clazz2Handler = new HashMap<>();
        processors = new ArrayList<>();
    }

    /**
     * Create a generation context for a CSV to bean marshaller context.
     *
     * @param pkg unique namespace for the generated marshallers.
     * @return CsvToBeanBuilder for fluent api calls.
     */
    public static CsvToBeanBuilder nameSpace(String pkg) {
        return new CsvToBeanBuilder(pkg);
    }

    public static <T> RowProcessor<T> buildRowProcessor(Class<T> csvClass) {
        CsvMarshaller annotation = csvClass.getAnnotation(CsvMarshaller.class);
        int headerLines = annotation == null ? 1 : annotation.headerLines();
        return CsvToBeanBuilder.nameSpace(annotation.packageName())
                .builder(csvClass, headerLines)
                .build();
    }

    public static <T> RowProcessor<T> buildRowProcessor(Class<T> csvClass, String packageName) {
        CsvMarshaller annotation = csvClass.getAnnotation(CsvMarshaller.class);
        int headerLines = annotation == null ? 1 : annotation.headerLines();
        return CsvToBeanBuilder.nameSpace(packageName)
                .builder(csvClass, headerLines)
                .build();
    }

    public static <T> RowProcessor<T> buildRowProcessor(Class<T> csvClass, String packageName, DirOptions dirOption) {
        CsvMarshaller annotation = csvClass.getAnnotation(CsvMarshaller.class);
        int headerLines = annotation == null ? 1 : annotation.headerLines();
        return CsvToBeanBuilder.nameSpace(packageName)
                .dirOption(dirOption)
                .builder(csvClass, headerLines)
                .build();
    }

    /**
     * Add event publisher
     *
     * @return
     */
    public CsvToBeanBuilder addEventPublisher() {
        this.addEventPublisher = true;
        return this;
    }

    public CsvToBeanBuilder removeEventPublisher() {
        this.addEventPublisher = false;
        return this;
    }

    /**
     * Write generated artifacts to the standard maven test generation
     * directories.
     *
     * @param deployAsTest true indicates write to test directories
     * @return CsvToBeanBuilder for fluent api calls.
     */
    public CsvToBeanBuilder dirOption(DirOptions dirOption) {
        OutputRegistry.INSTANCE.setDirOptions(dirOption);
        return this;
    }

    /**
     * explicitly set the directories where the artifacts generated by Fluxtion
     * compiler will be written to.
     *
     * @param generatedDir - source directory to store outputs for generated SEP
     * @param resorcesDir - meta-data storage directory
     * @return
     */
    public CsvToBeanBuilder setOutputDirs(String generatedDir, String resorcesDir) {
        this.resorcesDir = resorcesDir;
        this.generatedDir = generatedDir;
        return this;
    }

    /**
     * Map a bean with no customisation or validation. The namespace of the
     * CsvToBeanBuilder is the union of {@link #nameSpace(java.lang.String) ) and
     * the marshallerId.
     *
     * @param marshallerId a unique identifier for the generated marshaller
     * @param clazz
     * @return
     */
    public <T> CsvToBeanBuilder mapBean(String marshallerId, Class<T> clazz) {
        setupContext();
        CsvMarshallerBuilder<T> builder = csvMarshaller(clazz).tokenConfig(CharTokenConfig.WINDOWS);
        builder.addEventPublisher = addEventPublisher;
        processors.add(builder.build());
        return this;
    }

    public <T> CsvMarshallerBuilder<T> builder(Class<T> clazz, int headerLines) {
        setupContext();
        CsvMarshallerBuilder<T> builder = csvMarshaller(clazz, headerLines).tokenConfig(CharTokenConfig.WINDOWS);
        return builder;
    }

    /**
     * Map a bean with a set of validation rules added to the provided
     * {@link RulesEvaluatorBuilder.BuilderRowProcessor} by the caller.
     *
     * @param <T>
     * @param marshallerId
     * @param clazz
     * @param ruleGenerator
     * @return
     */
    public <T> CsvToBeanBuilder mapBean(String marshallerId, Class<T> clazz, Consumer<RulesEvaluatorBuilder.BuilderRowProcessor<T>> ruleGenerator) {
        try {
            String cap = "FluxCsv" + marshallerId.substring(0, 1).toUpperCase() + marshallerId.substring(1) + "Mediator";
            StaticEventProcessor sep = sepInstance(new Consumer<SEPConfig>() {
                @Override
                public void accept(SEPConfig cfg) {
                    final CsvMarshallerBuilder<T> csvMarshaller = csvMarshaller(clazz);
                    csvMarshaller.addEventPublisher = addEventPublisher;
                    RulesEvaluatorBuilder.BuilderRowProcessor<T> validator1 = RulesEvaluatorBuilder.validator(csvMarshaller.build());
                    ruleGenerator.accept(validator1);
                    validator1.build();
                }
            }, pckg + ".fluxCsv" + marshallerId, cap, OutputRegistry.INSTANCE.getGenDir(), OutputRegistry.INSTANCE.getResDir(), true);
            clazz2Handler.put(clazz, sep);
            return this;
        } catch (Exception ex) {
            throw new RuntimeException("failed to map bean class:" + clazz, ex);
        }
    }

    public <T> CsvToBeanBuilder mapCustomBean(String marshallerId, Class<T> clazz, Consumer<CsvMarshallerBuilder<T>> ruleGenerator) {
        setupContext();
        CsvMarshallerBuilder<T> builder = csvMarshaller(clazz).tokenConfig(CharTokenConfig.WINDOWS);
        builder.addEventPublisher = addEventPublisher;
        ruleGenerator.accept(builder);
        processors.add(builder.build());
        return this;
//
//        try {
//            String cap = "FluxCsv" + marshallerId.substring(0, 1).toUpperCase() + marshallerId.substring(1) + "Mediator";
//            StaticEventProcessor sep = sepInstance(new Consumer<SEPConfig>() {
//                @Override
//                public void accept(SEPConfig cfg) {
//                    CsvMarshallerBuilder<T> builder = csvMarshaller(clazz);
//                    builder.addEventPublisher = addEventPublisher;
//                    ruleGenerator.accept(builder);
//                    builder.build();
//                }
//            }, pckg + ".fluxCsv" + marshallerId, cap, generatedDir, resorcesDir, true);
//            clazz2Handler.put(clazz, sep);
//            return this;
//        } catch (Exception ex) {
//            throw new RuntimeException("failed to map bean class:" + clazz, ex);
//        }
    }

    public CsvRecordStream build() {
        return CsvRecordStream.decoders(processors.toArray(new RowProcessor[0]));
    }

    private void setupContext() {
        if (generatedDir != null && resorcesDir != null) {
            GenerationContext.updateContext(pckg, "", new File(generatedDir), new File(resorcesDir));
        } else {
            GenerationContext.updateContext(pckg, "", OutputRegistry.INSTANCE.getGenDirFile(), OutputRegistry.INSTANCE.getResDirFile());
        }
    }
}
