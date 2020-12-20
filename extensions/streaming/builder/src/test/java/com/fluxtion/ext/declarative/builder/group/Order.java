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
package com.fluxtion.ext.declarative.builder.group;

/**
 * Represents a simple FX order
 *
 * @author Greg Higgins
 */
public class Order {

    public static final Class<Order> ORDER = Order.class;
    
    public Order(int id, String ccyPair, int size) {
        this.id = id;
        this.ccyPair = ccyPair;
        this.size = size;
    }

    public Order() {
    }

    private int id;
    private String ccyPair;
    private int size;

    public int getId() {
        return id;
    }

    public String getCcyPair() {
        return ccyPair;
    }

    public int getSize() {
        return size;
    }

    public double getSizeDouble() {
        return size;
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", ccyPair=" + ccyPair + ", size=" + size + '}';
    }

}
