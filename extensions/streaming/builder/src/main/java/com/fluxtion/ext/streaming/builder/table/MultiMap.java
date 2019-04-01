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
package com.fluxtion.ext.streaming.builder.table;

/**
 *
 * @author gregp
 */
import java.util.*;

public class MultiMap<K, V> {

    private Map<K, List<V>> map = new HashMap<>();

    public void put(K key, V value) {
        if (map.get(key) == null) {
            map.put(key, new ArrayList<>());
        }

        map.get(key).add(value);
    }

    public void putIfAbsent(K key, V value) {
        if (map.get(key) == null) {
            map.put(key, new ArrayList<>());
        }

        // if value is absent, insert it
        if (!map.get(key).contains(value)) {
            map.get(key).add(value);
        }
    }

    public List<V> get(Object key) {
        return map.getOrDefault(key, Collections.EMPTY_LIST);
    }


    public Set<K> keySet() {
        return map.keySet();
    }


    public Set<Map.Entry<K, List<V>>> entrySet() {
        return map.entrySet();
    }


    public Collection<List<V>> values() {
        return map.values();
    }


    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }


    public Collection<V> remove(Object key) {
        return map.remove(key);
    }

    public int size() {
        int size = 0;
        for (Collection<V> value : map.values()) {
            size += value.size();
        }
        return size;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void clear() {
        map.clear();
    }

    public boolean remove(K key, V value) {
        if (map.get(key) != null) // key exists
        {
            return map.get(key).remove(value);
        }

        return false;
    }

    public boolean replace(K key, V oldValue, V newValue) {

        if (map.get(key) != null) {
            if (map.get(key).remove(oldValue)) {
                return map.get(key).add(newValue);
            }
        }
        return false;
    }
}
