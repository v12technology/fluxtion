/*
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxtion.example.core.dependencyinjection.propertyscalar;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.example.shared.MyEvent;

/**
 *
 * @author V12 Technology Ltd.
 */
public class PropertyHandler {
    //final properties
    private final boolean booleanFinalProp;
    private final byte byteFinalProp;
    private final char charFinalProp;
    private final short shortFinalProp;
    private final float floatFinalProp;
    private final int intFinalProp;
    private final double doubleFinalProp;
    private final long longFinalProp;
    private final String stringFinalProp;
    //public properties
    public boolean booleanPublicProp;
    public byte bytePublicProp;
    public char charPublicProp;
    public short shortPublicProp;
    public float floatPublicProp;
    public int intPublicProp;
    public double doublePublicProp;
    public long longPublicProp;
    public String stringPublicProp;
    //public properties
    private boolean booleanBeanProp;
    private byte byteBeanProp;
    private char charBeanProp;
    private short shortBeanProp;
    private float floatBeanProp;
    private int intBeanProp;
    private double doubleBeanProp;
    private long longBeanProp;
    private String stringBeanProp;
    //transient properties - ignored
    public boolean booleanTransientProp;
    public byte byteTransientProp;
    public char charTransientProp;
    public short shortTransientProp;
    public float floatTransientProp;
    public int intTransientProp;
    public double doubleTransientProp;
    public long longTransientProp;
    public String stringTransientProp;

    public PropertyHandler(boolean booleanFinalProp, byte byteFinalProp, char charFinalProp, short shortFinalProp, float floatFinalProp, int intFinalProp, double doubleFinalProp, long longFinalProp, String stringFinalProp) {
        this.booleanFinalProp = booleanFinalProp;
        this.byteFinalProp = byteFinalProp;
        this.charFinalProp = charFinalProp;
        this.shortFinalProp = shortFinalProp;
        this.floatFinalProp = floatFinalProp;
        this.intFinalProp = intFinalProp;
        this.doubleFinalProp = doubleFinalProp;
        this.longFinalProp = longFinalProp;
        this.stringFinalProp = stringFinalProp;
    }

    public boolean isBooleanBeanProp() {
        return booleanBeanProp;
    }

    public void setBooleanBeanProp(boolean booleanBeanProp) {
        this.booleanBeanProp = booleanBeanProp;
    }
    
    public byte getByteBeanProp() {
        return byteBeanProp;
    }

    public void setByteBeanProp(byte byteBeanProp) {
        this.byteBeanProp = byteBeanProp;
    }

    public char getCharBeanProp() {
        return charBeanProp;
    }

    public void setCharBeanProp(char charBeanProp) {
        this.charBeanProp = charBeanProp;
    }

    public short getShortBeanProp() {
        return shortBeanProp;
    }

    public void setShortBeanProp(short shortBeanProp) {
        this.shortBeanProp = shortBeanProp;
    }

    public float getFloatBeanProp() {
        return floatBeanProp;
    }

    public void setFloatBeanProp(float floatBeanProp) {
        this.floatBeanProp = floatBeanProp;
    }

    public int getIntBeanProp() {
        return intBeanProp;
    }

    public void setIntBeanProp(int intBeanProp) {
        this.intBeanProp = intBeanProp;
    }

    public double getDoubleBeanProp() {
        return doubleBeanProp;
    }

    public void setDoubleBeanProp(double doubleBeanProp) {
        this.doubleBeanProp = doubleBeanProp;
    }

    public long getLongBeanProp() {
        return longBeanProp;
    }

    public void setLongBeanProp(long longBeanProp) {
        this.longBeanProp = longBeanProp;
    }

    public String getStringBeanProp() {
        return stringBeanProp;
    }

    public void setStringBeanProp(String stringBeanProp) {
        this.stringBeanProp = stringBeanProp;
    }
    
    @EventHandler
    public void myEvent(MyEvent event){
        
    }
    
    @Initialise
    public void init(){
        //calculate and set any derived properties here
    }
}
