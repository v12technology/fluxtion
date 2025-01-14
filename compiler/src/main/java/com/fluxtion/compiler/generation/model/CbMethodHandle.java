/*
 * Copyright (c) 2019-2025 gregory higgins.
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
package com.fluxtion.compiler.generation.model;

import com.fluxtion.runtime.annotations.AfterTrigger;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.dataflow.ParallelFunction;
import lombok.Getter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * @author Greg Higgins
 */
public class CbMethodHandle {

    public enum CallBackType {TRIGGER, EVENT_HANDLER, EXPORT_FUNCTION;}

    /**
     * The callback method.
     */
    @Getter
    public final Method method;
    /**
     * the instance the method will operate on.
     */
    @Getter
    public final Object instance;
    /**
     * the variable name of the instance in the SEP.
     */
    @Getter
    public final String variableName;

    /**
     * the parameter type of the callback - can be null
     */
    @Getter
    public final Class<?> parameterClass;

    /**
     * indicates is an {@link OnEventHandler} method
     */
    public final boolean isEventHandler;
    /**
     * Is a multi arg event handler
     */
    @Getter
    private final boolean exportedHandler;

    @Getter
    public final boolean postEventHandler;

    @Getter
    public final boolean invertedDirtyHandler;

    @Getter
    private final boolean guardedParent;

    @Getter
    private final boolean noPropagateEventHandler;
    private final boolean failBuildOnUnguardedTrigger;
    @Getter
    private final boolean forkExecution;

    public CbMethodHandle(Method method, Object instance, String variableName) {
        this(method, instance, variableName, null, false, false);
    }

    public CbMethodHandle(Method method, Object instance, String variableName, Class<?> parameterClass, boolean isEventHandler, boolean exportedHandler) {
        this.method = method;
        this.instance = instance;
        this.variableName = variableName;
        this.parameterClass = parameterClass;
        this.isEventHandler = isEventHandler;
        this.postEventHandler = method.getAnnotation(AfterTrigger.class) != null;
        OnTrigger onTriggerAnnotation = method.getAnnotation(OnTrigger.class);
        OnParentUpdate onParentUpdateAnnotation = method.getAnnotation(OnParentUpdate.class);
        OnEventHandler onEventHandlerAnnotation = method.getAnnotation(OnEventHandler.class);
        this.exportedHandler = exportedHandler;
        this.invertedDirtyHandler = onTriggerAnnotation != null && !onTriggerAnnotation.dirty();
        boolean parallel = (instance instanceof ParallelFunction) ? ((ParallelFunction) instance).parallelCandidate() : false;
        this.forkExecution = parallel || onTriggerAnnotation != null && onTriggerAnnotation.parallelExecution();
        this.failBuildOnUnguardedTrigger = onTriggerAnnotation != null && onTriggerAnnotation.failBuildIfMissingBooleanReturn();
        this.guardedParent = onParentUpdateAnnotation != null && onParentUpdateAnnotation.guarded();
        this.noPropagateEventHandler = onEventHandlerAnnotation != null && !onEventHandlerAnnotation.propagate();
    }

    public boolean isEventHandler() {
        return isEventHandler;
    }


    public String getMethodTarget() {
        if (Modifier.isStatic(getMethod().getModifiers())) {
            return instance.getClass().getSimpleName();
        }
        return variableName;
    }

    public String invokeLambdaString() {
        return getMethodTarget() + "::" + getMethod().getName();
    }

    public String forkVariableName() {
        return "fork_" + getVariableName();
    }

    @Override
    public String toString() {
        return "CbMethodHandle{" +
                "method=" + method +
                ", instance=" + instance +
                ", variableName='" + variableName + '\'' +
                ", parameterClass=" + parameterClass +
                ", isEventHandler=" + isEventHandler +
                ", isExportHandler=" + exportedHandler +
                ", isPostEventHandler=" + postEventHandler +
                ", isInvertedDirtyHandler=" + invertedDirtyHandler +
                ", isGuardedParent=" + guardedParent +
                ", isNoPropagateEventHandler=" + noPropagateEventHandler +
                ", failBuildOnUnguardedTrigger=" + failBuildOnUnguardedTrigger +
                ", forkExecution=" + forkExecution +
                '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.method);
        hash = 23 * hash + Objects.hashCode(this.instance);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CbMethodHandle other = (CbMethodHandle) obj;
        if (!Objects.equals(this.method, other.method)) {
            return false;
        }
        return Objects.equals(this.instance, other.instance);
    }

    public boolean failBuildOnUnguardedTrigger() {
        return failBuildOnUnguardedTrigger;
    }
}
