/*
 * Copyright (C) 2020 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.integration.eventflow;

import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.ext.text.builder.csv.CsvToBeanBuilder;
import com.fluxtion.generator.compiler.OutputRegistry;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.util.HashMap;
import java.util.HashSet;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author V12 Technology Ltd.
 */
@Log4j2
public class MarshallerRegistry implements Lifecycle {

    private HashMap<Class, RowProcessor> marshallerMap;
    private HashSet<Class> blackList;
    private static int count;
    private final int myCount;

    public MarshallerRegistry() {
        myCount = count++;
        log.info("created instance:{}", myCount);
    }

    public RowProcessor getRowProcessor(Object o) {
        final Class<? extends Object> clazz = o.getClass();
        RowProcessor processor = null;
        if (!blackList.contains(clazz)) {
            processor = marshallerMap.computeIfAbsent(clazz, (c) -> {
                RowProcessor processorX = null;
                try {
                    log.info("generating marsahller for:{}", clazz);
                    processorX = CsvToBeanBuilder.buildRowProcessor(c, "com.fluxtion.csvmarshaller.autogen");
                } catch (Exception e) {
                    log.info("problem generating marshaller", e);
                    blackList.add(c);
                }
                return processorX;
            });
        }
        return processor;
    }

    @Override
    public void init() {
        marshallerMap = new HashMap<>();
        blackList = new HashSet();
        log.info("init starting scan for csv marshallers");
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .addClassLoader(OutputRegistry.INSTANCE.getClassLoader())
                .scan()) {
            ClassInfoList rowProcessorInfoList = scanResult.getClassesImplementing(RowProcessor.class.getCanonicalName());
            rowProcessorInfoList.forEach(this::addMarshaller);
        }
        log.info("init completed scan for csv marshallers");
        marshallerMap.keySet().stream().forEach(k -> log.info("marsahller:{}", k));
    }

    private void addMarshaller(ClassInfo info) {
        try {
            Class<RowProcessor> marshallerClass = info.loadClass(RowProcessor.class);
            RowProcessor rowProcessor = marshallerClass.getDeclaredConstructor().newInstance();
            log.info("registering marshaller:{} for type:{}", marshallerClass.getName(), rowProcessor.eventClass());
            marshallerMap.put(rowProcessor.eventClass(), rowProcessor);
        } catch (Exception ex) {
            log.warn("unable to load RowProcessor:{}",info, ex);
        }
    }

    @Override
    public void tearDown() {
    }

}
