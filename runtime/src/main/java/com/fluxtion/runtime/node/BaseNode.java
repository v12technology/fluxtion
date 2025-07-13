/*
 * SPDX-FileCopyrightText: © 2025 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package com.fluxtion.runtime.node;

import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.EventProcessorContextListener;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.builder.FluxtionIgnore;
import com.fluxtion.runtime.audit.EventLogNode;
import lombok.Getter;
import lombok.Setter;

public class BaseNode extends EventLogNode implements EventProcessorContextListener {

    @Getter
    protected EventProcessorContext context;
    @Getter
    @Setter
    @FluxtionIgnore
    private String name;

    @Initialise
    public final void init() {
        name = context.getNodeNameLookup().lookupInstanceName(this);
        auditLog.info("init", name);
        _initialise();
    }

    protected void _initialise() {
    }

    @Override
    public final void currentContext(EventProcessorContext eventProcessorContext) {
        this.context = eventProcessorContext;
    }
}
