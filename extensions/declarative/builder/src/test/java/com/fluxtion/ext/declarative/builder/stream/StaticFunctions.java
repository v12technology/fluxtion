package com.fluxtion.ext.declarative.builder.stream;

/**
 *
 * @author V12 Technology Ltd.
 */


public class StaticFunctions {
 
    public static int statStr2Int(String val){
        try {
            return (int) Double.parseDouble(val);
        } catch (Exception e) {
            return -1;
        }
    }
}
