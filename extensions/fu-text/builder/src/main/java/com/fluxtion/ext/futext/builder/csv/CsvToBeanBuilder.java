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
package com.fluxtion.ext.futext.builder.csv;

import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.ext.futext.api.util.marshaller.DispatchingCsvMarshaller;
import static com.fluxtion.ext.futext.builder.csv.CsvMarshallerBuilder.csvMarshaller;
import static com.fluxtion.generator.compiler.InprocessSepCompiler.sepInstance;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 *
 * @author V12 Technology Ltd.
 */
public class CsvToBeanBuilder {

    private final String pckg;
    private HashMap<Class, EventHandler> clazz2Handler;

    public CsvToBeanBuilder(String pckg) {
        this.pckg = pckg;
        clazz2Handler = new HashMap<>();
    }

    public static CsvToBeanBuilder nameSpace(String pkg) {
        return new CsvToBeanBuilder(pkg);
    }

    public CsvToBeanBuilder mapBean(String marshallerId, Class clazz) {
        try {
            String cap = marshallerId.substring(0, 1).toUpperCase() + marshallerId.substring(1) + "CsvBean";
            EventHandler sep = sepInstance((cfg) -> csvMarshaller(clazz).build(), pckg + "." + marshallerId, cap);
            clazz2Handler.put(clazz, sep);
            return this;
        } catch (Exception ex) {
            throw new RuntimeException("failed to map bean class:" + clazz, ex);
        }
    }

    public <T> CsvToBeanBuilder mapBean(String marshallerId, Class<T> clazz, Consumer<RulesEvaluatorBuilder.BuilderRowProcessor<T>> ruleGenerator) {
        try {
            String cap = marshallerId.substring(0, 1).toUpperCase() + marshallerId.substring(1) + "CsvBean";
            EventHandler sep = sepInstance(new Consumer<SEPConfig>() {
                @Override
                public void accept(SEPConfig cfg) {
                    RulesEvaluatorBuilder.BuilderRowProcessor<T> validator1 = RulesEvaluatorBuilder.validator(csvMarshaller(clazz).build());
                    ruleGenerator.accept(validator1);
                    validator1.build();
                }
            }, pckg + "." + marshallerId, cap);
            clazz2Handler.put(clazz, sep);
            return this;
        } catch (Exception ex) {
            throw new RuntimeException("failed to map bean class:" + clazz, ex);
        }
    }

    public DispatchingCsvMarshaller build() {
        DispatchingCsvMarshaller dispatcher = new DispatchingCsvMarshaller();
        clazz2Handler.forEach(dispatcher::addMarshaller);
        return dispatcher;
    }

    public DispatchingCsvMarshaller build(EventHandler sink) {
        DispatchingCsvMarshaller dispatcher = new DispatchingCsvMarshaller();
        clazz2Handler.forEach(dispatcher::addMarshaller);
        dispatcher.addSink(sink);
        return dispatcher;
    }
}
