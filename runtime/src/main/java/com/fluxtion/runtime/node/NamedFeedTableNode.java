/*
 * Copyright (c) 2019, 2024 gregory higgins.
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
package com.fluxtion.runtime.node;

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.event.NamedFeedEvent;
import com.fluxtion.runtime.partition.LambdaReflection;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("all")
public class NamedFeedTableNode<K, V> extends BaseNode implements TableNode<K, V> {

    private final String feedName;
    private final LambdaReflection.SerializableFunction keyMethodReference;
    private final String topicName;
    private transient final Map tableMap = new HashMap();
    private transient final Map tableMapReadonly = Collections.unmodifiableMap(tableMap);

    public NamedFeedTableNode(String feedName, String keyFunction) {
        this(feedName, null, keyFunction);
    }

    @SneakyThrows
    public NamedFeedTableNode(String feedName, String topicName, String keyFunction) {
        Objects.requireNonNull(feedName, "feedName cannot be null");
        Objects.requireNonNull(keyFunction, "keyFunction cannot be null");
        this.feedName = feedName;
        this.topicName = topicName;
        String[] functionString = keyFunction.split("::");
        Class<?> clazz = Class.forName(functionString[0]);
        Method keyMethod = clazz.getMethod(functionString[1]);
        keyMethodReference = LambdaReflection.method2Function(keyMethod);
        Objects.requireNonNull(keyMethodReference, "keyMethodReference cannot be null");
    }

    public <T, R> NamedFeedTableNode(@AssignToField("feedName") String feedName,
                                     @AssignToField("keyMethodReference") LambdaReflection.SerializableFunction<T, R> keyMethodReference) {
        this.feedName = feedName;
        this.keyMethodReference = keyMethodReference;
        this.topicName = null;
    }

    public <T, R> NamedFeedTableNode(@AssignToField("feedName") String feedName,
                                     @AssignToField("topicName") String topicName,
                                     @AssignToField("keyMethodReference") LambdaReflection.SerializableFunction<T, R> keyMethodReference) {
        this.feedName = feedName;
        this.topicName = topicName;
        this.keyMethodReference = keyMethodReference;
    }

    @SneakyThrows
    @OnEventHandler(filterVariable = "feedName")
    public boolean tableUpdate(NamedFeedEvent feed) {
        if (topicName == null || topicName.equals(feed.getTopic())) {
            Object data = feed.getData();
            Object key = keyMethodReference.apply(data);
            auditLog.debug("received", feed);
            if (feed.isDelete()) {
                auditLog.debug("deletedKey", key);
                tableMap.remove(key);
            } else {
                auditLog.debug("putKey", key);
                tableMap.put(key, data);
            }
            return true;
        }
        return false;
    }

    @Override
    public Map<K, V> getTableMap() {
        return tableMapReadonly;
    }
}