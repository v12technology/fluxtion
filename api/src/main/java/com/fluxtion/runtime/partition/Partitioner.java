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
package com.fluxtion.runtime.partition;

import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A simple event partitioner based on a single or multiple properties of an
 * event. User register a creation function for an EventHandler and an optional
 * initialiser that
 *
 * @author gregp
 */
public class Partitioner<E extends EventHandler> implements EventHandler, Lifecycle, BatchHandler {

    private HashMap<Class, SerializableFunction> class2Function;
    private HashMap<Class, MultiKeyGenerator> class2MultiFunction;
    private HashMap<Object, EventHandler> handlerMap;
    private EventHandler[] handlerArray;
    private BatchHandler[] batchHandArray;
    private final Supplier<E> factory;
    private Consumer<E> initialiser;

    public Partitioner(Supplier<E> factory, Consumer<E> initialiser) {
        this.factory = factory;
        class2Function = new HashMap<>();
        class2MultiFunction = new HashMap<>();
        handlerMap = new HashMap<>();
        handlerArray = new EventHandler[0];
        batchHandArray = new BatchHandler[0];
        this.initialiser = initialiser;

    }

    public Partitioner(Supplier<E> factory) {
        this(factory, null);
    }

    public <s, t> void partition(SerializableFunction<s, t> supplier) {
        Class clazz = supplier.getContainingClass();
        class2Function.put(clazz, supplier);
    }

    public <s, t> void partition(SerializableFunction<s, ?>... supplier) {
        Class clazz = supplier[0].getContainingClass();
        List<SerializableFunction> supplierList = Arrays.asList(supplier);
        class2MultiFunction.put(clazz, new MultiKeyGenerator(supplierList));
    }

    @Override
    public void onEvent(Event e) {
        SerializableFunction f = class2Function.get(e.getClass());
        MultiKeyGenerator multiF = class2MultiFunction.get(e.getClass());
        boolean filtered = (f != null | multiF != null);
        if (f != null) {
            EventHandler handler = handlerMap.computeIfAbsent(f.apply(e), (t) -> {
                return initialise();
            });
            pushEvent(handler, e);
        }
        if (multiF != null) {
            EventHandler handler = handlerMap.computeIfAbsent(multiF.generateKey(e), (t) -> {
                multiF.newValueList();
                return initialise();
            });
            pushEvent(handler, e);
        }
        if (!filtered) {
            for (EventHandler eventHandler : handlerArray) {
                pushEvent(eventHandler, e);
            }
        }

    }

    private void pushEvent(EventHandler handler, Event e) {
        handler.onEvent(e);
        handler.afterEvent();
    }

    private E initialise() {
        System.out.println("initialising");
        E newHandler = factory.get();
        handlerArray = Arrays.copyOf(handlerArray, handlerArray.length + 1);
        handlerArray[handlerArray.length - 1] = newHandler;
        if (newHandler instanceof Lifecycle) {
            ((Lifecycle) newHandler).init();
        }
        if (newHandler instanceof BatchHandler) {
            batchHandArray = Arrays.copyOf(batchHandArray, batchHandArray.length + 1);
            batchHandArray[batchHandArray.length - 1] = (BatchHandler) newHandler;
        }
        if (initialiser != null) {
            initialiser.accept(newHandler);
        }
        return newHandler;
    }

    @Override
    public void tearDown() {
        Arrays.stream(handlerArray)
                .filter(e -> e instanceof Lifecycle)
                .map(Lifecycle.class::cast)
                .forEach(Lifecycle::tearDown);
    }

    @Override
    public void batchPause() {
        for (BatchHandler batchHandler : batchHandArray) {
            batchHandler.batchPause();
        }
    }

    @Override
    public void batchEnd() {
        for (BatchHandler batchHandler : batchHandArray) {
            batchHandler.batchEnd();
        }
    }

    @Override
    public void init() {
        //do nothing
    }

    private class MultiKeyGenerator {

        List<SerializableFunction> mapper;
        List values;

        MultiKeyGenerator(List<SerializableFunction> mapper) {
            this.mapper = mapper;
            values = new ArrayList();
        }

        List generateKey(Event e) {
            values.clear();
            for (int i = 0; i < mapper.size(); i++) {
                SerializableFunction f = mapper.get(i);
                values.add(f.apply(e));
            }
            return values;
        }

        void newValueList() {
            values = new ArrayList();
        }
    }

}
