package com.fluxtion.extension.functional.helpers;

/**
 *
 * @author gregp
 */


public class StaticFunctions {
    
    private static int ID;
    private int myId;
    
    public StaticFunctions() {
        myId = ID++;
    }
    
    
    
    public static void push(String a){
        
    }
    
    public static String add(String a){
        return a;
    }
    
    public static String intToString(Integer i){
        return "";
    }
    
    public void instanceMethod(String s){
        
    }

    @Override
    public String toString() {
        return "StaticFunctions{" + "myId=" + myId + '}';
    }
    
}
