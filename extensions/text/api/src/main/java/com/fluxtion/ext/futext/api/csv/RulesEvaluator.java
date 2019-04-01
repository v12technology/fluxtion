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
package com.fluxtion.ext.futext.api.csv;

import com.fluxtion.ext.declarative.api.Wrapper;

/**
 * A RulesEvaluator aggregates a set of rules and reports success if all rules
 * are valid.
 *
 * @author V12 Technology Ltd.
 */
public class RulesEvaluator<T> {

    private final Wrapper<T> validated;
    private final Wrapper<T> failedValidation;

    public RulesEvaluator(Wrapper<T> validated, Wrapper<T> failedValidation) {
        this.validated = validated;
        this.failedValidation = failedValidation;
    }

    public Wrapper<T> passedNotifier() {
        return validated;
    }

    public Wrapper<T> failedNotifier() {
        return failedValidation;
    }

}
