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
package com.fluxtion.ext.text.api.event;

import com.fluxtion.api.SepContext;
import com.fluxtion.ext.streaming.api.IntFilterEventHandler;
import com.fluxtion.ext.streaming.api.Wrapper;

/**
 *
 * @author gregp
 */
public class EofEvent {

    public static final EofEvent EOF = new EofEvent();

    /**
     * Utility method for creating a node that listens to {@link EofEvent}.
     * Synonymous with: {@code EventSelect.select(EofEvent.class);}.
     * 
     * @see EventSelect
     *
     * @return
     */
    public static Wrapper<EofEvent> eofTrigger() {
        Wrapper<EofEvent> handler = new IntFilterEventHandler(EofEvent.class);
        return SepContext.service().addOrReuse(handler);
    }

}
