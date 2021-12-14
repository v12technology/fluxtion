package com.fluxtion.runtim.stream;

public interface StreamAccessories {

    static void println(Object message){
        System.out.println(message);
    }

    static Integer boxInt(int input){
        return input;
    }

    static Double boxDouble(double input){
        return input;
    }

    static Long boxLong(long input){
        return input;
    }
//    public static class DeDupeInt{}
}
