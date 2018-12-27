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
package com.fluxtion.extension.declarative.builder.group;

import com.fluxtion.extension.declarative.api.numeric.NumericFunctionStateful;
import com.fluxtion.extension.declarative.builder.util.ImportMap;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * meta data defining how to initialise a value in an aggregated row.
 * 
 * @author Greg Higgins
 */
public class GroupByInitialiserInfo {
   
    //Source
    private Class sourceClass;
    private Method sourceMethod;
    private String sourceClassName;
    private String sourceCalcMethodName;
    private String sourceInstanceId;
    //Target
    private Class targetClass;
    private Method targetMethod;
    private String targetArgType;
    private String targetClassName;
    private String targetCalcMethodName;
    private String targetInstanceId;
    
    public final ImportMap importMap;
    private String id;

    public GroupByInitialiserInfo(ImportMap importMap) {
        this.importMap = importMap;
    }
    
    
    public void setSource(Class clazz, Method method, String id){
        sourceClass = clazz;
        sourceMethod = method;
        sourceInstanceId = id;
        sourceClassName = importMap.addImport(clazz);
        sourceCalcMethodName = method.getName();
    }
    
    public void setTarget(Class clazz, Method method, String id){
        targetClass = clazz;
        targetMethod = method;
        targetInstanceId = id;
        targetClassName = importMap.addImport(clazz);
        targetCalcMethodName = method.getName();
        targetArgType = method.getParameterTypes()[0].getName();
    }
    
    public String getInitialiseFunction(){
        String f = targetInstanceId + "." + targetCalcMethodName + "("
                + "(" + targetArgType + ")" + sourceInstanceId + "." + sourceCalcMethodName + "())";
        return f;
    }

    public Class getSourceClass() {
        return sourceClass;
    }

    public Method getSourceMethod() {
        return sourceMethod;
    }

    public String getSourceClassName() {
        return sourceClassName;
    }

    public String getSourceCalcMethodName() {
        return sourceCalcMethodName;
    }

    public String getSourceInstanceId() {
        return sourceInstanceId;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public String getTargetClassName() {
        return targetClassName;
    }

    public String getTargetCalcMethodName() {
        return targetCalcMethodName;
    }

    public String getTargetInstanceId() {
        return targetInstanceId;
    }

    public ImportMap getImportMap() {
        return importMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
