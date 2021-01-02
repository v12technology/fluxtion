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

import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.Stateful.StatefulNumber;
import com.fluxtion.ext.streaming.builder.util.ImportMap;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import lombok.Data;
import org.apache.commons.lang3.ClassUtils;

/**
 * Meta data for a group by aggregate function.
 * 
 * @author Greg Higgins
 */
@Data
public class GroupByFunctionInfo {
   
    //Function
    private Class functionClass;
    private Method functionCalcMethod;
    private String functionCalcArgType;
    private String functionCalcReturnType;
    private String functionClassName;
    private String functionCalcMethodName;
    private String functionInstanceId;
    private boolean stateful;
    private boolean statefulNonNumeric;
    private boolean statefulNumeric;
    private boolean staticFunction;
    //Source
    private Class sourceClass;
    private Method sourceMethod;
    private String sourceClassName;
    private String sourceCalcMethodName;
    private String sourceInstanceId;
    private boolean sourceThis;//passing the whole event in to function
    //Target
    private Class targetClass;
    private Method targetMethod;
    private String targetArgType;
    private String targetClassName;
    private String targetCalcMethodName;
    private String targetInstanceId;
    
    public final ImportMap importMap;
    private boolean functionSingleArg;
    private boolean functionAcceptsNumber;

    public GroupByFunctionInfo(ImportMap importMap) {
        this.importMap = importMap;
        this.sourceThis = false;
    }
    
    public void setFunction(Class clazz, Method method, String id){
        functionClass = clazz;
        functionCalcMethod = method;
        functionInstanceId = id;
        functionClassName = importMap.addImport(clazz);
        functionCalcMethodName = method.getName();
        functionCalcArgType = method.getParameterTypes()[0].getName();
        functionAcceptsNumber = Number.class.isAssignableFrom(method.getParameterTypes()[0]);
        functionSingleArg = (method.getParameterCount() == 1);
        functionCalcReturnType = method.getReturnType().getCanonicalName();
        stateful = Stateful.class.isAssignableFrom(clazz);
        statefulNumeric = StatefulNumber.class.isAssignableFrom(clazz);
        statefulNonNumeric = stateful & !statefulNumeric;
        staticFunction = Modifier.isStatic(method.getModifiers());
    }
    
    public void setSource(Class clazz, Method method, String id){
        this.sourceThis = false;
        sourceClass = clazz;
        sourceMethod = method;
        sourceInstanceId = id;
        sourceClassName = importMap.addImport(clazz);
        sourceCalcMethodName = method.getName();
    }

    void setSourceThis() {
        sourceThis = true;
    }
    
    public void setTarget(Class clazz, Method method, String id){
        targetClass = clazz;
        targetMethod = method;
        targetInstanceId = id;
        targetClassName = importMap.addImport(clazz);
        targetCalcMethodName = method.getName();
        targetArgType = method.getParameterTypes()[0].getName();
    }
    
    public String getUpdateTarget(){
        String functionId = functionInstanceId;
        if(staticFunction){
            functionId = functionClassName;
        }else {
            functionId = "instance." + functionInstanceId+"Function";
        }
        String source = sourceInstanceId==null?"0":sourceInstanceId +  "." + sourceCalcMethodName + "()";
        if(sourceThis){
            source = "event";
        }
        if(functionAcceptsNumber){
            source = "number.set(" + source +")";
        }
        String a = "\t\t\t" + functionCalcReturnType + " value = instance." + functionInstanceId + ";\n";
        String b = "\t\t\tvalue = " + functionId + "." + functionCalcMethodName + "((" + functionCalcArgType + ")"
                + source
                + ", (" + functionCalcReturnType + ")value"
                +    ");\n";
        if(functionSingleArg){
            b = "\t\t\tvalue = " + functionId + "." + functionCalcMethodName + "((" + functionCalcArgType + ")"
                + source
                +    ");\n";
        }
        String c = "\t\t\t" + targetInstanceId + "." + targetCalcMethodName + "((" + targetArgType + ")"
                + "value);\n"
                ;
        String d = "\t\t\tinstance." + functionInstanceId + " = value;";
        return a + b + c + d;
    }
    
    public String getCopyToTarget(){
        String c =  "newInstance.target." + targetCalcMethodName + "((" + targetArgType + ")"
                + "newInstance." + functionInstanceId + ")";
        return c ;
    }

}
