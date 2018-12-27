/* 
 *  Copyright (C) [2016]-[2017] V12 Technology Limited
 *  
 *  This software is subject to the terms and conditions of its EULA, defined in the
 *  file "LICENCE.txt" and distributed with this software. All information contained
 *  herein is, and remains the property of V12 Technology Limited and its licensors, 
 *  if any. This source code may be protected by patents and patents pending and is 
 *  also protected by trade secret and copyright law. Dissemination or reproduction 
 *  of this material is strictly forbidden unless prior written permission is 
 *  obtained from V12 Technology Limited.  
 */
package com.fluxtion.extension.functional.group;

/**
 *
 * @author Greg Higgins
 */
public class TraderPosition {
    
    public static final Class<TraderPosition> TRADER_POSITION = TraderPosition.class;
    public String name;
    public String ccyPair;
    public double dealtVolume;
    public double contraVolume;
    public double maxDealtVolume;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCcyPair() {
        return ccyPair;
    }

    public void setCcyPair(String ccyPair) {
        this.ccyPair = ccyPair;
    }

    public double getDealtVolume() {
        return dealtVolume;
    }

    public void setDealtVolume(double dealtVolume) {
        this.dealtVolume = dealtVolume;
    }

    public double getContraVolume() {
        return contraVolume;
    }

    public void setContraVolume(double contraVolume) {
        this.contraVolume = contraVolume;
    }

    public double getMaxDealtVolume() {
        return maxDealtVolume;
    }

    public void setMaxDealtVolume(double maxDealtVolume) {
        this.maxDealtVolume = maxDealtVolume;
    }

    @Override
    public String toString() {
        return "TraderPosition{" + "name=" + name + ", ccyPair=" + ccyPair + ", dealtVolume=" + dealtVolume + ", contraVolume=" + contraVolume + ", maxDealtVolume=" + maxDealtVolume + '}';
    }

}
