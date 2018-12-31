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
package com.fluxtion.ext.futext.builder.csv;

import com.fluxtion.extension.declarative.builder.util.ImportMap;
import com.fluxtion.ext.futext.api.ascii.Conversion;
import com.fluxtion.ext.futext.api.csv.ColumnValidator;
import java.lang.reflect.Method;

/**
 *
 * @author Greg Higgins
 */
public class CsvPushFunctionInfo {

    //Source
    private int fieldIndex;
    private String fieldName;
    private boolean duplicateField = false;
    private boolean fixedWidth = false;
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

    public Class getTargetClass() {
        return targetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public String getTargetArgType() {
        return targetArgType;
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

    public int getFieldIndex() {
        return fieldIndex;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isIndexField() {
        return indexField;
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
    
    public boolean isTrim() {
        return trim;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public boolean isDuplicateField() {
        return duplicateField;
    }

    public void setDuplicateField(boolean duplicateField) {
        this.duplicateField = duplicateField;
    }

    public String getConverterInstanceId() {
        return converterInstanceId;
    }

    public void setConverterInstanceId(String converterInstanceId) {
        this.converterInstanceId = converterInstanceId;
    }

    public boolean isConverterInstance(){
        return getConverterInstanceId()!=null;
    }

    public Object getConverterInstance() {
        return converterInstance;
    }

    public String getConverterClass() {
        return converterClass;
    }

    public boolean isFixedWidth() {
        return fixedWidth;
    }

    public int getFixedStart() {
        return fixedStart;
    }

    public int getFixedLen() {
        return fixedLen;
    }

    public int getFixedEnd() {
        return fixedEnd;
    }
    
}
