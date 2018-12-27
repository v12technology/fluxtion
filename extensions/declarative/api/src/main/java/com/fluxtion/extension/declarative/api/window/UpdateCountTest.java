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
package com.fluxtion.extension.declarative.api.window;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.api.generation.GenerationContext;
import com.fluxtion.extension.declarative.api.Test;
import java.util.Objects;

/**
 * Tracks an object for change notifications and tests for update count is
 * greater than limit. Where the update count is incremented if the tracked
 * object notifies of a change. The count can be reset by tracking a reset
 * notifier.
 *
 * @author Greg Higgins
 */
public class UpdateCountTest implements Test {

    public Object observed;
    public int count;
    public int notifiyLimit;
    private int _notifiyLimit;
    public Object resetNotifier;

    public UpdateCountTest() {
    }

    private UpdateCountTest(Object observed, int limit, Object resetNotifier) {
        this.observed = observed;
        this.notifiyLimit = limit;
        this.resetNotifier = resetNotifier;
    }

    public static Test updateCount(Object observed, int limit) {
        return updateCount(observed, limit, null);
    }

    public static Test updateCount(Object observed, int limit, Object resetNotifier) {
        Test test = new UpdateCountTest(observed, limit, resetNotifier);
        GenerationContext.SINGLETON.getNodeList().add(test);
        return test;
    }

    @OnParentUpdate("resetNotifier")
    public void reset(Object resetNotifier) {
        count = 0;
    }

    @OnParentUpdate("observed")
    public void incrementUpdateCount(Object observed) {
        count++;
    }

    @OnEvent
    public boolean update() {
        boolean breached = count >= _notifiyLimit;
        if (breached) {
            count = 0;
        }
        return breached;
    }

    @Initialise
    public void init() {
        count = 0;
        _notifiyLimit = notifiyLimit;
    }

    public void setObserved(Object observed) {
        this.observed = observed;
    }

    public void setNotifiyLimit(int notifiyLimit) {
        this.notifiyLimit = notifiyLimit;
    }

    public void setResetNotifier(Object resetNotifier) {
        this.resetNotifier = resetNotifier;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.observed);
        hash = 37 * hash + this.notifiyLimit;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UpdateCountTest other = (UpdateCountTest) obj;
        if (this.notifiyLimit != other.notifiyLimit) {
            return false;
        }
        if (!Objects.equals(this.observed, other.observed)) {
            return false;
        }
        return true;
    }

}
