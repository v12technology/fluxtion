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
package com.fluxtion.ext.streaming.builder.table;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author gregp
 */
@Data
@NoArgsConstructor
public class JoinedRow  {

    public Object[] tables = new Object[7];

    public JoinedRow(Object[] tables) {
        System.arraycopy(tables, 0, this.tables, 0, tables.length);
    }

    protected JoinedRow cloneRow()  {
        JoinedRow row = new JoinedRow(this.tables);
        return row;
    }
}
