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
package com.fluxtion.ext.streaming.builder.group;

import com.fluxtion.ext.streaming.builder.util.ImportMap;
import java.lang.reflect.Method;
import lombok.Data;

/**
 * meta data defining how to initialise a value in an aggregated row.
 * 
 * @author Greg Higgins
 */
@Data
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
}
