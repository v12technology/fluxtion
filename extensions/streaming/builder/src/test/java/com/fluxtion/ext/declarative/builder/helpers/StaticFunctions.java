package com.fluxtion.ext.declarative.builder.helpers;

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
    
    public void setint(int i){
        
    }
    
    public static String add(String a){
        return a;
    }
    
    public static String intToString(Integer i){
        return "";
    }
    
    public void instanceMethod(String s){
        
    }
    
    public int getInt(){
        return 1;
    }

    @Override
    public String toString() {
        return "StaticFunctions{" + "myId=" + myId + '}';
    }
    
}
