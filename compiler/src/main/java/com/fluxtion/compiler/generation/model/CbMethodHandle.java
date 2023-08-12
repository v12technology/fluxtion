/*
 * Copyright (c) 2019, V12 Technology Ltd.
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

import com.fluxtion.runtime.annotations.*;
import com.fluxtion.runtime.dataflow.ParallelFunction;

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
    public final Method method;
    /**
     * the instance the method will operate on.
     */
    public final Object instance;
    /**
     * the variable name of the instance in the SEP.
     */
    public final String variableName;

    /**
     * the parameter type of the callback - can be null
     */
    public final Class<?> parameterClass;

    /**
     * indicates is an {@link OnEventHandler} method
     */
    public final boolean isEventHandler;
    /**
     * Is a multi arg event handler
     */
    private final boolean exportedHandler;

    public final boolean isPostEventHandler;

    public final boolean isInvertedDirtyHandler;

    private final boolean isGuardedParent;

    private final boolean isNoPropagateEventHandler;
    private final boolean failBuildOnUnguardedTrigger;
    private final boolean forkExecution;
    private final boolean isNoPropagateFunction;

    public CbMethodHandle(Method method, Object instance, String variableName) {
        this(method, instance, variableName, null, false, false);
    }

    public CbMethodHandle(Method method, Object instance, String variableName, Class<?> parameterClass, boolean isEventHandler, boolean exportedHandler) {
        this.method = method;
        this.instance = instance;
        this.variableName = variableName;
        this.parameterClass = parameterClass;
        this.isEventHandler = isEventHandler;
        this.isPostEventHandler = method.getAnnotation(AfterTrigger.class) != null;
        OnTrigger onTriggerAnnotation = method.getAnnotation(OnTrigger.class);
        OnParentUpdate onParentUpdateAnnotation = method.getAnnotation(OnParentUpdate.class);
        OnEventHandler onEventHandlerAnnotation = method.getAnnotation(OnEventHandler.class);
        NoPropagateFunction noPropagateFunction = method.getAnnotation(NoPropagateFunction.class);
        this.exportedHandler = exportedHandler;
        this.isInvertedDirtyHandler = onTriggerAnnotation != null && !onTriggerAnnotation.dirty();
        boolean parallel = (instance instanceof ParallelFunction) ? ((ParallelFunction) instance).parallelCandidate() : false;
        this.forkExecution = parallel || onTriggerAnnotation != null && onTriggerAnnotation.parallelExecution();
        this.failBuildOnUnguardedTrigger = onTriggerAnnotation != null && onTriggerAnnotation.failBuildIfNotGuarded();
        this.isGuardedParent = onParentUpdateAnnotation != null && onParentUpdateAnnotation.guarded();
        this.isNoPropagateEventHandler = onEventHandlerAnnotation != null && !onEventHandlerAnnotation.propagate();
        this.isNoPropagateFunction = noPropagateFunction != null;
    }

    public Method getMethod() {
        return method;
    }

    public Object getInstance() {
        return instance;
    }

    public String getVariableName() {
        return variableName;
    }

    public Class<?> getParameterClass() {
        return parameterClass;
    }

    public boolean isEventHandler() {
        return isEventHandler;
    }

    public boolean isNoPropagateEventHandler() {
        return isNoPropagateEventHandler;
    }

    public boolean isPostEventHandler() {
        return isPostEventHandler;
    }

    public boolean isInvertedDirtyHandler() {
        return isInvertedDirtyHandler;
    }

    public boolean isGuardedParent() {
        return isGuardedParent;
    }

    public boolean isForkExecution() {
        return forkExecution;
    }

    public boolean isNoPropagateFunction() {
        return isNoPropagateFunction;
    }

    public boolean isExportedHandler() {
        return exportedHandler;
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
                ", isPostEventHandler=" + isPostEventHandler +
                ", isInvertedDirtyHandler=" + isInvertedDirtyHandler +
                ", isGuardedParent=" + isGuardedParent +
                ", isNoPropagateEventHandler=" + isNoPropagateEventHandler +
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
