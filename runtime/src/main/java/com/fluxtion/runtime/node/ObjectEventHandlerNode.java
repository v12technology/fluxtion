/*
 * SPDX-FileCopyrightText: © 2025 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package com.fluxtion.runtime.node;

import com.fluxtion.runtime.annotations.OnEventHandler;

public class ObjectEventHandlerNode extends NamedBaseNode implements LifecycleNode {

    public ObjectEventHandlerNode() {
        setName("allEventHandler");
    }

    @OnEventHandler
    public final boolean onEvent(Object event) {
        return handleEvent(event);
    }

    protected boolean handleEvent(Object event) {
        return true;
    }
}
