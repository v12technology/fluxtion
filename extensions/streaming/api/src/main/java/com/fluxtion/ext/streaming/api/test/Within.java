/* 
 * Copyright (C) 2019 V12 Technology Ltd.
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
package com.fluxtion.ext.streaming.api.test;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.IntFilterEventHandler;
import com.fluxtion.api.SepContext;
import com.fluxtion.ext.streaming.api.Test;
import com.fluxtion.ext.streaming.api.Wrapper;

/**
 * A {@link Test} that reports true if the monitored object has notified a
 * change within the window specified. The window can be any numeric value
 * although it is expected usually to be time.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class Within implements Test {

    private final int expireMillis;
    private final Object monitored;
    private int lastTs;
    private int lastMonitoredTs;
    private boolean MonitoredUpdated;

    public Within(int expireMillis, Object monitored) {
        this.expireMillis = expireMillis;
        this.monitored = monitored;
        lastMonitoredTs = -expireMillis - 1;
        lastTs = 0;
    }

    public void setTime(int lastTs) {
        this.lastTs = lastTs;
    }

    @OnParentUpdate("monitored")
    public void monitoredUpdate(Object monitored) {
        MonitoredUpdated = true;
    }

    @OnEvent
    public boolean expireTest() {
        if (MonitoredUpdated) {
            lastMonitoredTs = lastTs;
        }
        MonitoredUpdated = false;
        return passed();
    }

    @Override
    public boolean passed() {
        return (lastTs - lastMonitoredTs) < expireMillis;
    }

    public static <T, S extends Integer, E> Within within(Wrapper<T> t, int millis, SerializableFunction<E, S> supplier) {
        SepContext cfg = SepContext.service();
        Within within = cfg.add(new Within(millis, t));
        Wrapper<E> handler = new IntFilterEventHandler(supplier.getContainingClass());
        cfg.addOrReuse(handler).push(supplier, within::setTime);
        return within;
    }

}
