/*
 * SPDX-FileCopyrightText: © 2025 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package com.fluxtion.runtime.node;

import com.fluxtion.runtime.annotations.OnEventHandler;

/**
 * Represents a node in an event-driven architecture that handles generic events.
 * Extends {@link NamedBaseNode} and implements {@link LifecycleNode}, thereby
 * inheriting functionality for named identification and lifecycle management.
 * Automatically sets its name to "allEventHandler" upon creation.
 *
 * <h2>Usage</h2>
 * <ul>
 * <li>This class listens for and handles events through the annotated
 * {@link OnEventHandler} method {@code onEvent}.
 * <li>It provides a mechanism to extend and override event-handling behavior by
 * overriding the {@code handleEvent} method.
 * </ul>
 *
 * <h2>Lifecycle Integration</h2>
 * As part of implementing {@link LifecycleNode}, this class can integrate into
 * a structured application lifecycle, including initialization, starting,
 * stopping, and teardown.
 */
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
