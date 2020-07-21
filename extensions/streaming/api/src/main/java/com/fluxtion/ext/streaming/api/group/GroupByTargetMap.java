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
package com.fluxtion.ext.streaming.api.group;

import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.numeric.BufferValue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A map holding the instances in the group by
 *
 * @param <U> The underlying instance type of the group by
 * @param <K> The underlying instance type of the key of the group by
 * @param <T> The wrapper for elements in the map
 * @author Greg Higgins
 */
public class GroupByTargetMap<K, U, T extends Wrapper<U>> {

    private final Class<T> targetClass;
    private final HashMap<K, T> map = new HashMap<>();
    private final Map<K, T> immutableMap = Collections.unmodifiableMap(map);

    public GroupByTargetMap(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    //TODO add methods for Numeric value, and primitive types
    public T getOrCreateInstance(Object key, GroupByInitializer<K, U> initialiser, K source) {
        T instance = map.get(key);
        if (instance == null) {
            try {
                instance = targetClass.getDeclaredConstructor().newInstance();
                initialiser.apply(source, instance.event());
                map.put((K) key, instance);
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
                map.put((K) key, instance);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
            return instance;
        }
        return instance;
    }

    //use charsequence key!!
    public T getOrCreateInstance(CharSequence key) {
        String keyString = key.toString();
        T instance = map.get(keyString);
        if (instance == null) {
            try {
                instance = targetClass.newInstance();
                map.put((K) keyString, instance);
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
                map.put((K) key.asString(), instance);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return instance;
    }

    public T getOrCreateInstance(CharSequence key, GroupByInitializer<K, U> initialiser, K source) {
        String keyString = key.toString();
        T instance = map.get(keyString);
        if (instance == null) {
            try {
                instance = targetClass.newInstance();
                initialiser.apply(source, instance.event());
                map.put((K) keyString, instance);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return instance;
    }

    public T getOrCreateInstance(BufferValue key, GroupByInitializer<K, U> initialiser, K source) {
        T instance = map.get(key.asString());
        if (instance == null) {
            try {
                instance = targetClass.newInstance();
                initialiser.apply(source, instance.event());
                map.put((K) key.asString(), instance);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return instance;
    }

    public T getOrCreateInstance(MultiKey<K> key, GroupByInitializer<K, U> initialiser, K source) {
        T instance = map.get(key);
        if (instance == null) {
            try {
                instance = targetClass.newInstance();
                initialiser.apply(source, instance.event());
                map.put(key.copyKey(), instance);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return instance;
    }

    public T getOrCreateInstance(MultiKey<K> key) {
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

    public T getInstance(K key) {
        return map.get(key);
    }

    public Map<K, T> getInstanceMap() {
        return immutableMap;
    }

}
