/*
 * Copyright (c) 2025 gregory higgins.
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

package com.fluxtion.runtime.util;

import lombok.Data;

@Data
public class MyCopy implements Copyable<MyCopy> {
    private int value;

    @Override
    public MyCopy clone() {
        MyCopy myCopy = new MyCopy();
        myCopy.copyFrom(this);
        return null;
    }

    @Override
    public <S extends MyCopy> MyCopy copyFrom(S from) {
        this.setValue(from.getValue());
        return this;
    }
}
