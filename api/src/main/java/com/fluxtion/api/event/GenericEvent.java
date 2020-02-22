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
package com.fluxtion.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A generic event, where the filter is determined by the class type. An event
 * handler can use the following syntax to receive events filtered by generic
 * type:
 * <pre>
 *   
 * {@code @EventHandler
 *  public void someMethod(GenericEvent<MyType> event){
 *     //...
 *  }}
 * </pre>
 * The generated SEP provide all filtering logic within the generated dispatch.
 * The Fluxtion compiler analyses the generic type and sets the
 * {@link  #filterString} to the canonical class name of the type.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 * @param <T> The listener to register
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GenericEvent<T> extends DefaultEvent {

    public final T value;

    public GenericEvent(T value) {
        super(value.getClass().getCanonicalName());
        this.value = value;

    }

    public <V extends T> GenericEvent(Class<T> valueClass, V value) {
        super(valueClass.getCanonicalName());
        this.value = value;
    }

}
