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
package com.fluxtion.ext.streaming.api.enrich;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.event.Signal;
import com.fluxtion.api.partition.LambdaReflection.SerializableBiConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.util.Tuple;
import java.util.HashMap;
import java.util.Map;

/**
 * Performs a lookup and set for a target instance. The lookup key is provided with a SerializableFunction and the
 * set function is a SerializableBiConsumer, both for the same target class. The lookup map is queried with the result
 * of the key function, and if the result is non-null the value is used with the SerializableBiConsumer to set the value
 * on the target instance. <p>
 * 
 * The map is initially empty and is mutated using Signal events that contain Tuple's for key value pairs.<p>
 * 
 * The id of the map is the filter string used when sending signals so they are routed to the correct instance.<p>
 * <pre>{@code
//registering lookup instance:
    sep((c) ->{
        select(MyNode.class)
            .forEach(new EventDrivenLookup("mylookup", MyNode::getKey, MyNode::setValue)::lookup);
    });

//using the factory method:
    sep((c) ->{
        select(MyNode.class)
            .forEach(lookup("mylookup", MyNode::getKey, MyNode::setValue));
    });
 
 
    MyNode nodeEvent = new MyNode();
    nodeEvent.setKey("hello");
    nodeEvent.setValue("nobody");

    //seed a lookup value with an event
    onEvent(new Signal<Tuple>("mylookup", new Tuple<>("hello", "world")));
    assertThat(nodeEvent.getValue(), is("nobody"));
    //send the event to the processor and the lookup will update MyNode::setValue with "world" for MyNode::getKey == "hello"
    onEvent(nodeEvent);
    assertThat(nodeEvent.getValue(), is("world"));
    }
 * 
 * </pre>
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class EventDrivenLookup {

    private final String id;
    private final transient String id_deleteEntry;
    private final transient String id_replaceMap;
    private final SerializableFunction keyFunction;
    private final SerializableBiConsumer setFunction;
    private Map lookupMap;

    public static <T, S, K, V> SerializableConsumer<T> lookup(String id, SerializableFunction<S, K> keyFunction, SerializableBiConsumer<S, V> setFunction) {
        return new EventDrivenLookup(id, keyFunction, setFunction)::lookup;
    }
    
    public <S, K, V> EventDrivenLookup(String id, SerializableFunction<S, K> keyFunction, SerializableBiConsumer<S, V> setFunction) {
        this.id = id;
        this.id_deleteEntry = id + "_deleteEntry";
        this.id_replaceMap = id + "_replaceMap";
        this.keyFunction = keyFunction;
        this.setFunction = setFunction;
    }

    public <S, K> SerializableFunction<S, K> getKeyFunction() {
        return keyFunction;
    }

    public <S, V> SerializableBiConsumer getSetFunction() {
        return setFunction;
    }

    public void setLookupMap(Map lookupMap) {
        this.lookupMap = lookupMap;
    }

    public <T> void lookup(T target) {
        Object apply = keyFunction.apply(target);
        Object val = lookupMap.get(apply);
        if (val != null) {
            setFunction.accept(target, val);
        }
    }

    @EventHandler(propagate = false, filterVariable = "id")
    public boolean updateMap(Signal<Tuple> update) {
        lookupMap.put(update.getValue().getKey(), update.getValue().getValue());
        return false;
    }

    @EventHandler(propagate = false, filterVariable = "id_deleteEntry")
    public boolean removeEntryMap(Signal<Tuple> update) {
        lookupMap.remove(update.getValue().getKey());
        return false;
    }

    @EventHandler(propagate = false, filterVariable = "id_replaceMap")
    public boolean replaceMap(Signal<Tuple> update) {
        this.lookupMap = (Map) update.getValue().getValue();
        return false;
    }

    @Initialise
    public void initLoolup() {
        lookupMap = new HashMap();
    }

}
