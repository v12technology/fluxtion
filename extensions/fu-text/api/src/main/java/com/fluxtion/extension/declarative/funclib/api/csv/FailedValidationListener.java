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
package com.fluxtion.extension.declarative.funclib.api.csv;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.extension.declarative.api.Wrapper;
import com.fluxtion.extension.declarative.funclib.api.event.CharEvent;

/**
 * Helper class that tracks and indicates that a validation failure has occured
 * in a RowProcessor instance.
 *
 * @author gregp
 */
public class FailedValidationListener<T> implements Wrapper<T> {

    @NoEventReference
    private final RowProcessor<T> delegate;
    private char eolChar;
    private boolean eol;

    public FailedValidationListener(RowProcessor<T> delegate) {
        this.delegate = delegate;
        this.eolChar = delegate.eolChar();
    }

    @EventHandler
    public boolean charEvent(CharEvent event) {
        this.eol = event.getCharacter() == eolChar;
        boolean failed = this.eol & !delegate.passedValidation();
        return failed;
    }

    @Override
    public T event() {
        return delegate.event();
    }

    @Override
    public Class<T> eventClass() {
        return delegate.eventClass();
    }

    public int rowCount() {
        return delegate.rowCount();
    }

}
