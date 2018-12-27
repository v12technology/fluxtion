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
package com.fluxtion.extension.declarative.builder.table;

/**
 *
 * @author gregp
 */
import java.util.*;

public class MultiMap<K, V> {

    private Map<K, List<V>> map = new HashMap<>();

    /**
     * Add the specified value with the specified key in this multimap.
     */
    public void put(K key, V value) {
        if (map.get(key) == null) {
            map.put(key, new ArrayList<>());
        }

        map.get(key).add(value);
    }

    /**
     * Associate the specified key with the given value if not already
     * associated with a value
     */
    public void putIfAbsent(K key, V value) {
        if (map.get(key) == null) {
            map.put(key, new ArrayList<>());
        }

        // if value is absent, insert it
        if (!map.get(key).contains(value)) {
            map.get(key).add(value);
        }
    }

    /**
     * Returns the Collection of values to which the specified key is mapped, or
     * empty collection if this multimap contains no mapping for the key.
     */
    public List<V> get(Object key) {
        return map.getOrDefault(key, Collections.EMPTY_LIST);
    }

    /**
     * Returns a Set view of the keys contained in this multimap.
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * Returns a Set view of the mappings contained in this multimap.
     */
    public Set<Map.Entry<K, List<V>>> entrySet() {
        return map.entrySet();
    }

    /**
     * Returns a Collection view of Collection of the values present in this
     * multimap.
     */
    public Collection<List<V>> values() {
        return map.values();
    }

    /**
     * Returns true if this multimap contains a mapping for the specified key.
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /**
     * Removes the mapping for the specified key from this multimap if present.
     * and returns the Collection of previous values associated with key, or
     * null if there was no mapping for key.
     */
    public Collection<V> remove(Object key) {
        return map.remove(key);
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    public int size() {
        int size = 0;
        for (Collection<V> value : map.values()) {
            size += value.size();
        }
        return size;
    }

    /**
     * Returns true if this map contains no key-value mappings.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Removes all of the mappings from this map.
     */
    public void clear() {
        map.clear();
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value and return true if removed
     */
    public boolean remove(K key, V value) {
        if (map.get(key) != null) // key exists
        {
            return map.get(key).remove(value);
        }

        return false;
    }

    /**
     * Replaces the entry for the specified key only if currently mapped to the
     * specified value and return true if replaced
     */
    public boolean replace(K key, V oldValue, V newValue) {

        if (map.get(key) != null) {
            if (map.get(key).remove(oldValue)) {
                return map.get(key).add(newValue);
            }
        }
        return false;
    }
}
