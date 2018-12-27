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
package com.fluxtion.extension.declarative.api.group;

import com.fluxtion.extension.declarative.api.Wrapper;
import com.fluxtion.extension.declarative.api.numeric.BufferValue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A map holding the instances in the group by
 * 
 * @author Greg Higgins
 * @param <U> The underlying instance type of the group by 
 * @param <T> The wrapper for elements in the map
 */
public class GroupByTargetMap< U, T extends Wrapper<U>> {

    private final Class<T> targetClass;
    private final HashMap<? super Object, T> map = new HashMap<>();
    private final Map<? super Object, T> immutableMap = Collections.unmodifiableMap(map);

    public GroupByTargetMap(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    //TODO add methods for Numeric value, and primitive types
    public <K> T getOrCreateInstance(Object key, GroupByIniitialiser initialiser, K source) {
        T instance = map.get(key);
        if (instance == null) {
            try {
                instance = targetClass.getDeclaredConstructor().newInstance();
                GroupByIniitialiser<K, U> f = (GroupByIniitialiser<K, U>) initialiser;
                f.apply(source, instance.event());
                map.put(key, instance);

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } 
        }
        return instance;
    }

    public T getOrCreateInstance(Object key) {
        T instance = map.get(key);
        if (instance == null) {
            try {
                instance = targetClass.newInstance();
                map.put(key, instance);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return instance;

    }

    public T getOrCreateInstance(BufferValue key) {
        T instance = map.get(key.asString());
        if (instance == null) {
            try {
                instance = targetClass.newInstance();
                map.put(key.asString(), instance);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return instance;
    }

    public <K> T getOrCreateInstance(BufferValue key, GroupByIniitialiser initialiser, K source) {
        T instance = map.get(key.asString());
        if (instance == null) {
            try {
                instance = targetClass.newInstance();
                GroupByIniitialiser<K, U> f = (GroupByIniitialiser<K, U>) initialiser;
                f.apply(source, instance.event());
                map.put(key.asString(), instance);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return instance;
    }

    public <K> T getOrCreateInstance(MultiKey key, GroupByIniitialiser initialiser, K source) {
        T instance = map.get(key);
        if (instance == null) {
            try {
                instance = targetClass.newInstance();
                GroupByIniitialiser<K, U> f = (GroupByIniitialiser<K, U>) initialiser;
                f.apply(source, instance.event());
                map.put(key.copyKey(), instance);

            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return instance;
    }

    public T getOrCreateInstance(MultiKey key) {
        T instance = map.get(key);
        if (instance == null) {
            try {
                instance = targetClass.newInstance();
                map.put(key.copyKey(), instance);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return instance;
    }

    public T getInstance(Object key) {
        return map.get(key);
    }

    public Map<? super Object, T> getInstanceMap() {
        return immutableMap;
    }

}
