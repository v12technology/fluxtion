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

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.annotations.runtime.ServiceRegistered;
import com.fluxtion.runtime.event.NamedFeedEvent;
import com.fluxtion.runtime.input.NamedFeed;
import com.fluxtion.runtime.partition.LambdaReflection;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("all")
public class NamedFeedTableNode<K, V> extends BaseNode implements TableNode<K, V> {

    private final String feedName;
    private final LambdaReflection.SerializableFunction keyMethodReference;
    private final String topicName;
    private transient final Map tableMap = new HashMap();
    private transient final Map tableMapReadonly = Collections.unmodifiableMap(tableMap);
    private long lastSequenceNumber;

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

    @Initialise
    public void initialise() {
        lastSequenceNumber = -1;
    }

    @ServiceRegistered
    public void serviceRegistered(NamedFeed feed, String feedName) {
        if (feedName != null && feedName.equals(this.feedName)) {
            auditLog.info("requestSnapshot", feedName)
                    .info("snapshot", feed.lastUpdate());
            tableUpdate(feed.lastUpdate());
        } else {
            auditLog.info("ignoreFeedSnapshot", feedName);
        }
    }

    @SneakyThrows
    @OnEventHandler(filterVariable = "feedName")
    public boolean tableUpdate(NamedFeedEvent feed) {
        if (feed.getSequenceNumberStart() > lastSequenceNumber & (topicName == null || topicName.equals(feed.getTopic()))) {
            List data = feed.getData();
            lastSequenceNumber = feed.getSequenceNumberStart();
            for (int i = 0, dataSize = data.size(); i < dataSize; i++) {
                Object datum = data.get(i);
                Object key = keyMethodReference.apply(datum);
                auditLog.debug("received", feed);
                if (feed.isDelete()) {
                    auditLog.debug("deletedKey", key);
                    tableMap.remove(key);
                } else {
                    auditLog.debug("putKey", key);
                    tableMap.put(key, datum);
                }
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
