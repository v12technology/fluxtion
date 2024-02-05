/*
 * Copyright (C) 2018 2024 gregory higgins.
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

import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An StaticEventProcessor partitioner based upon a received event.A partitioner
 * creates an instance of an {@link StaticEventProcessor} and dispatches events
 * to that instance. Partitioning allows a separate memory context for an
 * EventHandler, this can be useful when the structure of processing is repeated
 * but the state is different for each instance.<p>
 * <p>
 * For example monitoring the fuel level on a fleet of cars is the same
 * processing for each car, but an individual car will have a unique fuel level.
 * In this case the StaticEventProcessor can be partitioned on vehicle
 * identification number.
 * <p>
 * <p>
 * The StaticEventProcessor instance will be re-used or a new one created when
 * new events are received. The {@link #partition(SerializableFunction)} methods provide
 * functions that map keys from an incoming event. the key is used
 * manage StaticEventProcessor instances in an underlying map. If no key/value
 * mapping is found then a new StaticEventProcessor is created and handles the
 * incoming message.
 * <p>
 * <p>
 * New instances are created with s {@link Supplier} factory. Optionally an
 * initialiser can be provided that can access the newly created
 * StaticEventProcessor before any messages are processed. Using the car/fuel
 * analogy the initialiser function may set a reference to a global fuel monitor
 * from each newly created car processor.
 *
 * @param <E>
 * @author gregp
 */
public class Partitioner<E extends StaticEventProcessor> implements StaticEventProcessor, Lifecycle, BatchHandler {

    private HashMap<Class, SerializableFunction> class2Function;
    private HashMap<Class, MultiKeyGenerator> class2MultiFunction;
    private final ByteBuffer buffer;
    private final byte[] array;
    private static final int DEFAULT_SIZE = 64;
    private List<Function> charKeyedHandlers;
    private HashMap<Object, E> handlerMap;
    private StaticEventProcessor[] handlerArray;
    private BatchHandler[] batchHandArray;
    private final Supplier<E> factory;
    private Consumer<E> initialiser;

    /**
     * Create a partitioner with a factory and initialiser function.
     *
     * @param factory     factory creating instances of EventHandlers
     * @param initialiser Initialisation function applied to new EventHandlers
     */
    public Partitioner(Supplier<E> factory, Consumer<E> initialiser) {
        this.factory = factory;
        class2Function = new HashMap<>();
        class2MultiFunction = new HashMap<>();
        handlerMap = new HashMap<>();
        handlerArray = new StaticEventProcessor[0];
        batchHandArray = new BatchHandler[0];
        charKeyedHandlers = new ArrayList<>();
        array = new byte[DEFAULT_SIZE];
        buffer = ByteBuffer.wrap(array);
        this.initialiser = initialiser;
    }

    /**
     * Create a partitioner with a factory.
     *
     * @param factory factory creating instances of EventHandlers
     */
    public Partitioner(Supplier<E> factory) {
        this(factory, null);
    }

    /**
     * Register a partition key generator function that creates a
     * {@link CharSequence} key from an incoming event.The key values from
     * function are interpreted with the following logic:
     * <ul>
     * <li>null - no match and no dispatch
     * <li>'*' - will dispatch to all EventHandlers i.e. a broadcast
     * <li>[CharSrquence] - creates an StaticEventProcessor keyed with this
     * CharSequence
     * </ul>
     *
     * @param <I>
     * @param <K>             Generated key
     * @param partitionKeyGen key mapping function
     */
    public <I, K extends CharSequence> void keyPartitioner(Function<I, K> partitionKeyGen) {
        charKeyedHandlers.add(partitionKeyGen);
    }

    /**
     * Register a partition key generator function that creates keys from a
     * property on an incoming event. an incoming Event
     *
     * @param <s>      The incoming event
     * @param <t>      The key type
     * @param supplier Key value supplier
     */
    public <s, t> void partition(SerializableFunction<s, t> supplier) {
        Class clazz = supplier.getContainingClass();
        class2Function.put(clazz, supplier);
    }

    /**
     * Register a partition key generator function that creates keys from a set
     * of properties on an incoming event. an incoming Event
     *
     * @param <s>      The incoming event
     * @param <t>      The key type
     * @param supplier Key value suppliers
     */
    public <s, t> void partition(SerializableFunction<s, ?>... supplier) {
        Class clazz = supplier[0].getContainingClass();
        List<SerializableFunction> supplierList = Arrays.asList(supplier);
        class2MultiFunction.put(clazz, new MultiKeyGenerator(supplierList));
    }

    @Override
    public void onEvent(Object e) {
        SerializableFunction f = class2Function.get(e.getClass());
        MultiKeyGenerator multiF = class2MultiFunction.get(e.getClass());
        boolean keyed = charsequenceKeyProcess(e);
        boolean filtered = (f != null | multiF != null | keyed);
        if (f != null) {
            StaticEventProcessor handler = handlerMap.computeIfAbsent(f.apply(e), (t) -> {
                return initialise();
            });
            pushEvent(handler, e);
        }
        if (multiF != null) {
            StaticEventProcessor handler = handlerMap.computeIfAbsent(multiF.generateKey(e), (t) -> {
                multiF.newValueList();
                return initialise();
            });
            pushEvent(handler, e);
        }
        if (!filtered) {
            for (StaticEventProcessor eventHandler : handlerArray) {
                pushEvent(eventHandler, e);
            }
        }
    }

    public E getProcessor(Object key) {
        return handlerMap.get(key);
    }

    private boolean charsequenceKeyProcess(Object e) {
        boolean matched = false;
        for (int i = 0; i < charKeyedHandlers.size(); i++) {
            Function keyGen = charKeyedHandlers.get(i);
            CharSequence key = (CharSequence) keyGen.apply(e);
            if (key != null && key.length() == 1 && key.charAt(0) == '*') {
                return matched;
            }
            matched = true;
            if (key != null) {
                buffer.clear();
                for (int j = 0; j < key.length(); j++) {
                    buffer.put((byte) key.charAt(j));
                }
                buffer.flip();
                E ret = handlerMap.get(buffer);
                if (ret != null) {
                    //invoke
                    pushEvent(ret, e);
                } else {
                    //initialise, add and invoke
                    ret = initialise();
                    handlerMap.put(ByteBuffer.wrap(Arrays.copyOf(array, buffer.limit())), ret);
                    pushEvent(ret, e);
                }
            } else {
            }
        }
        return matched;
    }

    private void pushEvent(StaticEventProcessor handler, Object e) {
        handler.onEvent(e);
    }

    private E initialise() {
//        System.out.println("initialising");
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

        List generateKey(Object e) {
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
