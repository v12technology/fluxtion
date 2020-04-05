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
package com.fluxtion.ext.streaming.builder.util;

import lombok.Data;

/**
 * java bean classes for interaction with velocity
 */
@Data
public class SourceInfo {

    public String wrapperType;
    public String type;
    public String id;
    public boolean constant;
    public boolean wrapper;

    public SourceInfo(String type, String id) {
        this.type = type;
        this.id = id;
    }
}
