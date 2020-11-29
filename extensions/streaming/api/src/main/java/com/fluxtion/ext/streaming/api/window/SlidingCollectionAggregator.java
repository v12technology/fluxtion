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
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T>
 */
public class SlidingCollectionAggregator<T extends WrappedCollection> {

    @SepNode
    private final Object notifier;
    private Stateful currentWindow;
    @NoEventReference
    private final WrappedCollection<T, ?, ?> source;
    
    private final int bucketCount;
    @PushReference
    private ArrayListWrappedCollection<T> targetCollection;
    private transient ArrayListWrappedCollection<T> primingCollection;
    private ArrayDeque<Stateful> deque;
//    @NoEventReference
    @SepNode
    private TimeReset timeReset;
    private int publishCount;
    private transient final boolean logging = false;
    private boolean addedToCurrentWindow;
    private boolean publish;

    public SlidingCollectionAggregator(Object notifier, WrappedCollection<T, ?, ?> source, int bucketCount) {
        this.notifier = notifier;
        this.source = source;
        this.bucketCount = bucketCount;
        targetCollection = SepContext.service().addOrReuse(new ArrayListWrappedCollection<>());
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
//        publish |= publishCount + 1 >= bucketCount;
        ArrayListWrappedCollection collection = publish ? targetCollection : primingCollection;
        if (publish & !primingCollection.isEmpty()) {
            log("switching to target collection");
            targetCollection.combine(primingCollection);
            collection = targetCollection;
            primingCollection.reset();
        }
        if (expiredBuckete == 0) {//in the same window
//            currentWindow.reset();
            currentWindow.combine(source);
            source.reset();
            addedToCurrentWindow = true;
            log("0 expired current window:", currentWindow);
        } else {
            log("expired: " + expiredBuckete);
            if (!addedToCurrentWindow) {
                addedToCurrentWindow = false;
                currentWindow.reset();
                currentWindow.combine(source);
            }
            source.deduct(currentWindow);
            deque.add(currentWindow);
            collection.combine(currentWindow);
            log("multi expired added:", currentWindow);
            log("multi expired source:", source);
            currentWindow = deque.pop();
            collection.deduct(currentWindow);
            currentWindow.reset();
            currentWindow.combine(source);
            log("multi expired current window:", currentWindow);
            for (int i = 1; i < expiredBuckete; i++) {
                Stateful popped2 = deque.poll();
                log("multi expired popped:", popped2);
                collection.deduct(popped2);
                popped2.reset();
                deque.add(popped2);
            }
            log("multi expired collection:", collection);
        }
        log("deque: ");
        deque.forEach(c -> log("\t", c));
        if (publish) {
            log("publishing");
        }
        log("\n");
        return publish;
    }

    private void log(String message) {
        if (!logging) {
            return;
        }
        System.out.println(message);
    }

    private void log(String prefix, Stateful holder) {
        if (!logging) {
            return;
        }
        log(prefix, holder, null);
    }

    private void log(String prefix, Stateful holder, String suffix) {
        if (!logging) {
            return;
        }
        if (holder instanceof ArrayListWrappedCollection) {
            ArrayListWrappedCollection collection = (ArrayListWrappedCollection) holder;
            System.out.println(prefix + " " + collection.collection() + (suffix == null ? "" : suffix));
        } else if (holder instanceof WrappedCollection) {
            WrappedCollection collection = (WrappedCollection) holder;
            System.out.println(prefix + " " + collection.collection() + (suffix == null ? "" : suffix));

        }
        if (holder instanceof ArrayDeque) {
            ArrayDeque dequeLocal = (ArrayDeque) holder;
            System.out.println(prefix + " " + dequeLocal.toString() + (suffix == null ? "" : suffix));
            dequeLocal.toString();
        }
    }

    public WrappedList<T> comparator(Comparator comparator) {
        return SepContext.service().addOrReuse(targetCollection).comparator(comparator);
    }

    public List<T> collection() {
        return publishCount >= bucketCount ? targetCollection.collection() : List.of();
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
                final Stateful function = new ArrayListWrappedCollection<>();
                function.reset();
                deque.add(function);
            }
        } catch (Exception ex) {
            throw new RuntimeException("missing default constructor for:" + source.getClass());
        }
    }

}
