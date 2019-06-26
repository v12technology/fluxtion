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
package com.fluxtion.ext.text.builder.csv;

import com.fluxtion.ext.streaming.builder.util.ImportMap;
import com.fluxtion.ext.text.api.ascii.Conversion;
import java.lang.reflect.Method;
import lombok.Data;

/**
 *
 * @author Greg Higgins
 */
@Data
public class CsvPushFunctionInfo {

    //Source
    private int fieldIndex;
    private String fieldName;
    private boolean duplicateField = false;
    private boolean fixedWidth = false;
    private boolean mandatory = true;
    private int fixedStart;
    private int fixedLen;
    private int fixedEnd;
    
    //Target
    private Class targetClass;
    private Method targetMethod;
    private String targetArgType;
    private String targetClassName;
    private String targetCalcMethodName;
    private String targetInstanceId;
    private String targetGetMethod;
    //mapping info
    private boolean trim = false;
    //converter

    public final ImportMap importMap;
    private boolean indexField;
    private String fieldIdentifier;
    //converter
    private String converterMethod;
    private String converterInstanceId;
    private Object converterInstance;
    private String converterClass;
    //validator
    private String validatorMethod;
    
    public CsvPushFunctionInfo(ImportMap importMap) {
        this.importMap = importMap;
    }

    public void setSourceColIndex(int colIndex) {
        this.fieldIndex = colIndex;
        indexField = true;
    }

    public void setSourceFieldName(String fieldName) {
        this.fieldName = fieldName;
        this.fieldIndex = -1;
        indexField = false;
    }
    
    public void setSourceFixedField(int startIndex, int length){
        this.fixedStart = startIndex;
        this.fixedLen = length;
        this.fixedEnd = fixedStart + fixedLen;
        this.fieldIndex = startIndex;
        fixedWidth = true;
    }

    public void setTarget(Class clazz, Method method, String id) {
        targetClass = clazz;
        targetMethod = method;
        targetInstanceId = id;
        targetClassName = importMap.addImport(clazz);
        targetCalcMethodName = method.getName();
        targetArgType = importMap.addImport(method.getParameterTypes()[0]);
        getUpdateTarget2();
    }

    public void setConverter(Method method) {
        converterMethod = method.getName();
    }
    
    public void setValidator(String validatorId, Method targetGetMethod){
        validatorMethod = validatorId;
        this.targetGetMethod = targetGetMethod.getName();
    }
    
    public void setConverter(String instanceId, Method method, Object converterInstance){
        this.converterInstanceId = instanceId;
        this.converterInstance = converterInstance;
        this.converterClass = importMap.addImport(converterInstance.getClass());
        converterMethod = converterInstanceId + "." + method.getName();
    }
    
    public String getValidate(){
        return validatorMethod + ".validate(" + targetInstanceId + "." + targetGetMethod + "(), validationBuffer)";
    }
    
    public boolean isValidated(){
        return validatorMethod!=null;
    }

    public String getUpdateTarget2() {

        String conversion = targetCalcMethodName;
        boolean addConversion = true;

        if (converterMethod != null) {
            addConversion = false;
            conversion = converterMethod + "(" + targetCalcMethodName + ")";
        } else {
            switch (targetArgType) {
                case "String":
                    conversion += ".toString()";
                    addConversion = false;
                    break;
                case "CharSequence":
                case "StringBuilder":
                    addConversion = false;
                    break;
                case "double":
                    conversion = "atod(" + targetCalcMethodName + ")";
                    break;
                case "float":
                    conversion = "(float)atod(" + targetCalcMethodName + ")";
                    break;
                case "int":
                    conversion = "atoi(" + targetCalcMethodName + ")";
                    break;
                case "byte":
                    conversion = "(byte)atoi(" + targetCalcMethodName + ")";
                    break;
                case "short":
                    conversion = "(short)atoi(" + targetCalcMethodName + ")";
                    break;
                case "char":
                    conversion = "(char)atoi(" + targetCalcMethodName + ")";
                    break;
                case "long":
                    conversion = "atol(" + targetCalcMethodName + ")";
                    break;
            }
            if (addConversion) {
                importMap.addStaticImport(Conversion.class);
            }
        }

        String a = targetInstanceId + "." + targetCalcMethodName + "("
                + conversion
                + ");";
        return a;
    }

    public boolean isNamedField() {
        return !indexField;
    }

    public String getFieldIdentifier() {
        if(indexField){
            fieldIdentifier = "fieldIndex_" + getFieldIndex();
        }else if(fixedWidth){
            fieldIdentifier = "fixedStart_" + getFieldIndex();
        }else{
            fieldIdentifier = "fieldName_" + getFieldName();
        }
        return fieldIdentifier;
    }
    
    public String getFieldLenIdentifier(){
        return "fixedStart_" + getFieldIndex() + "_Len_" + getFixedLen();
    }
    
    public int getFieldLength(){
        if(fixedWidth){
            return fixedLen;
        }else{
            return -1;
        }
    }
}
