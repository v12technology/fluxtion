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
package com.fluxtion.extension.functional.push;

/**
 *
 * @author greg
 */
public class SaleItem {
    private transient int quantity;
    private transient double cost;

    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "SaleItem{" + "quantity=" + quantity + ", cost=" + cost + '}';
    }
    
}
