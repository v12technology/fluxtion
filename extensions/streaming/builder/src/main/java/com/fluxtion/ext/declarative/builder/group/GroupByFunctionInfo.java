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
package com.fluxtion.ext.declarative.builder.group;

import com.fluxtion.ext.declarative.api.numeric.NumericFunctionStateful;
import com.fluxtion.ext.declarative.builder.util.ImportMap;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Meta data for a group by aggregate function.
 * 
 * @author Greg Higgins
 */
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
    private boolean staticFunction;
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

    public GroupByFunctionInfo(ImportMap importMap) {
        this.importMap = importMap;
    }
    
    public void setFunction(Class clazz, Method method, String id){
        functionClass = clazz;
        functionCalcMethod = method;
        functionInstanceId = id;
        functionClassName = importMap.addImport(clazz);
        functionCalcMethodName = method.getName();
        functionCalcArgType = method.getParameterTypes()[0].getName();
        functionCalcReturnType = method.getReturnType().getName();
        stateful = NumericFunctionStateful.class.isAssignableFrom(clazz);
        staticFunction = Modifier.isStatic(method.getModifiers());
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
    
    public String getUpdateTarget(){
        
//            double value = instance.aggregateSum2;
//            value = aggregateSum2.calcSum(event.getTradeVolume(), value);
//            target.setTotalVolume((int)value);
//            instance.aggregateSum2 = value;
        String functionId = functionInstanceId;
        if(staticFunction){
            functionId = functionClassName;
        }else if(stateful){
            functionId = "instance." + functionInstanceId+"Function";
        }
//        functionReturnType = (stateful && )
        
        String source = sourceInstanceId==null?"0":sourceInstanceId +  "." + sourceCalcMethodName + "()";

        String a = "\t\t\t" + functionCalcReturnType + " value = instance." + functionInstanceId + ";\n";
        String b = "\t\t\tvalue = " + functionId + "." + functionCalcMethodName + "((" + functionCalcArgType + ")"
                + source
                + ", (" + functionCalcArgType + ")value"
                +    ");\n";
        String c = "\t\t\t" + targetInstanceId + "." + targetCalcMethodName + "((" + targetArgType + ")"
                + "value);\n"
                ;
        String d = "\t\t\tinstance." + functionInstanceId + " = value;";
        return a + b + c + d;
    }

    public Class getFunctionClass() {
        return functionClass;
    }

    public Method getFunctionCalcMethod() {
        return functionCalcMethod;
    }

    public String getFunctionClassName() {
        return functionClassName;
    }

    public String getFunctionCalcMethodName() {
        return functionCalcMethodName;
    }

    public String getFunctionCalcArgType() {
        return functionCalcArgType;
    }

    public String getFunctionInstanceId() {
        return functionInstanceId;
    }

    public boolean isStateful() {
        return stateful;
    }

    public boolean isStateless() {
        return !stateful;
    }

    public boolean isStaticFunction() {
        return staticFunction;
    }

    public boolean isInstanceFunction(){
        return !staticFunction;
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

    @Override
    public String toString() {
        return "GroupByFunctionInfo{\n " + "functionClass=" + functionClass + ",\n functionCalcMethod=" + functionCalcMethod + ",\n functionCalcArgType=" + functionCalcArgType + ",\n functionClassName=" + functionClassName + ",\n functionCalcMethodName=" + functionCalcMethodName + ",\n functionInstanceId=" + functionInstanceId + ",\n stateful=" + stateful + ",\n sourceClass=" + sourceClass + ",\n sourceMethod=" + sourceMethod + ",\n sourceClassName=" + sourceClassName + ",\n sourceCalcMethodName=" + sourceCalcMethodName + ",\n sourceInstanceId=" + sourceInstanceId + ",\n targetClass=" + targetClass + ",\n targetMethod=" + targetMethod + ",\n targetClassName=" + targetClassName + ",\n targetCalcMethodName=" + targetCalcMethodName + ",\n targetInstanceId=" + targetInstanceId + ",\n importMap=" + importMap + '}';
    }
    
}
