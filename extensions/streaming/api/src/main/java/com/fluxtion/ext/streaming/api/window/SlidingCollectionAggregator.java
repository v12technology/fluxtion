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
package com.fluxtion.ext.streaming.api.window;

import com.fluxtion.api.SepContext;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.PushReference;
import com.fluxtion.api.annotations.SepNode;
import com.fluxtion.ext.streaming.api.ArrayListWrappedCollection;
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.WrappedCollection;
import com.fluxtion.ext.streaming.api.WrappedList;
import com.fluxtion.ext.streaming.api.stream.StreamOperator;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Applies a sliding window to a {@link WrappedCollection}
 * 
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T>
 */
@Slf4j
public class SlidingCollectionAggregator<T extends WrappedCollection> {

    @SepNode
    private final Object notifier;
    private WrappedCollection currentWindow;
    @NoEventReference
    private final WrappedCollection source;
    private final int bucketCount;
    @PushReference
    @SepNode
    private ArrayListWrappedCollection<T> targetCollection;
    private transient ArrayListWrappedCollection<T> primingCollection;
    private ArrayDeque<WrappedCollection> deque;
    @SepNode
    private TimeReset timeReset;
    private int publishCount;
    private boolean addedToCurrentWindow;
    private boolean publish;

    public SlidingCollectionAggregator(Object notifier, T source, int bucketCount) {
        this.notifier = notifier;
        this.source = source;
        this.bucketCount = bucketCount;
        targetCollection = new ArrayListWrappedCollection<>();
    }

    public ArrayListWrappedCollection<T> getTargetCollection() {
        return targetCollection;
    }

    public void setTargetCollection(ArrayListWrappedCollection<T> targetCollection) {
        this.targetCollection = targetCollection;
    }

    public TimeReset getTimeReset() {
        return timeReset;
    }

    public void setTimeReset(TimeReset timeReset) {
        this.timeReset = timeReset;
    }

    public SlidingCollectionAggregator<T> id(String id) {
        return StreamOperator.service().nodeId(this, id);
    }

    @OnEvent
    public boolean aggregate() {
        int expiredBuckete = timeReset == null ? 1 : timeReset.getWindowsExpired();
        publishCount += expiredBuckete;
        publish |= publishCount >= bucketCount;
        ArrayListWrappedCollection collection = publish ? targetCollection : primingCollection;
        if (publish & !primingCollection.isEmpty()) {
            log.debug("switching to target collection");
            targetCollection.combine(primingCollection);
            collection = targetCollection;
            primingCollection.reset();
        }
        if (expiredBuckete == 0) {
            currentWindow.combine(source);
            source.reset();
            addedToCurrentWindow = true;
            logCollection("0 expired current window:", currentWindow);
        } else {
            log.debug("expired: " + expiredBuckete);
            if (!addedToCurrentWindow) {
                addedToCurrentWindow = false;
                currentWindow.reset();
                currentWindow.combine(source);
            }
            source.deduct(currentWindow);
            deque.add(currentWindow);
            collection.combine(currentWindow);
            logCollection("current window added:", currentWindow);
            logCollection("expired source:", source);
            currentWindow = deque.pop();
            collection.deduct(currentWindow);
            currentWindow.reset();
            currentWindow.combine(source);
            logCollection("new current window:", currentWindow);
            for (int i = 1; i < expiredBuckete; i++) {
                WrappedCollection popped2 = deque.poll();
                logCollection("popped:", popped2);
                collection.deduct(popped2);
                popped2.reset();
                deque.add(popped2);
            }
            logCollection("current collection:", collection);
        }
        logDeque();
        if (publish) {
            log.debug("publishing");
        }
        return publish;
    }

    public void logDeque(){
        if (!log.isDebugEnabled()) {
            return;
        }
        StringBuilder sb = new StringBuilder("deque:\n");
        deque.forEach(c -> sb.append("\t").append(c.collection()).append("\n"));
        log.debug(sb.toString());
    }

    private void logCollection(String prefix, Stateful holder) {
        logCollection(prefix, holder, null);
    }

    private void logCollection(String prefix, Stateful holder, String suffix) {
        if (!log.isDebugEnabled()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (holder instanceof WrappedCollection) {
            WrappedCollection collection = (WrappedCollection) holder;
            sb.append(prefix).append(" ").append(collection.collection()).append((suffix == null ? "" : suffix));
        }
        log.debug(sb.toString());
    }

    public WrappedList<T> comparator(Comparator comparator) {
        return SepContext.service().addOrReuse(targetCollection).comparator(comparator);
    }

    public List<T> collection() {
        return publishCount >= bucketCount ? targetCollection.collection() : Collections.emptyList();
    }

    @Initialise
    public void init() {
        try {
            deque = new ArrayDeque<>(bucketCount);
            primingCollection = new ArrayListWrappedCollection<>();
            targetCollection.reset();
            primingCollection.reset();         
            currentWindow = new ArrayListWrappedCollection<>();
            for (int i = 0; i < bucketCount; i++) {
                final WrappedCollection function = new ArrayListWrappedCollection<>();
                function.reset();
                deque.add((T) function);
            }
        } catch (Exception ex) {
            throw new RuntimeException("missing default constructor for:" + source.getClass());
        }
    }

}
