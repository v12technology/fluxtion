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
package com.fluxtion.integration.log4j2;

import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.ext.text.builder.csv.CsvToBeanBuilder;
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
@Log4j2(topic = "fluxtion.journaller.csv")
public class Log4j2CsvJournaller extends PipelineFilter {

    StringBuilder sb = new StringBuilder();
    private static final Logger appLog = LogManager.getLogger(Log4j2CsvJournaller.class);
    private HashMap<Class, RowProcessor> marshallerMap;
    private HashSet<Class> blackList;

    @Override
    public void processEvent(Object o) {
        final Class<? extends Object> clazz = o.getClass();
        if (!blackList.contains(clazz)) {
            sb.setLength(0);
            RowProcessor processor = marshallerMap.computeIfAbsent(clazz, (c) -> {
                RowProcessor processorX = null;
                try {
                    appLog.info("generating marsahller for:{}", clazz);
                    processorX = CsvToBeanBuilder.buildRowProcessor(c, "com.fluxtion.csvmarshaller.autogen");
                } catch (Exception e) {
                    appLog.info("problem generating marshaller", e);
                    blackList.add(c);
                }
                return processorX;
            });
//        RowProcessor processor = marshallerMap.get(o.getClass());
            if (processor != null) {
                try {
                    sb.append(clazz.getSimpleName()).append(',');
                    processor.toCsv(o, sb);
                    log.info(sb);
                } catch (IOException ex) {
                    appLog.warn("could not marshall instabce to csv:", o);
                }
            } else {
                appLog.debug("no marshaller registred for:", clazz);
            }
        }
        propagate(o);
    }

    public void initHandler() {
        marshallerMap = new HashMap<>();
        blackList = new HashSet();
        appLog.info("init scanning for csv marshallers");
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .scan()) {
            ClassInfoList rowProcessorInfoList = scanResult.getClassesImplementing(RowProcessor.class.getCanonicalName());
            rowProcessorInfoList.forEach(this::addMarshaller);
        }
    }

    private void addMarshaller(ClassInfo info) {
        try {
            Class<RowProcessor> marshallerClass = info.loadClass(RowProcessor.class);
            RowProcessor rowProcessor = marshallerClass.getDeclaredConstructor().newInstance();
            appLog.info("registering marshaller:{} for type:{}", marshallerClass.getName(), rowProcessor.eventClass());
            marshallerMap.put(rowProcessor.eventClass(), rowProcessor);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            appLog.warn("unable to load RowProcessor", ex);
        }

    }

}
