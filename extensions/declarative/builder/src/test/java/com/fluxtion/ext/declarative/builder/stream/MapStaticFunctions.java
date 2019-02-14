package com.fluxtion.ext.declarative.builder.stream;

import javafx.util.Pair;

/**
 *
 * @author V12 Technology Ltd.
 */
public class MapStaticFunctions {

    public static int statStr2Int(String val) {
        try {
            return (int) Double.parseDouble(val);
        } catch (Exception e) {
            return -1;
        }
    }

    public static Number statStr2Number(String val) {
        try {
            return (int) Double.parseDouble(val);
        } catch (Exception e) {
            return -1;
        }
    }

    public static boolean statStr2Boolean(String b) {
        try {
            return Boolean.valueOf(b);
        } catch (Exception e) {
            return false;
        }
    }
    
    
        //From primitve to String
    public static String number2String(Number i) {
        try {
            return i.doubleValue()+ "";
        } catch (Exception e) {
            return "";
        }
    }

    public static String int2String(int i) {
        try {
            return i + "";
        } catch (Exception e) {
            return "";
        }
    }

    public static String double2String(double i) {
        try {
            return i + "";
        } catch (Exception e) {
            return "";
        }
    }

    public static String boolean2String(boolean i) {
        try {
            return i + "";
        } catch (Exception e) {
            return "";
        }
    }
    
    //Ref to Ref
    public static Pair<String, Integer> int2Pair(int val){
        return new Pair<>(int2String(val), val);
    }
    
}
