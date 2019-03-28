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
package com.fluxtion.ext.declarative.builder.util;

import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.api.EventWrapper;
import com.fluxtion.api.event.Event;
import java.lang.reflect.Method;
import javax.lang.model.type.TypeKind;

/**
 *
 * @author Greg Higgins
 */
public class FunctionInfo {

    public String paramString;
    public String returnType;
    public Class returnTypeClass;
    public String calculateMethod;
    public String calculateClass;
    public Class calculateClazz;
    public SourceInfo sourceInfo;
    private final Method functionMethod;
    private String sep;
    private int count;
    private ImportMap importMap;

    public FunctionInfo(Method method) {
        this(method, null);
    }

    public FunctionInfo(Method method, ImportMap importMap) {
        functionMethod = method;
        calculateMethod = method.getName();
        calculateClass = importMap == null
                ? method.getDeclaringClass().getCanonicalName()
                : importMap.addImport(method.getDeclaringClass());
        calculateClazz = method.getDeclaringClass();
        returnTypeClass = method.getReturnType();
        returnType = importMap == null
                ? method.getReturnType().getCanonicalName()
                : importMap.addImport(method.getReturnType());
        paramString = "";
        sep = "";
        this.importMap = importMap;
    }

    public String paramTypeByIndex(int index) {
        if (importMap == null) {
            return functionMethod.getParameterTypes()[index].getName();
        }
        return importMap.addImport(functionMethod.getParameterTypes()[index]);//.getName();
    }

    public String cast(boolean cast) {
        String castString = "";
        if (cast) {
            castString = "(" + paramTypeByIndex(count) + ")";
        }
        return castString;
    }

    public void appendParamNumber(Number numeric, SourceInfo sourceInfo) {
        String getNumber = functionMethod.getParameterTypes()[count].getName() + "Value()";
        paramString += sep + sourceInfo.id + "." + getNumber;
        sep = ", ";
        count++;
    }

    public void appendParamNumber(String name) {
        String getNumber = functionMethod.getParameterTypes()[count].getName() + "Value()";
        paramString += sep + name + "." + getNumber;
        sep = ", ";
        count++;
    }

    public void appendPreviousResult() {
        paramString = "result";
        sep = ", ";
    }

    private void checkAddPrimitiveAccess() {
        if (functionMethod.getParameterTypes()[count].isPrimitive()) {
            String getNumber = functionMethod.getParameterTypes()[count].getName() + "Value()";
            paramString += "." + getNumber;
        }
    }

    public void appendParamValue(String value, boolean isCast) {
        appendParamValue(value, isCast, false);
    }

    public void appendParamValue(String value, boolean isCast, boolean checkIsNumber) {
        paramString += sep + cast(isCast) + value;
        if (checkIsNumber) {
            checkAddPrimitiveAccess();
        }
        sep = ", ";
        count++;
    }

    public <S> void appendParamLocal(String id, Wrapper<S> handler, boolean isCast) {
        Class<S> eventClazz = handler.eventClass();
        String eventClass = eventClazz.getCanonicalName();
        if (importMap != null) {
            eventClass = importMap.addImport(handler.eventClass());
        }
        paramString += sep + cast(isCast) + "((" + eventClass + ")" + id + ".event())";
        if (!eventClazz.isPrimitive() && Number.class.isAssignableFrom(eventClazz)) {
            checkAddPrimitiveAccess();
        }
        sep = ", ";
        count++;
    }

    public <S> void appendParamLocal(Method sourceMethod, String id, Wrapper<S> handler, boolean isCast) {
        String eventClass = handler.eventClass().getCanonicalName();
        if (importMap != null) {
            eventClass = importMap.addImport(handler.eventClass());
        }
        paramString += sep + cast(isCast) + "((" + eventClass + ")" + id + ".event())." + sourceMethod.getName() + "()";
        if (!sourceMethod.getReturnType().isPrimitive()) {
            checkAddPrimitiveAccess();
        }
        sep = ", ";
        count++;
    }

    public void appendParamLocal(Method sourceMethod, String id, boolean isCast) {
        paramString += sep + cast(isCast) + id + "." + sourceMethod.getName() + "()";
        if (!sourceMethod.getReturnType().isPrimitive()) {
            checkAddPrimitiveAccess();
        }
        sep = ", ";
        count++;
    }

    public void appendParamSource(Method sourceMethod, SourceInfo sourceInfo, boolean isCast) {
        paramString += sep + cast(isCast) + sourceInfo.id + "." + sourceMethod.getName() + "()";
        sep = ", ";
        count++;
    }

    public <S extends Event> void appendParamSource(Method sourceMethod, SourceInfo sourceInfo, EventWrapper<S> handler, boolean isCast) {
        String eventClass = handler.eventClass().getCanonicalName();
        if (importMap != null) {
            eventClass = importMap.addImport(handler.eventClass());
        }
        paramString += sep + cast(isCast) + "((" + eventClass + ")" + sourceInfo.id + ".event())." + sourceMethod.getName() + "()";
        sep = ", ";
        count++;
    }

    public <S> void appendParamSource(Method sourceMethod, SourceInfo sourceInfo, Wrapper<S> handler, boolean isCast) {
        String eventClass = handler.eventClass().getCanonicalName();
        if (importMap != null) {
            eventClass = importMap.addImport(handler.eventClass());
        }
        paramString += sep + cast(isCast) + "((" + eventClass + ")" + sourceInfo.id + ".event())." + sourceMethod.getName() + "()";
        sep = ", ";
        count++;
    }

    public TypeKind getReturnTypeKind() {
        return TypeKind.valueOf(returnType.toUpperCase());
    }

    public Method getFunctionMethod() {
        return functionMethod;
    }

    public String getParamString() {
        return paramString;
    }

    public SourceInfo getSourceInfo() {
        return sourceInfo;
    }
    
    @Override
    public String toString() {
        return "FunctionInfo{" + "paramString=" + paramString + ", returnType=" + returnType + ", returnTypeClass=" + returnTypeClass + ", calculateMethod=" + calculateMethod + ", calculateClass=" + calculateClass + ", functionMethod=" + functionMethod + ", sep=" + sep + ", count=" + count + '}';
    }

    public void appendParamSource(ArraySourceInfo sourceInfo, String INPUT_ARRAY_ELEMENT, boolean isCast) {
        String sourceMethod = sourceInfo.getter;
        paramString += sep + cast(isCast) + INPUT_ARRAY_ELEMENT + "." + sourceMethod + "()";
        sep = ", ";
        count++;
    }

}
