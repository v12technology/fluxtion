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
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
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
        returnType = method.getReturnType().getName();
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

    public void appendParamNumeric(NumericValue numeric, SourceInfo sourceInfo) {
        String getNumber = functionMethod.getParameterTypes()[count].getName() + "Value()";
        paramString += sep + sourceInfo.id + "." + getNumber;
        sep = ", ";
        count++;
    }

    public void appendPreviousResult() {
        paramString = "result";
        sep = ", ";
    }

    public void appendParamLocal(String id, boolean isCast) {
        paramString += sep + cast(isCast) + id;
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
