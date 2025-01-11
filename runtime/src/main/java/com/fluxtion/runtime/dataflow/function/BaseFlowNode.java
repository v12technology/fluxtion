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

package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.node.BaseNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

@Getter
@Setter
public abstract class BaseFlowNode<T> extends BaseNode implements TriggeredFlowFunction<T> {

    protected transient boolean overrideUpdateTrigger;
    protected transient boolean overridePublishTrigger;
    protected transient boolean inputStreamTriggered;
    protected transient boolean overrideTriggered;
    protected transient boolean publishTriggered;
    protected transient boolean publishOverrideTriggered;
    protected transient boolean resetTriggered;
    private Object updateTriggerNode;
    private Object publishTriggerNode;
    private Object publishTriggerOverrideNode;
    private Object resetTriggerNode;
    private BooleanSupplier dirtySupplier;

    //    @SuppressWarnings("rawtypes")
    protected List<FlowSupplier<?>> inputs = new ArrayList<>();

    @Initialise
    public final void initialiseEventStream() {
        overrideUpdateTrigger = updateTriggerNode != null;
        overridePublishTrigger = publishTriggerOverrideNode != null;
        dirtySupplier = getContext().getDirtyStateMonitor().dirtySupplier(this);
        initialise();
    }

    protected void initialise() {
        //NO-OP
    }

    protected abstract boolean isStatefulFunction();

    @Override
    public boolean parallelCandidate() {
        return false;
    }

    @OnParentUpdate("inputs")
    public void inputUpdated(FlowSupplier<?> inputEventStream) {
        inputStreamTriggered = !resetTriggered;
    }

    @OnParentUpdate("updateTriggerNode")
    public void updateTriggerNodeUpdated(Object triggerNode) {
        overrideTriggered = true;
    }

    @OnParentUpdate("publishTriggerNode")
    public final void publishTriggerNodeUpdated(Object triggerNode) {
        publishTriggered = true;
    }

    @OnParentUpdate("publishTriggerOverrideNode")
    public final void publishTriggerOverrideNodeUpdated(Object triggerNode) {
        publishOverrideTriggered = true;
    }

    @OnParentUpdate("resetTriggerNode")
    public final void resetTriggerNodeUpdated(Object triggerNode) {
        resetTriggered = true;
        inputStreamTriggered = false;
        if (isStatefulFunction()) resetOperation();
    }

    protected abstract void resetOperation();

    @OnTrigger
    public final boolean trigger() {
        if (executeUpdate()) {
            auditLog.info("invokeTriggerOperation", true);
            triggerOperation();
        } else {
            auditLog.info("invokeTriggerOperation", false);
        }
        return fireEventUpdateNotification();
    }

    protected abstract void triggerOperation();

    /**
     * Checks whether an event notification should be fired to downstream nodes. This can be due to any upstream trigger
     * firing including:
     * <ul>
     *     <li>upstream nodes firing a notification</li>
     *     <li>publish trigger firing</li>
     *     <li>update override firing</li>
     * </ul>
     *
     * @return flag indicating fire a notification to child nodes for any upstream change
     */
    protected boolean fireEventUpdateNotification() {
        boolean fireNotification = (!overridePublishTrigger && !overrideUpdateTrigger && inputStreamTriggered)
                | (!overridePublishTrigger && overrideTriggered)
                | publishOverrideTriggered
                | publishTriggered
                | resetTriggered;
        overrideTriggered = false;
        publishTriggered = false;
        publishOverrideTriggered = false;
        resetTriggered = false;
        inputStreamTriggered = false;
        auditLog.info("fireNotification", fireNotification);
        return fireNotification && get() != null;
    }

    /**
     * Checks whether an event notification should be fired to downstream nodes due to:
     * <ul>
     *     <li>upstream nodes firing a notification</li>
     *     <li>update override firing</li>
     * </ul>
     * Requests from publish trigger are not included in this flag
     *
     * @return flag indicating fire a notification to child nodes for an upstream update change, not publish trigger
     */
    protected boolean executeUpdate() {
        return (!overrideUpdateTrigger && inputStreamTriggered) | overrideTriggered;
    }

    @Override
    public void parallel() {

    }

    @Override
    public boolean hasChanged() {
        return dirtySupplier.getAsBoolean();
    }
}
