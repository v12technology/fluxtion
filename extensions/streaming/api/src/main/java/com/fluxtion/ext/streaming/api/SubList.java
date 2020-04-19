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
package com.fluxtion.ext.streaming.api;

import com.fluxtion.api.SepContext;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnParentUpdate;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Data
public class SubList<T> implements WrappedList<T> {

    private final WrappedList<T> parent;
    private final int start;
    private final int stop;
    private transient int sourceSize;
    private List<T> unmodifiableCollection;

    @OnParentUpdate
    public void parentUpdate(WrappedList<T> parent) {
        updatePointers();
    }

    @Override
    public List<T> collection() {
        return unmodifiableCollection;
    }

    @Override
    public WrappedList<T> top(int n) {
        return SepContext.service().addOrReuse(new SubList<>(this, 0, n));
    }
    
    @Override
    public WrappedList<T> last(int n) {
        return SepContext.service().addOrReuse(new SubList<>(this, -n, 0));
    }
    
    @Override
    public WrappedList<T> skip(int n) {
        return SepContext.service().addOrReuse(new SubList<>(this, n, -n));
    }

    @Initialise
    public void init() {
        sourceSize = -1;
        updatePointers();
    }

    private void updatePointers() {
        int size = parent.size();
        if (sourceSize < 0 | size != sourceSize) {
            if (start < 0) {
                //last
                unmodifiableCollection = parent.subList(Math.max((size+start), 0), size);
            } else if (stop < 0) {
                //skip
                unmodifiableCollection = parent.subList(Math.min(size, start), size);
            }else{
                //normal list
                unmodifiableCollection = parent.subList(0, Math.min(stop, size));
            }
        }
    }

}
