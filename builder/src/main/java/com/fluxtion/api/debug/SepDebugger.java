/* 
 * Copyright (C) 2016-2017 V12 Technology Limited. All rights reserved. 
 *
 * This software is subject to the terms and conditions of its EULA, defined in the
 * file "LICENCE.txt" and distributed with this software. All information contained
 * herein is, and remains the property of V12 Technology Limited and its licensors, 
 * if any. This source code may be protected by patents and patents pending and is 
 * also protected by trade secret and copyright law. Dissemination or reproduction 
 * of this material is strictly forbidden unless prior written permission is 
 * obtained from V12 Technology Limited.  
 */
package com.fluxtion.api.debug;

import com.fluxtion.runtime.event.Event;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Greg Higgins
 */
public class SepDebugger {

    private final Semaphore stepSemaphore;
    private final AtomicBoolean stepping;
    private final AtomicBoolean recordStatistics;
    private final LongAdder adder;
    private static final Logger LOG = LoggerFactory.getLogger(SepDebugger.class);
    public final Map<String, NodeStatistic> node2Statistics;
    private final CopyOnWriteArraySet<SepInvocationListener> listenerList;
    private Event currentEvent;

    public SepDebugger(SepInvocationListener invocationListener) {
        this.listenerList = new CopyOnWriteArraySet<>();
        if(invocationListener != null)
            listenerList.add(invocationListener);
        this.node2Statistics = new ConcurrentHashMap<>();
        this.stepSemaphore = new Semaphore(0);
        this.stepping = new AtomicBoolean(true);
        this.recordStatistics = new AtomicBoolean(true);
        this.adder = new LongAdder();
    }

    public SepDebugger() {
        this(new NullListener());
    }

    public int nodeInvocationCount() {
        return adder.intValue();
    }

    /**
     * resets the statistics for another run, but does not remove the node
     * mappings
     */
    public void resetStatistics() {
        for (NodeStatistic nodeHeuristic : node2Statistics.values()) {
            nodeHeuristic.resetStatistics();
        }
        adder.reset();
    }

    public void steppingOn(boolean steppingOn) {
        stepping.set(steppingOn);
        stepSemaphore.drainPermits();
        if (!steppingOn) {
            permitStep();
        }
    }

    public void statisticRecordingOn(boolean statisticsOn) {
        recordStatistics.set(statisticsOn);
    }

    /**
     * called by the debug controller to allow next step to happen.The stepper
     * will be blocked on the requestStep call until the permitStep function is
     * called.
     */
    public void permitStep() {
        stepSemaphore.release();
    }

    /**
     * called by the stepper to request permission for the next step to
     * happen.The stepper will be blocked on the requestStep call until the
     * permitStep function is called.
     */
    public void requestStep() throws InterruptedException {
        if (stepping.get()) {
            stepSemaphore.acquire();
        }
    }

    /**
     * removes all internal state from the SepDebugger.
     */
    public void resetDebugger() {
        node2Statistics.clear();
        stepping.set(true);
        recordStatistics.set(true);
        currentEvent = null;
    }

    public synchronized void nodeInvocation( Object node, String name) {
        try {
            for (SepInvocationListener invocationListener : listenerList) {
                invocationListener.nodePreInvocation(currentEvent, node, name);
            }
            requestStep();
        } catch (InterruptedException ex) {
            //decide what to do - probably just log
            return;
        }
        NodeStatistic heuristic = node2Statistics.get(name);
        if (heuristic == null) {
            heuristic = new NodeStatistic(name);
            node2Statistics.put(name, heuristic);
        }
        LOG.debug("node invocation node:{} count:{}", name, adder.intValue());
        heuristic.incrementCallCount();
        adder.increment();
    }

    public void addSepInvocationListener(SepInvocationListener l) {
        listenerList.add(l);
    }

    public void removeSepInvocationListener(SepInvocationListener l) {
        listenerList.remove(l);
    }

    public void eventInvocation(Event event) {
        this.currentEvent = event;
        //TODO add stats for recording the event cound, both filtered and aggregated by ID
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }
    
    private static final class NullListener implements SepInvocationListener {

        @Override
        public void nodePreInvocation(Event event, Object node, String nodeName) {
        }

    }
}
