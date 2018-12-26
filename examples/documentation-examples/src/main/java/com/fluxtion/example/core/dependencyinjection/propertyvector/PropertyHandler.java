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
package com.fluxtion.example.core.dependencyinjection.propertyvector;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.example.shared.MyEvent;
import java.util.List;

/**
 *
 * @author V12 Technology Ltd.
 */
public class PropertyHandler {
    //final properties
    private final boolean[] booleanFinalProp;
    private final List<Integer> intFinalProp;
    private final String[] stringFinalProp;
    
    //public properties
    public List<Boolean> booleanPublicProp;
    public int[] intPublicProp;
    public List<String> stringPublicProp;
    
    //bean properties
    private boolean[] booleanBeanProp;
    private List<Integer> intBeanProp;
    private List<String> stringBeanProp;

//    public PropertyHandler(boolean[] booleanFinalProp) {
//        this.booleanFinalProp = booleanFinalProp;
//    }

//    public PropertyHandler(boolean[] booleanFinalProp, String[] stringFinalProp) {
//        this.booleanFinalProp = booleanFinalProp;
//        this.stringFinalProp = stringFinalProp;
//    }

    
    
    public PropertyHandler(boolean[] booleanFinalProp, List<Integer> intFinalProp, String[] stringFinalProp) {
        this.booleanFinalProp = booleanFinalProp;
        this.intFinalProp = intFinalProp;
        this.stringFinalProp = stringFinalProp;
    }

    public boolean[] getBooleanBeanProp() {
        return booleanBeanProp;
    }

    public void setBooleanBeanProp(boolean[] booleanBeanProp) {
        this.booleanBeanProp = booleanBeanProp;
    }

    public List<Integer> getIntBeanProp() {
        return intBeanProp;
    }

    public void setIntBeanProp(List<Integer> intBeanProp) {
        this.intBeanProp = intBeanProp;
    }

    public List<String> getStringBeanProp() {
        return stringBeanProp;
    }

    public void setStringBeanProp(List<String> stringBeanProp) {
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
