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
package com.fluxtion.integration.eventflow.sinks;

import com.fluxtion.integration.log4j2.*;
import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.ext.text.builder.csv.CsvToBeanBuilder;
import com.fluxtion.generator.compiler.OutputRegistry;
import com.fluxtion.integration.etl.MarshallerRegistry;
import com.fluxtion.integration.eventflow.EventSink;
import com.fluxtion.integration.eventflow.PipelineFilter;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Journals events as CSV records using log4j2. Acts as a pipelinefilter:
 *
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class CsvSink implements EventSink {

    StringBuilder sb = new StringBuilder();
//    private HashMap<Class, RowProcessor> marshallerMap;
//    private HashSet<Class> blackList;
    private EventSink sink;
    private boolean firstPublish;
    private final MarshallerRegistry marshallerRegistry;

    public CsvSink(MarshallerRegistry marshallerRegistry) {
        this.marshallerRegistry = marshallerRegistry;
    }

    @Override
    public void publish(Object o) {
        final Class<? extends Object> clazz = o.getClass();
        RowProcessor processor = marshallerRegistry.getRowProcessor(o);
        sb.setLength(0);
        if (processor != null) {
            try {
                processor.toCsv(o, sb);
                if (sink != null) {
                    if (firstPublish) {
                        sink.publish(processor.csvHeaders());
                    }
                    sink.publish(sb);
                } else {
                    if (firstPublish) {
                        System.out.println(processor.csvHeaders());
                    }
                    System.out.print(sb);
                }
                firstPublish = false;
            } catch (IOException ex) {
                log.warn("could not marshall instabce to csv:", o);
            }
        } else {
            log.debug("no marshaller registred for:", clazz);
        }
    }

    @Override
    public void init() {
//        marshallerMap = new HashMap<>();
//        blackList = new HashSet();
        firstPublish = true;
//        log.info("init scanning for csv marshallers");
//        try (ScanResult scanResult = new ClassGraph()
//                .enableClassInfo()
//                .addClassLoader(OutputRegistry.INSTANCE.getClassLoader())
//                .scan()) {
//            ClassInfoList rowProcessorInfoList = scanResult.getClassesImplementing(RowProcessor.class.getCanonicalName());
//            rowProcessorInfoList.forEach(this::addMarshaller);
//        }
    }

//    private void addMarshaller(ClassInfo info) {
//        try {
//            Class<RowProcessor> marshallerClass = info.loadClass(RowProcessor.class);
//            RowProcessor rowProcessor = marshallerClass.getDeclaredConstructor().newInstance();
//            log.info("registering marshaller:{} for type:{}", marshallerClass.getName(), rowProcessor.eventClass());
//            marshallerMap.put(rowProcessor.eventClass(), rowProcessor);
//        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
//            log.warn("unable to load RowProcessor", ex);
//        }
//
//    }
}
