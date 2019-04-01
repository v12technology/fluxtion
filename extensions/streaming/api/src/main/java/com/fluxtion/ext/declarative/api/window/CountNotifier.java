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
package com.fluxtion.ext.declarative.api.window;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnEvent;
import java.util.Objects;

/**
 * A NotifyOnCount node.
 *
 * @author greg
 */
public class CountNotifier {

    private int count;
    public Object trackedInstance;
    public int notifyLimit;
    private int _notifyLimit;

    public CountNotifier(int count, Object trackedInstance) {
        this.count = count;
        this.trackedInstance = trackedInstance;
    }

    @OnEvent
    public boolean trackChange() {
        boolean notify = false;
        count++;
        if (count >= _notifyLimit) {
            count = 0;
            notify = true;
        }
        return notify;
    }

    public int getNotifyLimit() {
        return _notifyLimit;
    }

    public int getCount() {
        return count;
    }

    @Initialise
    public void init() {
        _notifyLimit = notifyLimit;
        trackedInstance = null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.trackedInstance);
        hash = 83 * hash + this.notifyLimit;
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
        final CountNotifier other = (CountNotifier) obj;
        if (this.notifyLimit != other.notifyLimit) {
            return false;
        }
        if (!Objects.equals(this.trackedInstance, other.trackedInstance)) {
            return false;
        }
        return true;
    }
}
